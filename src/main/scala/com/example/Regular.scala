package com.example

trait Regular {
  def |(that: Regular): Regular
  def ~(that: Regular): Regular
  def ? : Regular
  def + : Regular
  def apply(str: String): Boolean
}

object Regular {
  def apply(c: Char): Regular = ???
}
