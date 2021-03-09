package com.example

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

  def interpret(reg: Regular, s: String): Boolean = ???
}

object Demo extends App {
  println(
    ((Regular('a') ~ Regular('b') ~ Regular('C')) |
      (Regular('d') ~ Regular('e').+) |
      (Regular('f') ~ Regular('g'))).apply("abC|(de+)|fg")
  )
}
