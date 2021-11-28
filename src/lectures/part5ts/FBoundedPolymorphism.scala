package lectures.part5ts

object FBoundedPolymorphism extends App {

//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  class Cat extends Animal {
//    override def breed: List[Animal] = ??? // List[Cat] !!!
//  }
//
//  class Dog extends Animal {
//    override def breed: List[Animal] = ??? // List[Dog] !!!
//  }

  // We want each override to return a specific list
  // We could try the following things:
  // How we make the compiler to force type correctness so we make sure we are not able to make any mistake

  // Solution 1 - Naive
//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  class Cat extends Animal {
//    override def breed: List[Cat] = ??? // List[Cat] !!!
//  }
//
//  class Dog extends Animal {
//    override def breed: List[Cat] = ??? // List[Dog] !!!
//  }

  // Solution 2 - F-Bounded Polymorphism
//  trait Animal[A <: Animal[A]] { // Recursive type: F-Bounded Polymorphism
//    def breed: List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat] {
//    override def breed: List[Animal[Cat]] = ??? // List[Cat] !!!
//  }
//
//  class Dog extends Animal[Dog] {
//    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!!
//  }

  // This is used quite often in ORM, database APIs
//  trait Entity[E <: Entity[E]]

  // Is used for comparison as well
//  class Person extends Comparable[Person] {
//    override def compareTo(o: Person): Int = ???
//  }

  // But F-Bounded Polymorphism has its limitations as well

//  class Crocodile extends Animal[Dog] {
//    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!!
//  }

  // How do I force that the class I am defining and the type I am annotating with are the same?

  // Solution 3 - We could use F-Bounded Polymorphism  in conjunction with self types

//  trait Animal[A <: Animal[A]] { self: A =>
//    def breed: List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat] {
//    override def breed: List[Animal[Cat]] = ??? // List[Cat] !!!
//  }
//
//  class Dog extends Animal[Dog] {
//    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!!
//  }

  // Now It does not compile
//  class Crocodile extends Animal[Dog] {
//    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!!
//  }

  // This also has some limitations
  // Once we bring our hierarchy down one level the F-Bounded Polymorphism stops being effective

//  trait Fish extends Animal[Fish]
//  class Shark extends Fish {
//    override def breed: List[Animal[Fish]] = List(new Cod) // Wrong
//  }
//
//  class Cod extends Fish {
//    override def breed: List[Animal[Fish]] = ???
//  }

  // Exercise How we could fix it?

  // Solution 4 type classes!

//  trait Animal
//  trait CanBreed[A] {
//    def breed(a: A): List[A]
//  }
//
//  class Dog extends Animal
//  object Dog {
//    implicit object DogsCanBreed extends CanBreed[Dog] {
//      def breed(d: Dog): List[Dog] = List()
//    }
//  }
//
//  implicit class CanBreedOps[A](animal: A) {
//    def breed(implicit canBreed: CanBreed[A]): List[A] =
//      canBreed.breed(animal)
//  }
//
//  val dog = new Dog
//  dog.breed // We are making sure we are returning a list of Dogs
  /*
    The final method call when we call dog.breed will be:

      new CanBreedOps[Dog](dog).breed(Dog.DogsCanBreed)

      implicit value to pass to breed: Dog.DogsCanBreed

   */


//  class Cat extends Animal
//  object Cat {
//    implicit object CatsCanBreed extends CanBreed[Dog] {
//      def breed(d: Dog): List[Dog] = List()
//    }
//  }

// The compiler will be nagging about this piece of code
//  val cat = new Cat
//  cat.breed

  // Solution 5 Pure type classes
  trait Animal[A] {
    def breed(a: A): List[A]
  }

  class Dog
  object Dog {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(d: Dog): List[Dog] = List()
    }
  }

  class Cat
  object Cat {
    implicit object CatAnimal extends Animal[Dog] {
      override def breed(d: Dog): List[Dog] = List()
    }
  }

  implicit class CanBreedOps[A](animal: A) {
    def breed(implicit animalTypeClassInstance: Animal[A]): List[A] =
      animalTypeClassInstance.breed(animal)
  }

  val dog = new Dog
  dog.breed

//  val cat = new Cat // The compiler fails to fain the right implicit since is bad defined
//  cat.breed

}
