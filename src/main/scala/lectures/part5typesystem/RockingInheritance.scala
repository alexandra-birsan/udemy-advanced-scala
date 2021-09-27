package lectures.part5typesystem

object RockingInheritance extends App {

  // convenience
  trait Writer[T] {

    def write(value: T): Unit
  }

  trait Closeable {

    def close(status: Int): Unit
  }

  trait GenericStream[T] {

    // some methods
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](
      stream: GenericStream[T] with Writer[T] with Closeable
  ): Unit =
    // GenericStream[T] with Writer[T] with Closeable - IS ITS OWN TYPE and has access to all our API
    // this is the convenience: when we don't know who uses mixes in our specific traits, we can use them all in
    // a specific type
    {

      stream.foreach(println)
      stream.close(1)
    }

  // diamond problem
  trait Animal {
    def name: String
  }

  trait Lion extends Animal { override def name:  String = "LION" }
  trait Tiger extends Animal { override def name: String = "TIGER" }
//  class Mutant extends Lion with Tiger { override def name: String = "ALIEN" }
  class Mutant extends Lion with Tiger // this is the classical diamond problem:
  // we have Animal on top, we have Lion and Tiger as direct descendents of Animal and Mutant as
  // the intersection between Lion and Tiger

  val mutant = new Mutant
  println(mutant.name) // tiger
  /*
  The compiler does:
  Mutant extends Animal
  with { override def name: String = "LION" } // if we had only this, them mutant.name would print lion
  with { override def name: String = "TIGER" } // ok, I am going to use this!
   */

  // the super problem + type linearization

  trait Cold { def print() = println("Cold") }

  trait Green extends Cold {
    override def print() = {
      println("green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print() = {
      println("blue")
      super.print
    }
  }

  class Red {
    def print() = println("red")
  }

  class White extends Red with Green with Blue {

    override def print(): Unit = {
      println("white")
      super.print()
    }
  }

  val white = new White
  white.print() // white blue green cold

  // type linearization: White = AnyRef with <Red> with <Cold> with <Green> with <Blue> // the duplicates are skipped
  // if you call super from the body of White it will take a look at the type immediately to the left
}
