package lectures.part5typesystem

object PathDependentTypes extends App {

  class Outer {

    class Inner
    object InnerObject
    type InnerType

    def print(i:Inner) =  println("I")
    def printGeneral(i:Outer#Inner) =  println("i")
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String
    2
  }

  // we can declare classes and objects anywhere
  // per instance
  val outer = new Outer
//  val inner = new Inner // does not compile, neither new Outer.Inner
  val inner = new outer.Inner

  val oo = new Outer
  val otherInner:oo.Inner = new oo.Inner // inner and otherInner are different types

  outer.printGeneral(inner)
  outer.printGeneral(otherInner)

  /*
  Exercise
  DB keyed by Int or String, but maybe others
   */
  // use path-dependent types
  // use abstract type members and/or type aliases

  trait ItemLike{
    type Key
  }
  trait Item[K] extends ItemLike {
    type Key = K
  }

  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](value:ItemType#Key):ItemType = ???

  get[IntItem](42)
  get[StringItem]("home")
//  get[IntItem]("scala") // this should not compile
}
