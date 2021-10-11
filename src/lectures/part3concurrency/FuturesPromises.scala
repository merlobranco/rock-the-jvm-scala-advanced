package lectures.part3concurrency

import scala.concurrent.Future
import scala.util.{Failure, Success}

// Important for Futures
// ExecutionContext handles thread allocation of Futures
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // Calculates the meaning of life on ANOTHER thread
  } // (global) is available here and passed by the compiler

  println(aFuture.value) // Returns a Option[Try[Int]], not the proper way of calling it

  println("Waiting for the future to finish")
  aFuture.onComplete(t => t match { // Manages a Try[Int]
    case Success(meaningOfLive) => println(s"The meaning of life is $meaningOfLive")
    case Failure(e) => println(s"I have failed with $e")
  })

  // Same as the following partial function
  aFuture.onComplete { // Manages a Try[Int]
    case Success(meaningOfLive) => println(s"The meaning of life is $meaningOfLive")
    case Failure(e) => println(s"I have failed with $e")
  } // The callback represented by the partial function will be call by some thread, we don't know which one

  Thread.sleep(3000)

}
