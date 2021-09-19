package lectures.part2fp

object Monads extends App {

  // Our Try monad
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }
  object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Failure(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*
    Probing Monad's principles
   */

  /*
    Left-identity

    unit.flatMap(f) = f(x)
    Attempt.flatMap(f) = f(x) // This only makes sense for the Success scenario

    Success(x).flatMap(f) = f(x) // Proved
   */

  /*
    Right-identity

    Attempt.flatMap(unit) = Attempt
    Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x) // We are returning the function that is apply to x, in this case Attempt(x)
    Failure(e).flatMap(...) = Failure(e)
    // Also proved
   */

  /*
    Associativity

    Attempt.flatMap(f).flatMap(g) == Attempt.flatMap(x => f(x).flatMap(g))

    Failure(e).flatMap(f).flatMap(g) = Failure(e)
    Failure(e).flatMap(x => f(x).flatMap(g)) = Failure(e)

    Success(v).flatMap(f).flatMap(g) =
      f(v).flatMap(g) OR Fail(e)

    Success(v).flatMap(x => f(x).flatMap(g)) =
      f(v).flatMap(g) OR Fail(e)
    // Also proved

   */

  val attempt = Attempt {
    throw new RuntimeException("My own monad, yes!")
  }

  println(attempt)

  /*
    EXERCISE
    1) Implement a lazy[T] monad => That abstract away a computation which will only be executed when it's needed.

      unit/apply
      flatMap

    2) Monads = unit + flatMap // Current studied definition
       Monads = unit + map + flatten // Another definition

       Monad[T] {
        def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

        def map[B](f: T => B): Monad[B] = ???
        def flatten(m: Monad[Monad[T]]): Monad[T] = ???

        (Have a List in mind)
       }
   */
}
