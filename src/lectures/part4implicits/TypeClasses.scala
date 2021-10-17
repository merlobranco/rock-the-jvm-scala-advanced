package lectures.part4implicits

import java.util.Date

object TypeClasses extends App {

  // A Type Class is a trait that takes a Type and describes all the operations that can be applied to that Type

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age years old) <a href=$email/> </div>"
  }

  User("John", 32, "john@rockthejvm.com").toHtml

  /*
    This code will work but it has the following disadvantages:

    1 - This only works for the types WE write.
        For any already implemented types in the libraries we are using like Date and so on,
        we should write conversions to other types, not that much pretty
    2 - This is ONE implementation out of quite a number
        When an user is logged or not we should display something different for each scenario
   */

  /*
    Another option will be using Patten matching
    We do have the freedom of manipulating the Data Type we have access to,
    we have some benefits but we loose a few:

      1 - We loose the Type safety, because the value can be anything really
      2 - We need to modify the code every time, like adding new Data structure, etc
      3 - This is still one implementation
   */
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e) =>
      //      case java.util.Date =>
      case _ =>
    }
  }

  /*
    Better approach
    The good thing of this approach is:
      1 - We can define serializers for other types
      2 - We could define several serializers for a certain type
   */

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name} (${user.age} years old) <a href=${user.email}/> </div>"
  }

  val john = User("John", 32, "john@rockthejvm.com")
  println(UserSerializer.serialize(john))

  // 1 - We can define serializers for other types
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString}</div>"
  }

  // 2 - We could define several serializers for a certain type
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}</div>"
  }

  /*
    TYPE CLASS
    Defines a collection of operations that could be apply to a certain type
    All the implementers of the Type class are called Type Class instances
   */
  // TYPE CLASS TEMPLATE
  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  object MyTypeClassTemplate {
    def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance
  }

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

  val carol = User("Carol", 32, "carol@rockthejvm.com")

//  println(s"Equal by name: ${IsEqualByName.isEqual(john, carol)}")
//  println(s"Equal by age: ${IsEqualByAge.isEqual(john, carol)}")
  println(s"Equal by name: ${IsEqualByName(john, carol)}")
  println(s"Equal by age: ${IsEqualByAge(john, carol)}")

  // Part 2
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style: \"color=blue\">$value</div>"
  }

  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))

  // We have access to the serialize method and the own User type methods as well
  println(HTMLSerializer[User].serialize(john)) // This is possible thanks to the apply method

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
}