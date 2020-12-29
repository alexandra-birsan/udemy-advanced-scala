package lectures.part2afp

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1

  // how can I restrict the values that I pass to the function? e.g. the function to be applied to 1, 2, 3 but not to 4

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  // the implementation above does its job, but it's clunky to do it like this

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 3 => 999
    // for other numbers it'll throw a scala.MatchError:
  }
  // {1,2,5} => Int  this function can be applied only to the {1,2,3} domain from Int, it's a partial function
  // Scala supports a short-hand notation for this

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 3 => 999
  } // what it between {} is the partial function value

  println(aPartialFunction(2))
  //  println(aPartialFunction(23))

  // partial functions utilities
  println(aPartialFunction.isDefinedAt(23)) // to find if a found is applicable to a value

  // PF can be lifted to total functions (if they cannot be applied to the argument, they will return None)
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(23))

  // orElse - if you want to chain multiple partial functions
  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 69
  }

  println(pfChain(2))
  println(pfChain(45))

  //PF extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }
  // this compiles because partial functions are a subtype of a normal function
  // a side effect to this is that HOF accept partial functions as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)
  // if I change case 3 to case 5, this will throw a MatchError
  /*
    Note: a partial function can ONLY have 1 parameter type (otherwise, how would we implement pattern matching with multiple parameters?)
   */

  /**
   * Exercises
   *
   * 1. construct a PF yourself (anon class)
   * 2. dumb chat bot as a PF
   */

  // ex 1
  val anAnonPF = new PartialFunction[String, String] {
    override def apply(v1: String): String = v1 match {
      case "Hello!" => "Hello, there!"
      case "Bye!" => "Bye, bye!"
    }

    override def isDefinedAt(x: String): Boolean = x == "Hello!" || x == "Bye!"
  }

  println(anAnonPF("Hello!"))

  // ex 2
  scala.io.Source.stdin.getLines().map(anAnonPF).foreach(println)
}
