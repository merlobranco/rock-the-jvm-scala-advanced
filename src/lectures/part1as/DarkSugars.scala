package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // Syntax sugar #1: methods with single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  val description = singleArgMethod {
    // Write some complex code
    42
  }

  val aTryInstance = Try { // Java's try {...}
    throw new RuntimeException
  }

  List(1,2,3).map {
    x => x + 1
  }

  // Syntax sugar #2: Single abstract method pattern
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  // Equivalent with lambda expressions
  val aFunkyInstance: Action = (x: Int) => x + 1

  // Example: Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hello, Scala")
  })

  val aSweeterThread = new Thread(() => println("sweet, Scala!"))

  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }
  val anAbstractInstance: AnAbstractType = (a: Int) => println("sweet")

  // Syntax sugar #3: the :: and #:: methods are special

  val prependedList = 2 :: List(3, 4)
  // Should be 2.::(List(3, 4))
  // Instead is List(3, 4).::(2)
  // ?!

  // The associativity of the method is determined by the operators last character
  // If it ends in a colon ':' means right associative
  // If not means left associative, which means it has the normal method behaviour
  // This allows the compiler to write such as operators in reverse order

  1 :: 2 :: 3 :: List(4, 5)
  List(4,5).::(3).::(2).::(1) // Equivalent

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // Actual implementation here
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // Syntax sugar #4: Multi-word method naming
  // (More than a syntax sugar a language feature)

  class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is so sweet!"

  // Syntax sugar #5: Infix types
  class Composite[A, B]
  val composite: Int Composite String = ???

  class -->[A, B]
  val towards: Int --> String = ???

//  trait <[A, B]
//  val lessThan: A < B = ???

  // Syntax sugar #6: update() is very special, much like apply()
  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7)
  // Used in mutable collections
  // remember apply() AND update()

  // Syntax sugar #7: Setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member: Int = internalMember // "Getter"
    def member_=(value: Int): Unit =
      internalMember = value // "Setter"
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // Rewritten as aMutableContainer.member_=(42)

}
