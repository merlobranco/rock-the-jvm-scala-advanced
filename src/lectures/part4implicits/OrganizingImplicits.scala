package lectures.part4implicits

object OrganizingImplicits extends App {

  // This implicit has priority over scala.Predef
  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
//  implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _) // Another possible option
//  implicit def reverseOrdering(): Ordering[Int] = Ordering.fromLessThan(_ > _) // This will fail
  // This implicit will trigger an error since the compiler won't know which implicit should use
//  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)

  println(List(1,4,5,3,2).sorted)

  // Scala looks for the sorted implicit in scala.Predef

  /*
    Implicits
      Could be used as implicit parameters
      Should be defined inside an Object or a Trait
      They can be:
        - val/var
        - object
        - accessor methods = defs with no parenthesis
   */

  // Exercise

  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

  /*
   Implicit Scope
     From highest to lowest priority
     - normal scope = LOCAL SCOPE
     - imported scope
     - companion objects of all types involved in the method signature

   Let's consider the following scenario:

     sorted[B >: A](implicit ord: Ordering[B]): List[B]

       It will look for implicits on:

         - List
         - Ordering
         - All the types involved = A or any super type
  */

//  implicit def alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
//  implicit def alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)

  // Better approach for defining implicits
//  object Person {
//    implicit def alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
//  }

  // This implicit has more priority since it's on the local scope
//  implicit def ageOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)

  // If both implicit are good ones the we should put them in different companion objects and then reference the right one

  object AlphabeticNameOrdering {
    implicit def alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
  }

  object AgeOrdering {
    implicit def ageOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
  }

  import AgeOrdering._
  println(persons.sorted)

  /*
    Exercise
      Define ordering by
      - totalPrice = most used (50%)
      - unit count 25%
      - by unit price 25%

      Put them in the proper place
   */

  case class Purchase(nUnits: Int, unitPrice: Double) {
    def total = nUnits * unitPrice
  }

  val purchases = List(
    Purchase(3, 100),
    Purchase(1, 600),
    Purchase(6, 50)
  )

  object Purchase { // We will use a companion object because is the most used
    implicit def totalOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.total < _.total)
  }

  object CountOrdering {
    implicit def countOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
  }

  object UnitPriceOrdering {
    implicit def unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }

  println(purchases.sorted)
}
