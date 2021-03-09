package com.example

import scala.annotation.tailrec
import cats.data.Chain

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

  val interpret: (Regular, String) => Boolean = {
    case class Result(matches: Boolean, leftover: String)

    def go(current: Regular, remainder: String): Result = {
      val noMatch = Result(false, remainder)

      current match {
        case Char(v) =>
          remainder.headOption match {
            case Some(next) => Result(v == next, remainder.tail)
            case None       => noMatch
          }

        case Alternative(left, right) =>
          val leftResult = go(left, remainder)

          if (leftResult.matches)
            leftResult
          else
            go(right, remainder)

        case Sequence(left, right) =>
          val leftResult = go(left, remainder)

          if (leftResult.matches)
            go(right, leftResult.leftover)
          else
            leftResult

        case Optional(underlying) =>
          val result = go(underlying, remainder)

          if (result.matches)
            result
          else
            Result(true, remainder)

        case Repeated(underlying) =>
          @tailrec
          def loop(previous: Result): Result = {
            val result = go(underlying, previous.leftover)

            if (result.matches)
              loop(result)
            else
              previous
          }

          loop(noMatch)
      }
    }

    go(_, _).matches
  }

}

object Demo extends App {
  // println(
  //   ((Regular('a') ~ Regular('b') ~ Regular('C')) |
  //     (Regular('d') ~ Regular('e').+) |
  //     (Regular('f') ~ Regular('g'))).apply("abC|(de+)|fg")
  // )
  println(Regular('a').apply("a"))
  println((Regular('c') | Regular('a') ~ Regular('d')).apply("ad"))
  println((Regular('c') | Regular('a') ~ Regular('d')).apply("cd"))
}
