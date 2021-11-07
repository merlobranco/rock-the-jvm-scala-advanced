package lectures.part5ts

object Variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // VARIANCE
  // It's the problem of Inheritance (Type substitution) over generics

  // Should Cage[Cat] inheritance from Cage[Animal]?
  class Cage[T]

  // Options:
  // 1) Yes: Covariance
  class CCage[+T]
  val ccage: CCage[Animal] = new CCage[Cat]

  // 2) No: Ivariance
  class ICage[T]
//  val icage: ICage[Animal] = new ICage[Cat]

  // 3) Hell no, opposite: Contravariance
  class XCage[-T]
  // I am replacing a cat cage by a more general one
  val xcage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](animal: T) // Invariant

  // Covariant position
  class CovariantCage[+T](val animal: T)

  /*
    This won't compile. It will throw a contravariant position error
    class ContravariantCage[-T](val animal: T)

    Because if this compiles we will have something like this:

    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)

    And we are looking for a specific cage not any other one
   */

  /*
    class CovariantVariableCage[+T](var animal: T) // Types of var in CONTRAVARIANT POSITION

    It won't compile, the var is in contravariant position
    Because if the compile passes this code, we will able to write something like

    val ccage: CCage[Animal] = new CCage[Cat](new Cat)
    ccage.animal = new Crocodile

    We are passing a specific cage and we are not able to pass the animal inside

    class ContravariantVariableCage[-T](var animal: T) // Also in COVARIANT POSITION

    This also won't compile
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */

  class InvariantVariableCage[T](var animal: T) // OK

  /*
    trait AnotherCovariantCage[+T] {
      def addAnimal(animal: T) // Method arguments CONTRAVARIANT POSITION
    }

    val ccage = CCage[Animal] = new CCage[Dog]
    ccage.add(new Cat) // And we don't want Cats and Dogs in the same position
  */

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }
  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  class MyList[+A] {
//    def add(element: A): MyList[A]
    def add[B >: A](element: B): MyList[B] = new MyList[B] // Widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  // This is fine because Cat is a supper type of Kitty and the compiler will return a list of cats

  val evenMoreAnimals = moreAnimals.add(new Dog) // Now we will have a list of animals

  // IMPORTANT!!!
  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITIONS

  // RETURN TYPES
  class PetShop[-T] {
//    def get(isItAPuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION
    def get[S <: T](isItAPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  /*
    Imagine the compile passes a code like this

    val catShop = new PetShop[Animal] {
      def get(isItAPuppy: Boolean): Animal = new Cat
    }

    Then we will have

    val dogShop: PetShop[Dog] = catShop
    dogShop.get(true) // EVIl CAT!
   */

  val shop: PetShop[Dog] = new PetShop[Animal]
//  val evilCat = shop.get(true, new Cat)

  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
    BIG RULE

    - Method arguments are in CONTRAVARIANT POSITION
    - Return types are in COVARIANT POSITION
   */

  /*
    EXERCISES


    Design Parking application for illegal parking

    1) Implement Invariant, Covariant, Contravariant version of
      Parking[T](things: List[T]) {
        park(vehicle: T)
        impound(vehicles: List[T])
        checkVehicles(conditions: String): List[T]
      }

    2) Imagine instead of using List Scala type we use someone else API

      Invariant list IList[T]

    3) Parking should be a monad!
      - flapMap
   */
  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  class CParking[+T](things: List[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???
    def impound[S >: T](vehicles: List[S]): CParking[S] = ??? // Widening
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  class IParking[T](things: List[T]) {
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  class XParking[-T](things: List[T]) {
    def park(vehicle: T): XParking[T] = ???
    def impound(vehicles: List[T]): XParking[T] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???

//    def flatMap[S](f: T => XParking[S]): XParking[S] = ???
//    def flatMap[S](f: Function1[T, XParking[S]]): XParking[S] = ???
    def flatMap[R <: T, S](f: Function1[R, XParking[S]]): XParking[S] = ???
  }

  /*
    RULE of THUMB
    - Use covariance => When we are dealing with a COLLECTION OF THINGS
    - Use contravariance => When we want to deal with a GROUP OF ACTIONS we want to perform on our types

    In this case we will go for XParking because we want a group of actions over the vehicles
   */

  class CParking2[+T](things: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???
    def impound[S >: T](vehicles: IList[S]): CParking2[S] = ??? // Widening
    def checkVehicles[S >: T](conditions: String): IList[S] = ???
  }

  class IParking2[T](things: IList[T]) {
    def park(vehicle: T): IParking2[T] = ???
    def impound(vehicles: IList[T]): IParking2[T] = ???
    def checkVehicles(conditions: String): IList[T] = ???
  }

  class XParking2[-T](things: IList[T]) {
    def park(vehicle: T): XParking2[T] = ???
    def impound[S <: T](vehicles: IList[S]): XParking2[S] = ???
    def checkVehicles[S <: T](conditions: String): IList[S] = ???
  }



}
