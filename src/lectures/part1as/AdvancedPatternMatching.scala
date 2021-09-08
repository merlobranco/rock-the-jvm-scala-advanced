package lectures.part1as

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"The only element is $head")
    case _ =>
  }

  /*
    - Constants
    - Wildcards
    - Case Classes
    - Tuples
    - Some Special Magic Like Above
   */

  class Person(val name: String, val age: Int)

  // Making my class compatible with Pattern Matching, without making it a case Class

  // 1. Create an Companion Object (Singleton object that could have any name) that implements unapply method

//  object PersonPattern {
  object Person {
    def unapply(person: Person): Option[(String, Int)] = {
      if (person.age < 21) None
      else Some((person.name, person.age))
    }

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
//    case PersonPattern(n, a) => s"Hi, my name is $n and I am $a years old"
    case Person(n, a) => s"Hi, my name is $n and I am $a years old"
    case _ => "I don't know who I am"
  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }

  println(legalStatus)

  /*
    Exercise. Define Pattern Matching Tests for the following scenario
   */

  val n: Int = 8
  val mathProperty = n match {
    case x if x < 10 => "Single digit"
    case x if x % 2 == 0 => "An even number"
    case _ => "no property"
  }

  object even {
//    def unapply(arg: Int): Option[Boolean] =
//      if (arg % 2 == 0) Some(true)
//      else None

    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
//    def unapply(arg: Int): Option[Boolean] =
//      if (arg > -10 && arg < 10) Some(true)
//      else None

    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val mathPropertyFinal = n match {
//    case singleDigit(_) => "Single digit"
//    case even(_) => "An even number"
    case singleDigit() => "Single digit"
    case even() => "An even number"
    case _ => "no property"
  }

  println(mathPropertyFinal)



}
