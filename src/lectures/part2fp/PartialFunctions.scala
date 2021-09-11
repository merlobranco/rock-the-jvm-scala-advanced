package lectures.part2fp

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int

  val aFussyFunction = (x: Int) =>
    if (x == 1) 23
    else if (x == 2) 45
    else if (x == 5) 89
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  // Here we have a partial function because is applicable to a small domain of Int: {1, 2, 5}
  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 23
    case 2 => 45
    case 5 => 89
  }

  // {1,2,5} => Int

  // Here we have a partial function and above we have total function
  // that cannot be assignable to a partial function like the current one
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 23
    case 2 => 45
    case 5 => 89
  } // Partial function value

  println(aPartialFunction(2))
  //println(aPartialFunction(22323)) this call will fail

  // Partial Function Utilities
  println(aPartialFunction.isDefinedAt(67)) // Checks if the partial function is applicable to the provided argument

  // Lift. It will turn a partial function to a total function: Int => Option[Int]
  val lifted = aPartialFunction.lift
  println(lifted(2))
  println(lifted(98))

  // Chaining partial functions
  val pfChain = aPartialFunction.orElse[Int, Int] { // Here we are defining a function on the fly
    case 45 => 67
  }

  println(pfChain(2))
  println(pfChain(45))

  // Partial functions extends normal functions

  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // Higher Order Functions accept functions as well
  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 56
    case 3 => 67
//    case 4 => 1000 // This case will crash the execution since 4 element is not available on the list
  }

  println(aMappedList)

  /*
    NOTE: Partial Functions ONLY have ONE parameter type
   */

  /*
    Exercises

    1 - Construct a Partial Function yourself (anonymous class)
    2 - Dumb chatbot as a Partial Function
   */

  val divide = new PartialFunction[Int, Int] {
    override def isDefinedAt(x: Int): Boolean = x != 0
    override def apply(x: Int): Int = 42 / x
  }

  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2 || x == 5

    override def apply(x: Int): Int = x match {
      case 1 => 23
      case 2 => 45
      case 5 => 89
    }
  }

//  println(divide(0))
  println(divide(12))

  val chatBot: PartialFunction[String, String] = {
    case "weather" => "Sunny day"
    case "job market" => "Plenty opportunity"
    case "traveling" => " All the places are quite cool"
    case "exit" => {
        System.exit(0)
        "Goodbye"
    }
    case _ => "I did not understand your question"
  }

//  scala.io.Source.stdin.getLines().foreach(line => println("Chat Bot says: " + chatBot(line)))
  scala.io.Source.stdin.getLines().map(chatBot).foreach(println)
}
