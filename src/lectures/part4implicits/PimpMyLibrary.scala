package lectures.part4implicits

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Try

object PimpMyLibrary extends App {
  // Type Enrichment (Pimping) allows to improve classes we don't have access to with additional methods and properties

  // We could use implicit classes for defining the following extra operation
  // 2.isPrime

  // For memory optimization purposes we should extend from AnyVal
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)
    def times(f: => Unit) = 1 to value foreach {_ => f}
    def *[T](l: List[T]): List[T] = {
      var result = List[T]()
      value times {
        result = result ++ l
      }
      result
    }
  }

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = !richInt.isEven
  }

  new RichInt(42).sqrt

  42.isEven // new RichInt(42).isEven

  // Other examples of Type Enrichment
  1 to 10

  3.seconds
  3 seconds

  // The compiler doesn't do multiple implicit searches. It will stop on the RichInt definition
  // 42.isOdd

  /*
    Enrich the String class
    - asInt
    - encrypt
      "John" -> "Lnjp" (2 characters positioned on the right)

    Enrich the Int class
    - times(function)
      3.times(() => ...)
    - *
      3 * List(1,2) => List(1,2,1,2,1,2)
   */

  implicit class RichString(val value: String) extends AnyVal {
    def asInt: Int = Try(value.toInt).getOrElse(-1)
    def encrypt: String = value.map(c => (c.toInt + 2).toChar)
  }

  println("123" asInt)
  println("John" encrypt)

  5 times println("Hello")

  println(3 * List(1,2))

  // "3" / 4
//  implicit def stringToInt(string: String): Int = Integer.valueOf(string)
//  println("6" / 2) // stringToInt("6") / 2

  // Equivalent of implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)
  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // Although the implicit conversion with methods in general are more powerful, they are discouraged

  // Danger zone
  implicit def intToBoolean(i: Int): Boolean = i == 1

  /*
    if (n) do something
    else do something else
   */

  // The problem of using implicit conversion methods is if there is a bug on them is truly difficult to detect or trace
  val aConditionedValue = if (3) "OK" else "Something wrong"
  println(aConditionedValue)
}