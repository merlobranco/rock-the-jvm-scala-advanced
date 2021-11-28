package lectures.part5ts

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HigherKindedTypes extends App {

  trait AHigherKindedType[F[_]]

  trait MyList[T] {
    def flatMap[B](f: T => B): MyList[B]
  }

  trait MyOption[T] {
    def flatMap[B](f: T => B): MyList[B]
  }

  trait MyFuture[T] {
    def flatMap[B](f: T => B): MyList[B]
  }

  // Combine/multiply List(1, 2) x List("a", "b") => List(1a, 1b, 2a, 2b)

  def multiply[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
    for {
      a <-listA
      b <- listB
    } yield (a, b)

  def multiply[A, B](listA: Option[A], listB: Option[B]): Option[(A, B)] =
    for {
      a <-listA
      b <- listB
    } yield (a, b)

  def multiply[A, B](listA: Future[A], listB: Future[B]): Future[(A, B)] =
    for {
      a <-listA
      b <- listB
    } yield (a, b)

  // Use Higher Kinded Types

  trait Monad[F[_], A] {
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f: A => B): F[B]
  }

  class MonadList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    override def map[B](f: A => B): List[B] = list.map(f)
  }

  class MonadOption[A](option: Option[A]) extends Monad[Option, A] {
    override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)
    override def map[B](f: A => B): Option[B] = option.map(f)
  }

  def multiply[F[_], A, B](ma: Monad[F, A], mb: Monad[F, B]): F[(A, B)] =
    for {
      a <- ma
      b <- mb
    } yield (a, b)

  /*
    ma.flatMap(a => b.map(b => (a, b)))
   */


  val monadList = new MonadList(List(1, 2, 3))
  monadList.flatMap(x => List(x, x + 1)) // List[Int]
  // Monad[List, Int] => List[Int]
  monadList.map(_ * 2) // List[Int]
  // Monad[List, Int] => List[Int]

  println(multiply(new MonadList(List(1, 2)), new MonadList(List("a", "b"))))
  println(multiply(new MonadOption(Some(2)), new MonadOption(Option("Scala"))))
}
