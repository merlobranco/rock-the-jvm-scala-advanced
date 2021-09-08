package lectures.part1as

import scala.annotation.tailrec
import scala.math.Ordering.Boolean

object Recap extends App {

  val aCondition: Boolean = false
  val aConditionedVal = if (aCondition) 42 else 65
  // Instructions vs Expressions
  /*
    Instructions: fundamental operation of imperatively languages like Java, Python, C++
    Executed on sequence to do things for programs
    Expressions: In languages like Scala we construct programs through expressions on top of other expressions
  */

  // Compiler infers types for us
  val aCodeBlock = {
    if (aCondition) 54
    56
  }

  // Unit: do not return meaningful values just side effects
  val theUnit = println("Hello, Scala")

  // Functions
  def aFunction(x: Int): Int = x + 1

  // Recursion: stack and tail
  @tailrec
  def factorial (n: Int, accumulator: Int): Int =
    if (n <= 0) accumulator
    else factorial(n-1, accumulator * n)

  // Object-oriented programming
  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog // Subtyping polymorphism

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("Crunch!")
  }

  // Method notations
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog // Natural language

  // Anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("roar!")
  }

  // Generics
  abstract class MyList[+A]

  // Singletons and Companions
  object MyList

  // Case classes
  case class Person(name: String, age: Int)

  // Exceptions and try/catch/finally

//  val throwsException = throw new RuntimeException // Nothing
  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case e: Exception => "I caught and exception"
  } finally {
    println("Some logs")
  }

  // Packaging and imports
  // Scala is more OO than canonical languages like Java and C++ because Scala is designed around Classes and Objects
  // Every piece of code is written in an Object or a Class

  // Functional programming
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  incrementer(1)

  val anonymousIncrementer = (x: Int) => x + 1
  List(1,2,3).map(anonymousIncrementer) // HOF
  // map, flatMap, filter

  // for-comprehension
  val pairs = for {
    num <- List(1,2,3) // if condition (guards)
    char <- List('a', 'b', 'c')
  } yield num + "-" + char

  println(pairs)

  // Scala collections: Seqs, Arrays, Lists, Vectors, Maps Tuples
  val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )

  // "Collections": Options, Try
  val anOption = Some(2)

  // Pattern Matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }
  println(greeting)

  // All the patterns
}
