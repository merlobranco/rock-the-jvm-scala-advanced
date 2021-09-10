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
  // We can define our own patterns

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
    case Person(n, _) => s"Hi, I am $n"
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

  // Infix patters
  case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")
  val humanDescription = either match {
//    case Or(number, string) => s"$number is written as $string"
    case number Or string => s"$number is written as $string"
  }

  println(humanDescription)

  // Decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "Starting with 1"
  }

  println(vararg)

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    // Basically what we are doing here is turning a list of type A elements into a option of sequence of type A elements in the same order
    // unapplySeq provides support to _*
    // We could implement as well unapply, but unapply does not provides support to _* patterns
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some (Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _) // list.head prepended with whatever I am putting inside
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Cons(4, Empty))))
  val decomposed = myList match {
    case MyList(1, 2, _*) => "Starting with 1, 2"
    case _ => "Something else"
  }

  println(decomposed)

  // Custom types for unapply or unapplySeq
  // We can return custom types, not only Optional as far they are implementing:
  //  isEmpty: Boolean
  //  get: Something

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false
      override def get: String = person.name
    }
  }

  val personWrapper = bob match {
    case PersonWrapper(n) => s"this person name is $n"
    case _ => "An alien"
  }

  println(personWrapper)
}
