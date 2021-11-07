package lectures.part5ts

import com.sun.rowset.internal.SyncResolverImpl

object PathDependentTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner) = println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String // We only can define Type aliases not types inside methods
    2
  }

  /*
    Type nested inside classes like inner classes and inner objects,
    the class members, object members, trait members and so on and so forth
    are defined per instance
   */

  val o = new Outer
//  val inner = new Inner
//  val inner = new Outer.Inner
  val inner = new o.Inner

  val oo = new Outer
//  val otherInner: oo.Inner = new o.Inner

  o.print(inner)
//  oo.print(inner)

  // We see these types are path-dependant

  // All the types have in common a super type
  // Outer#Inner
  o.printGeneral(inner)
  oo.printGeneral(inner)

  /*
    Exercise, Restricting type usage
    DB keyed by Int or String, but maybe others

    Given:

      trait Item[Key]
      trait IntItem extends Item[Int]
      trait StringItem extends Item[String]

      def get[ItemType](key: Key): ItemType

    It should be:

      get[IntItem](42) // OK
      get[StringItem]("home") // OK
      get[IntItem]("scala") // NOT OK

   Use path-dependant types
   abstract type members and/or type aliases
   */

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42 ) // OK
  get[StringItem]("home") // OK
//  get[IntItem]("scala") // NOT OK

}
