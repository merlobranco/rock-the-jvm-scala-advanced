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

  // 1 - Lazy Monad

  class Lazy[+A](value: => A) {
    // Call by need
    private lazy val internalValue = value
    def use: A = internalValue
    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(value)
  }
  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("Today I don't feel like doing anything")
    42
  } // The text is not printed because the value is not used, it is not evaluated

//  println(lazyInstance.use) // Now is evaluated

  val flatMappedInstance = lazyInstance.flatMap(x => Lazy {
    10 * x
  }) // Initially. We will see the text despite being a Lazy object, because flatMap applies f on the value, and f is applied eagerly. Solution: Receive the parameter by name as well

  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy {
    10 * x
  })
  flatMappedInstance.use
  flatMappedInstance2.use // It's triggering the printing twice. Solution: Adding a call by need using lazy val

  /*
    Left-identity
    unit.flatMap(f) = f(v)
    Lazy(v).flatMap(f) = f(v)

    Right-identity
    l.flatMap(unit) = l
    Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

    Associativity: l.flatMap(f).flatMap(g) = l.flatMap(x => f(x).flatMap(g))
    Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
    Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)
   */

  // 2: map a flatten in terms of flatMap
  /*
    Monad[T] {
      def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

      def map[B](f: T => B): Monad[B] = flatMap(x => unit(f(x))) // Monad[B]
      def flatten(m: Monad[Monad[T]]): Monad[T] = m.flatMap((x: Monad[T]) => x)

      List(1,2,3).map(_ * 2) = List(1,2,3).flatMap(x => List(x * 2))
      List(List(1,2),List(3,4)).flatten = List(List(1,2),List(3,4)).flatMap(x => x) = list(1,2,3,4)
    }
  */

}
