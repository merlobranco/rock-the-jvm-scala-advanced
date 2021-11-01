package lectures.part5ts

object RockingInheritance extends App {

  // Convenience
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    // Some methods
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // Diamond problem

  trait Animal { def name: String }
  trait Lion extends Animal { override def name = "Lion"}
  trait Tiger extends Animal { override def name = "Tiger"}
  class Mutant extends Lion with Tiger

  val m = new Mutant
  println(m.name)

  /*
    Mutant
    extends Animal with { override def name: String = "Lion"}
    with Animal with { override def name: String = "Tiger"}

    Whenever we have Diamond inheritance last override gets picked
   */

  // The super problem + type linearization
  trait Cold {
    def print = println("Cold")
  }

  trait Green extends Cold {
    override def print = {
      println("Green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print = {
      println("Blue")
      super.print
    }
  }

  class Red {
    def print = println("Red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("White")
      super.print
    }
  }

  val color = new White
  color.print
  /*
    Super problem explanation

    Cold = AnyRef with <Cold>
    Green
      = Cold with <Green>
      = AnyRef with <Cold> with <Green>
    Blue
      = Cold with <Blue>
      = AnyRef with <Cold> with <Blue>
    Red = AnyRef with <Red>

    White = Red with Green with Blue with <White>
      = AnyRef with <Red>
      with (AnyRef with <Cold> with <Green>)
      with (AnyRef with <Cold> with <Blue>)
      with <White>

    = anyRef with <Red> with <Cold> with <Green> with <Blue> with <White> // Type linearization for White
    // In the context of type serialization the super keyword has a whole new meaning
    // If we could super from the body of white it will take a look at the type immediately to the left in this type serialization
 */
}
