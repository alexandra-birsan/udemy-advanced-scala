package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {

  val aCondition: Boolean = false
  // the compiler infers the type for us
  val aConditionedVal = if (aCondition) 45 else 89
  // instructions vs expressions

  // Unit - no useful return value, only side effects
  val theUnit = println("Hello, Scala")

  // functions
  def aFunction(x: Int) = x + 1

  // recursion:stack and tail
  @tailrec def factorial(n: Int, accumulator: Int): Int =
    if (n <= 0) accumulator
    else factorial(n - 1, n * accumulator)

  //oop - by declaring classes like in Java
  class Animal

  class Dog extends Animal

  // subtyping polymorphism (= polymorphism via subtyping)
  val aDog: Animal = new Dog

  // abstract types
  trait Carnivore {
    def eat(animal: Animal): Unit
  }

  //mixin multiple traits
  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println("Crunch!")
  }

  // method notation
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog // the compiler rewrites this into the . way

  //anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(animal: Animal): Unit =
      "Roar!" // the compiler creates a new anon class for us and assigns the new value to an instance
  }

  //generics
  abstract class MyList[+A] // variance & covariance

  //singleton and companion objects
  object MyList

  //case classes
  case class Person(name: String, age: Int)

  // exceptions  and try/ catch/ finally
  val throwsException = throw new RuntimeException // Nothing
  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case _: Exception => "I caught an exception"
  } finally {
    println("some logs")
  }

  // packaging and imports
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  incrementer(1)

  val anonymousIncrementer = (x: Int) => x + 1

  List(1, 2, 3).map(anonymousIncrementer) // HOF
  // map, flatMap, filter

  // for-comprehension
  val pairs = for {
    num <- List(1, 2, 3) // if condition
    char <- List('a', 'b', 'c')
  } yield num + "-" + char

  // Scala collections: sequence, array, vectors, lists, maps, tuples
  val aMap = Map(
    "Daniel" -> 4,
    "Alex"   -> 2
  )

  // collections: Options, Try
  val anOption = Some(2)

  // pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case _ => x + "th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }
}
