package com.example

import scala.annotation.tailrec
import cats.data.Chain
import fs2.Pull
import cats.Functor
import cats.implicits._

sealed trait Regular extends Product with Serializable {
  def |(that: Regular): Regular = Regular.Alternative(this, that)
  def ~(that: Regular): Regular = Regular.Sequence(this, that)
  def ? : Regular = Regular.Optional(this)
  def + : Regular = Regular.Repeated(this)
  def apply(str: String): Boolean = Regular.interpret(this, str)
}

object Regular {
  final case class Alternative(left: Regular, right: Regular) extends Regular
  final case class Sequence(left: Regular, right: Regular) extends Regular
  final case class Optional(underlying: Regular) extends Regular
  final case class Repeated(underlying: Regular) extends Regular
  final case class Char(value: scala.Char) extends Regular

  def apply(c: scala.Char): Regular = Char(c)

  val interpret: (Regular, String) => Boolean = (reg, s) => interpretStream[cats.Id](reg, fs2.Stream.emits(s.toSeq))

  def interpretStream[F[_]: Functor](pattern: Regular, stream: fs2.Stream[F, scala.Char])(implicit S: fs2.Compiler[F, F]): F[Boolean] = {
    case class Result(matches: Boolean, leftover: fs2.Stream[F, scala.Char])

    def go(current: Regular, remainder: fs2.Stream[F, scala.Char]): Pull[F, Nothing, Result] = {
      val noMatch = Result(false, remainder)

      current match {
        case Char(v) =>
          remainder.pull.uncons1.map {
            case None               => noMatch
            case Some((next, tail)) => Result(v == next, tail)
          }

        case Alternative(left, right) =>
          go(left, remainder).flatMap {
            case leftResult if leftResult.matches => Pull.pure(leftResult)
            case _                                => go(right, remainder)
          }

        case Sequence(left, right) =>
          go(left, remainder).flatMap {
            case leftResult if leftResult.matches => go(right, leftResult.leftover)
            case leftResult                       => Pull.pure(leftResult)
          }

        case Optional(underlying) =>
          go(underlying, remainder).map {
            case result if result.matches => result
            case _                        => Result(true, remainder)
          }

        case Repeated(underlying) =>
          // this used to be tailrec
          def loop(previous: Result): Pull[F, Nothing, Result] =
            go(underlying, previous.leftover).flatMap {
              case result if result.matches => loop(result)
              case _                        => Pull.pure(previous)
            }

          loop(noMatch)
      }
    }

    go(pattern, stream)
      .flatMap(Pull.output1(_))
      .stream
      .compile
      .last
      .map(_.getOrElse(???))
      .map(_.matches)
  }

}

object Demo extends App {
  println(
    ((Regular('a') ~ Regular('b') ~ Regular('C')) |
      (Regular('d') ~ Regular('e').+) |
      (Regular('f') ~ Regular('g'))).apply("abC|(de+)|fg")
  )
  println(Regular('a').apply("a"))
  println((Regular('c') | Regular('a')).apply("ad"))
  println((Regular('c') | Regular('a') ~ Regular('d')).apply("ad"))
  println((Regular('c') | Regular('a') ~ Regular('d')).apply("cd"))
}
