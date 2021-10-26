package lectures.part4implicits

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MagnetPattern extends App {
  /*
    Magnet Pattern is an Use Case of Type Classes that is solving some of the methods created by method overloading
   */

  class P2PRequest
  class P2PResponse
  class Serializer[T]

  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResponse): Int
//    def receive[T](message: T)(implicit serializer: Serializer[T])
    // Context bound reduction
    def receive[T: Serializer](message: T): Int
    def receive[T: Serializer](message: T, statusCode: Int): Int
    def receive(future: Future[P2PRequest]): Int
//    def receive(future: Future[P2PResponse]): Int // Type erasure
    // Lot of overloads
  }

  /*
    Problems related with Method overload

      1) Type erasure
      2) Lifting does not work for all the overloads

        val receiveV = receive _ // ?!

      3) Code duplication
      4) Type inferrence ad default args

        actor.receive(?!)
   */

  /*
    SOLUTION!!! Magnet Pattern
   */

  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    def apply(): Int = {
      // Logic for handling a P2PRequest
      println("Handling P2P request")
      42
    }
  }

  implicit class FromP2PResponse(request: P2PResponse) extends MessageMagnet[Int] {
    def apply(): Int = {
      // Logic for handling a P2PResponse
      println("Handling P2P response")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  /*
    BENEFITS!!!
   */

  // 1) No more type erasure problems!
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  // 2) Lifting works
  trait MathLib {
    def add1(x: Int): Int = x + 1
    def add1(s: String): Int = s.toInt + 1
    // add1 overloads
  }

  // "Magnetize"
  trait AddMagnet {
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x + 1
  }

  implicit class AddString(s: String) extends AddMagnet {
    override def apply(): Int = s.toInt + 1
  }

  val addFV = add1 _
  println(addFV(1))
  println(addFV("3"))

  // But in this case the compiler does not know for which type the receive _ method will apply to (because it has a generic type R)
  val receiveFV = receive _
//  receiveFV(new P2PResponse) // Here the compiler is confused

  /*
    DRAWBACKS
   */

  // 1) Too much verbose
  // 2) Too much difficult to read
  // 3) You cannot name or place default arguments
//  receive()

  // 4) Call by name does not work correctly
  // Prove

  class Handler {
    def handle(s: => String) = {
      println(s)
      println(s)
    }
    // Other overloads
  }

  trait HandleMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet) = magnet()

  implicit class StringHandle(s: => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod(): String = {
    println("Hello, Scala")
    "Magnet"
  }

  handle(sideEffectMethod())

  handle {
    println("Hello, Scala")
    "Magnet" // Only this value will be converted to our magnet class
  }

  // Same as
  handle {
    println("Hello, Scala")
    new StringHandle("Magnet")
  }
}
