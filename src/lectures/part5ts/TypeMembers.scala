package lectures.part5ts

object TypeMembers extends App {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // Abstract type member
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalC = Cat // Type aliases
  }

  val ac = new AnimalCollection
  val dog: ac.AnimalType = ??? // There is not constructor or information to create an instance of this type

//  val cat: ac.BoundedAnimal = new Cat

  val pup: ac.SuperBoundedAnimal = new Dog
  val cat: ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  // Alternative to generics
  trait MyList {
    type T
    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends MyList {
    override type T = Int
    override def add(element: Int): MyList = ???
  }

  // .type
  type CatsType = cat.type
  val newCat: CatsType = cat // This is valid but we cannot create instances of this new type, just doing associations
//  new CatsType // Not possible

  /*
    Exercise - enforce a type to be applicable to SOME TYPES only
   */

  // LOCKED
  trait MList {
    type A
    def head: A
    def tail: MList
  }

  trait ApplicableToNumbers {
    type A <: Number
  }

  // NOT OK
//  class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumbers {
//    type A = String
//    def head = hd
//    def tail = tl
//  }

  // OK
  class IntList(hd: Integer, tl: IntList) extends MList {
    type A = Integer
    def head = hd
    def tail = tl
  }

  // Number
  // Type members and type member constraints (bounds)

}
