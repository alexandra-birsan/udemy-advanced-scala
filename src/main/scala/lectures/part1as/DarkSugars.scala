package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1: methods with single param
  def singleArgMethod(arg: Int) = s"$arg little ducks ..."

  val description = singleArgMethod {
    // write some complex code
    42
  }

  val aTryInstance = Try { // this is in fact the apply method from Try
    throw new RuntimeException
  }

  List(1, 2, 3).map { x =>
    x + 1
  }

  // syntax sugar #2: single abstract method
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val aFunckyInstance
      : Action = (x: Int) => x + 1 // magic done by the compiler to allow the conversion from lambda to single abstract type

  //example: Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hello, Scala")
  })

  val aSweeterThread = new Thread(() => println("Sweet, Scala"))

  // this pattern works also for some classes that have some methods implemented and only 1 abstract method

  abstract class AnAbstractType {
    def implemented: Int = 23

    def f(a: Int): Unit
  }

  val anAbstractInstance: AnAbstractType = (a: Int) => println("Sweet") // this is not type inference :)

  // syntax sugar #3: the :: and #:: methods are special - are right

  val prependedList = 2 :: List(3, 4)
  // infix are transformed to firstObject.method(secondObject)
  // there is no :: method on Int
  1 :: 2 :: 3 :: 4 :: List(4, 5)

  // List(4,5).::(3).::(2).::(1)

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // actual impl here
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // the -->: is right assoc

  // syntax sugar #4: multi-word naming

  class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"name said $gossip")
  }

  // we can infix this method
  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is so sweet!"

  //syntax sugar #5: infix types
  class Composite[A, B]

  val composite: Composite[Int, String] = ???
  // but we can write them also using infix
  val composite2: Int Composite String = ???

  class -->[A, B]

  val towards: Int --> String = ??? // the compiler translates this to -->[Int, String]

  // syntax sugar #6: update() is very special, much like apply()
  val anArray = Array(1, 2, 3)
  anArray(2) = 7 // rewritten to anArray.update(2,7) 2 = index, 7 = value that we want to put in
  // update used in mutable collections
  // remember apply() and update()

  // syntax sugar #7: setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member:                 Int = internalMember // getter
    def member_=(value: Int): Unit = internalMember = value //setter
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // rewritten by the compiler as aMutableContainer.member_=42

}
