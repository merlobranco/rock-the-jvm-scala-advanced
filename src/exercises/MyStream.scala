package exercises

/*
  Streams are special collections where the head is always evaluated always available,
  but the tail is lazily evaluated and available on demand

  naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
  naturals.take(100).foreach(println) // Lazily evaluated stream of the first 100 naturals (finite stream)
  naturals.foreach(println) // Will crash - infinite!
  naturals.map(_ * 2) // Stream of all even numbers (potentially infinite)
 */
abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // Prepend operator
  def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // Concatenate two streams

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // Takes the first n elements out of this stream
  def takeList(n: Int): List[A]
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] = ???
}
