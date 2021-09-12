package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  /*
    EXERCISE - Implement a functional set
   */
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  override def apply(elem: A): Boolean = contains(elem)

  /*
    EXERCISE
      - Removing an element
      - Difference with another Set
      - Intersection with another Set
   */

  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A]

  /*
    EXERCISE
      - Implement a unary_! = NEGATION of a set
        Set[1,2,3] =>
   */
  def unary_! : MySet[A]

}

// The reason we chose to make a class not an object
// It's because we declared MySet as INVARIANT not like COVARIANT as we did with MyLis
case class EmptySet[A]() extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override def -(elem: A): MySet[A] = this

//  override def --(anotherSet: MySet[A]): MySet[A] = anotherSet
  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = AllInclusiveSet()
}

// A All Inclusive Set is infinitive
case class AllInclusiveSet[A]() extends MySet[A] {
  override def contains(elem: A): Boolean = true
  override def +(elem: A): MySet[A] = this
  override def ++(anotherSet: MySet[A]): MySet[A] = this
  override def map[B](f: A => B): MySet[B] = ???
  override def flatMap[B](f: A => MySet[B]): MySet[B] = ???
  override def filter(predicate: A => Boolean): MySet[A] = ??? // Property-based Set
  override def foreach(f: A => Unit): Unit = ???
  override def -(elem: A): MySet[A] = ???
  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
  override def unary_! : MySet[A] = NonEmptySet[A]
}

// This getting too much complicated for implementing the negate of a set
// Better approach : PropertyBasedSet[A]

case class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  override def +(elem: A): MySet[A] = {
    if (this.contains(elem)) this
    else NonEmptySet[A](elem, this)
  }

  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] =
    if (predicate(head)) tail.filter(predicate) + head
    else tail.filter(predicate)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head


  override def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))
//  override def --(anotherSet: MySet[A]): MySet[A] = {
//    if (anotherSet.contains(head)) tail -- anotherSet - head
//    else tail -- anotherSet + head
//  }

  override def &(anotherSet: MySet[A]): MySet[A] =
//    filter(x => anotherSet.contains(x))
//    filter(x => anotherSet(x)) // Because the apply method of MySet
    filter(anotherSet) // Because Filer receives another function

  override def unary_! : MySet[A] = ???
}

object MySet {
  // With the vararg A* we could provide multiple values of type A
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values.toSeq, EmptySet[A]())
  }
}

object MySetPlayground extends App{
  val s = MySet(1,2,3,4)
  s + 5 ++ MySet(-1,-2) + 3 map(x => x * 10) foreach println
  println()
  s + 5 ++ MySet(-1,-2) + 3 + 10 flatMap(x => MySet(x, x * 10)) foreach println // Check  10 is not repeated
  println()
  s + 5 ++ MySet(-1,-2) + 3 flatMap(x => MySet(x, x * 10)) filter(_ % 2 == 0) foreach println
  println()
  s - 3 foreach println
  println()
  s & MySet(3,6,7,4) foreach println
  println()
  s -- MySet(3,6,7,4) foreach println

}
