package lectures.part4implicits

object ImplicitsIntro extends App {

  val pair = "Daniel"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"Hi, my name is $name!"
  }

  // The compiler is looking for all the implicits
  // It's able to convert the string to something that have the greet method
  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet) // println(fromStringToPerson("Peter").greet)

  class A {
    def greet: Int = 2
  }

  // Since there are to implicits that could match the compiler will complain since it does not know which one it should match
//  implicit def fromStringToA(str: String): A = new A

  // Implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 10

  increment(2) // It's no like default args since the compiler is looking for implicits in the search scope

}
