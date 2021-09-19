package exercises

import scala.annotation.tailrec

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

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc
    else tail.toList(head +: acc)
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new Cons(start, MyStream.from(generator(start))(generator))
}

object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true

  override def head: Nothing = throw new NoSuchElementException

  override def tail: MyStream[Nothing] = throw new NoSuchElementException

  override def #::[B >: Nothing](element: B): MyStream[B] = new Cons(element, this)

  override def ++[B >: Nothing](anotherStream: MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()

  override def map[B](f: Nothing => B): MyStream[B] = this

  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  override def take(n: Int): MyStream[Nothing] = this

  override def takeList(n: Int): List[Nothing] = Nil
}

/*
 REMEMBER!!!
 IN CALL BY NAME
 The expression is passed literally
 The expression is evaluated every time is used within
 Delays the evaluation of the passed expression until is used
*/

class Cons[+A](h: A, t: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false

  override val head: A = h // We are overriding as a val so I am able to use it in the entirely body

  override lazy val tail: MyStream[A] = t // Combining a call by name with a lazy val evaluation is called CALL BY NEED, and call by need is the technique we will use

  override def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)

  override def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  /*
    s = new Cons(1, ?)
    mapped = s.map(_ + 1) = 2 #:: s.tail.map(f) // The last expression still is not evaluated
      ... mapped.tail // Here is when the tail is evaluated
   */
  override def map[B](f: A => B): MyStream[B] = new Cons(f(head), tail.map(f)) // map still preserves lazy evaluation

  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f) // ++ preserves lazy evaluation

  override def filter(predicate: A => Boolean): MyStream[A] =
    if (predicate(head)) new Cons(head, tail.filter(predicate))
    else tail.filter(predicate)

  override def take(n: Int): MyStream[A] = {
    @tailrec
    def takeTailRec(n: Int, src: MyStream[A], acc: MyStream[A]):  MyStream[A] = {
      if (n <= 0) acc
      else takeTailRec(n - 1, src.tail, new Cons(src.head, acc))
    }
    takeTailRec(n, this, EmptyStream)
  }

//  override def take(n: Int): MyStream[A] = {
//    if (n <= 0) EmptyStream
//    else if (n == 1) new Cons(head, EmptyStream)
//    else new Cons(head, tail.take(n - 1))
//  }

  override def takeList(n: Int): List[A] = take(n).toList()

}

object MyStreamPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals // Naturals
  println(startFrom0.head)

  startFrom0.take(10000).foreach(println)

  // map, flatMap
  println(startFrom0.map(_ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
}
