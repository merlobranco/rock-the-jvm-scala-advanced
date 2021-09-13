package lectures.part2fp

object LazyEvaluation extends App {

  // Lazy delays the evaluation of values until they are actually used
  // Lazy values are evaluated once, but only when they are used for the first time
//  lazy val x: Int = throw new RuntimeException
//  println(x) // Here is when my Error will be triggered
  lazy val x: Int = {
    println("Hello")
    42
  }
  println(x) // The inside println will be evaluated once
  println(x)

  // Examples of implications:
  // Side effects
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if (simpleCondition && lazyCondition) "yes" else "no")

  // We don't see "Boo" because lazyCondition is not evaluated, because is not needed => simpleCondition is false

  // Example in conjunction with call by name ": =>", n will be evaluated 3 times because is called by name
  def byNameMethod(n: => Int): Int = {
    //    n + n + n + 1 // In order to evaluated the value once // This technique is called CALL BY NEED
    lazy val t = n
    t + t + t + 1
  }

  def retrieveMagicValue = {
    // Side effect or a long computation
    println("Waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue))

  //  Filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30lazy = numbers.withFilter(lessThan30) // withFilter is a collection's function that is using lazy vals under the hood
  val gt20lazy = lt30lazy.withFilter(greaterThan20)
  println
  println(gt20lazy)
  gt20lazy.foreach(println) // This will force the filter to take place and we will see the Side effects
                            // But is going to be interesting the order in which the functions will be called

  // For-comprehensions use withFilter with guards
  for {
    a <- List(1,2,3) if a % 2 == 0 // If guards use lazy vals!
  } yield a + 1
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1) // List[Int]

  /*
    Exercise: implement a lazily evaluated, singly linked STREAM of elements
   */

}
