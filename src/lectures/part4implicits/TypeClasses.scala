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

  // Part 3
  implicit class HTMLEnrichment[T](value: T) {
//    def toHTML(serializer: HTMLSerializer[T]): String = serializer.serialize(value)
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

//  println(john.toHTML(UserSerializer))
  println(john.toHTML)

  /*
    This last approach is very handy for the following reasons:

      - We could extend the functionality to new types
      - We could several implementations for the same type.
        We could choose implementation either importing the implicit serializer into the local scope
        or passing it explicitly
      - Super expressive!
   */

  println(2.toHTML)
  println(john.toHTML(PartialUserSerializer))

  /*
    The Type class pattern is composed for several parts:

      1) Type Class itself with all the functionality we want to expose
        Ej. HTMLSerializer[T] { .. }

      2) The Type class instances, some of which are implicit
        Ej. UserSerializer, IntSerializer PartialUserSerializer

      3) Conversion with implicit classes, that later will allow us to use out Type class instances as implicit parameters
        Ej. HMTLEnrichment
   */

  /*
    Context bounds
   */
  def htmlBoilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body>${content.toHTML(serializer)}</body></html>"

  /*
    Here we have a context bound that is telling to the compiler to inject an implicit parameter of type HTMLSerializer[T]
    The advantage is the short syntax, but we cannot use serializer by name, because the compiler injects it for us
   */
  def htmlSugar[T : HTMLSerializer](content: T): String = {
    // Now with this approach we could use serializer by name
    val serializer = implicitly[HTMLSerializer[T]]
    // Use serializer
    s"<html><body>${content.toHTML(serializer)}</body></html>"
  }

  /*
    Implicitly
   */
  case class Permissions(mask: String)
  implicit val defaultPermissions = Permissions("0744")

  // In some other part of the code we want to surface out what is the implicit value for permission
  val standardPerms = implicitly[Permissions]
}