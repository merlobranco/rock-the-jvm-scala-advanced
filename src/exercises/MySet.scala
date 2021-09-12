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

  override def unary_! : MySet[A] = PropertyBasedSet[A](_ => true)
}

// All elements of type A which satisfy a property
// { x in A | property(x)}
// Potentially infinite Set
case class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(elem: A): Boolean = property(elem)

  // { x in A | property(x)} + element = {x in A | property(x) || x == element}
  override def +(elem: A): MySet[A] =
    PropertyBasedSet[A](x => property(x) || x == elem)

  // { x in A | property(x)} + set = {x in A | property(x) || set contains x }
  override def ++(anotherSet: MySet[A]): MySet[A] =
    PropertyBasedSet[A](x => property(x) || anotherSet(x)) // Remember the apply method of MySet implements contains

  // If we map an infinite set with a function we won't be able to check if the result is fine or not
  // We are not able to say if the element is inside the Set or not, which breaks the whole point of a Set
  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] = PropertyBasedSet[A](x => property(x) && predicate(x))

  override def foreach(f: A => Unit): Unit = politelyFail

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole!")
}

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


//  override def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))
  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet) // Thanks to the ! unary operator
//  override def --(anotherSet: MySet[A]): MySet[A] = {
//    if (anotherSet.contains(head)) tail -- anotherSet - head
//    else tail -- anotherSet + head
//  }

  override def &(anotherSet: MySet[A]): MySet[A] =
//    filter(x => anotherSet.contains(x))
//    filter(x => anotherSet(x)) // Because the apply method of MySet
    filter(anotherSet) // Because Filer receives another function

  override def unary_! : MySet[A] = PropertyBasedSet[A](x => !this.contains(x))
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
  println()
  val negative = !s // s.unary_! = All the naturals not equal to 1,2,3,4
  println(negative(2)) // False. 2 is not in the negative set
  println(negative(5)) // True. 5 is in the negative set

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5 // all the even numbers > 4 + 5
  println(negativeEven5(5))
}
