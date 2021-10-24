package lectures.part4implicits

import java.util.Date

object JSONSerialization extends App {

  /*
    Users, posts, feeds
    Serialize to JSON
   */

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
    Steps
    1 - Create intermediate data types: Int, String List, Date
    2 - Create Type Classes for conversion to intermediate data types
    3 - Serialize to JSON
  */

  // Intermediate data type
  sealed trait JSONValue {
    def stringify: String
  }

  /*
    {
      name: "John",
      age: 22,
      friends: [...],
      latestPost: {
        content: "Scala Rocks",
        date: ...
      }
    }

    The key of the map will be the property name and
    the the value will be stored on the JSONValue object
   */

  /*
    Intermediate data types:
   */
  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = s"\"$value\""
  }

  final case class JSONInt(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    override def stringify: String = values.map {
      case (key, value) => s"\"$key\":${value.stringify}"
    }.mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Brais"),
    "posts" -> JSONArray(List(
      JSONString("Scala Rocks!"),
      JSONInt(453)
    ))
  ))

  println(data.stringify)

  // Type class
  /*
    We need:
      1 - Type class
      2 - Type class instances (implicit)
      3 - Pimp library to use type class instances
   */

  // 1 - Type class
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  // 3 - Conversion (JSONEnrichment). Pimp library to use type class instances
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue =
      converter.convert(value)
  }

  // 2 - Type class instances (implicit)
  implicit object StringConverter extends JSONConverter[String] {
    override def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object IntConverter extends JSONConverter[Int] {
    override def convert(value: Int): JSONValue = JSONInt(value)
  }

  implicit object UserConverter extends JSONConverter[User] {
    override def convert(value: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(value.name),
      "age" -> JSONInt(value.age),
      "email" -> JSONString(value.email)
    ))
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(value: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(value.content),
      "createdAt" -> JSONString(value.createdAt.toString)
    ))
  }

//  implicit object FeedConverter extends JSONConverter[Feed] {
//    override def convert(value: Feed): JSONValue = JSONObject(Map(
//      "user" -> UserConverter.convert(value.user),
//      "posts" -> JSONArray(value.posts.map(PostConverter.convert))
//    ))
//  }

  // Better approach
  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(value: Feed): JSONValue = JSONObject(Map(
      "user" -> feed.user.toJSON,
      "posts" -> JSONArray(value.posts.map(_.toJSON))
    ))
  }

  // Call stringify on result

  val now = new Date(System.currentTimeMillis())
  val john = User("John", 34, "john@rockthejvm.com")
  val feed = Feed(john, List(
    Post("Hello", now),
    Post("Look at this cute puppy", now)
  ))

  println(feed.toJSON.stringify)

}
