package lectures.part5typesystem

object SelfTypes extends App {

  trait Instrumentalist {
    def play():Unit
  }

  // how to enforce that a singer knows how to play an instrument?
  trait Singer { self:Instrumentalist => // marker at the language level that forces whoever implements this trait to
    // self can be replaced with a, scala whatever :D
    // implement Instrumentalist as well

    def sing():Unit
  }

  class LeadSinger extends Singer with Instrumentalist {

    override def play(): Unit = ???

    override def sing(): Unit = ???
  }

//  class Vocalist extends Singer {
//
//    override def sing(): Unit = ???
//  }

  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist {

    override def play(): Unit = println("Guitar solo")
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // vs inheritance
  class A
  class B extends A // B is an A

  trait T
  trait S { self: T => // S required a T
  }

  // self-types are commonly used in a CAKE PATTERN

  // CAKE PATTERN = "dependency injection"
  class Component{
    // API
  }
  // classical DI
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component)
  // dependent component will receive at runtime either ComponentA or ComponentB to build the app

  // cake pattern
  trait ScalaComponent{
    //API
    def action(x:Int):String
  }

  trait ScalaDependentComponent {self: ScalaComponent =>

    def dependentAction(x:Int):String = action(x) + "This rocks"
  }

  trait ScalaApplication {self: ScalaDependentComponent => }

  // how cake pattern creates abstraction layers
  // layer 1 - small components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent
  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture // you can choose now which component you want to mix in
  trait Analytics extends ScalaDependentComponent with Stats
   // AT EACH LAYER YOU CAN CHOOSE WHAT COMPONENTS FROM THE PREVIOUS LAYER YOU WANT TO MIX IN
   // layer 3
    trait AnalyticsApp extends ScalaApplication with Analytics
    // at each layer you can bake your app in layers

  // cyclical dependencies
//  class X extends Y
//  class Y extends X
  trait X {self: Y =>}
  trait Y {self: X =>}
  // this cyclic dependency is only apparent. We say that whoever implements X must also implement Y and whoever implements
  //Y must implement X, there's no contradiction there
  // we are saying that X and Y are independent concepts that go hand in hand.
}
