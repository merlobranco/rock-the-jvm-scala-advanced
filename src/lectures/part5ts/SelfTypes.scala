package lectures.part5ts

object SelfTypes extends App {

  // Self types is a way of requiring a type to mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer { self: Instrumentalist => // Whoever implements Singer should implement Instrumentalist as well
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

//  class Vocalist extends Singer {
//    override def sing(): Unit = ???
//  }

  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = ???
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // Self type are quite often compared with Inheritance
  class A
  class B extends A // B is an A

  trait T
  trait S { self: T => } // S requires a T

  // Self Types are equivalent to CAKE PATTERN => Also called in languages like Java: "dependency injection"

  // Classical Dependency Injection
  class Component {
    // API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component)

  // CAKE PATTERN
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }
  trait ScalaDependentComponent { self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + " this rocks!"
  }

  // Layer 1 - Small components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent

  // Layer 2 - Compose
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // Layer 3 - App
  trait ScalaApplication { self: ScalaDependentComponent => }

  trait AnalyticsApp extends ScalaApplication with Analytics

  // The different between Dependency Injection and the Cake Pattern
  // DI => Dependencies checked at runtime
  // Cake Pattern => Dependencies checked at compile time

  // Self type allow us to define cyclical dependencies
//  class X extends Y
//  class Y extends X

  trait X { self: Y => }
  trait Y { self: X => }

  // We are defining unrelated concepts that go hand by hand
}
