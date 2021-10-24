package exercises

import lectures.part4implicits.TypeClasses.User

object EqualityPlayground extends App {

  /**
   * Equality
   */
  trait Equal[T] {
    //    def isEqual(value: T, value2: T): Boolean
    def apply(value: T, value2: T): Boolean
  }

  object IsEqualByAge extends Equal[User] {
    //    override def isEqual(user: User, user2: User): Boolean = user.age == user2.age
    override def apply(user: User, user2: User): Boolean = user.age == user2.age
  }

  implicit object IsEqualByName extends Equal[User] {
    //    override def isEqual(user: User, user2: User): Boolean = user.name == user2.name
    override def apply(user: User, user2: User): Boolean = user.name == user2.name
  }

  val john = User("John", 32, "john@rockthejvm.com")
  val carol = User("Carol", 32, "carol@rockthejvm.com")

  //  println(s"Equal by name: ${IsEqualByName.isEqual(john, carol)}")
  //  println(s"Equal by age: ${IsEqualByAge.isEqual(john, carol)}")
  println(s"Equal by name: ${IsEqualByName(john, carol)}")
  println(s"Equal by age: ${IsEqualByAge(john, carol)}")

  /*
    Exercise: Implement the Type Class pattern for the Equality Type Class
   */

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]) = equalizer(a, b)
  }

  /*
    This is an example of a AD-HOC polymorphism
    If we have to distinct or potentially unrelated types have Equalizers implemented,
    we could call the Equal thing of them regardless of their types
    Depending of the values being compared the compiler takes care of fetching the correct Type class instance for our types.
   */
  println(s"Implicit Equal result: ${Equal(john, carol)}")

  /*
    Exercise - Improve the Equal Type Class with an implicit conversion class
    ===(anothervalue: T)
    !==(anothervalue: T)
   */

  implicit class TypeSafeEqual[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = equalizer(value, anotherValue)
    def !==(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = !equalizer(value, anotherValue)
  }

  println(john === carol)
  // john.===(carol)
  // new TypeSafeEqual[User](john).===(carol)
  // new TypeSafeEqual[User](john).===(carol)(IsEqualByName)
  println(john !== carol)

  /*
    TYPE SAFE
   */
  println(john == 43)
//  println(john === 43) // The compiler performs type validation
}
