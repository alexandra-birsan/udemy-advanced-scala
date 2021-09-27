package lectures.part2afp

object CurriesAndPAF extends App {

  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int = y => 3 + y
  println(add3(5))
  println(superAdder(3)(5)) // superAdder is a curried function

  // a METHOD
  def curriedAdder(x: Int)(y: Int): Int = x + y

  val add4: Int => Int = curriedAdder(4) // it does not compile if you remove the return type (because you don't pass the full param list)

  //lifting: because the curriedAdder is a method (we used def), we meed to pass all the params list
  // what we did for add4 is to convert a method into a function value of type Int => Int

  def inc(x: Int) = x + 1

  List(1, 2, 3).map(inc) // ETA-expansion // the compiler converts the method into a lambda

  // how to force the compiler to do ETA-expension when we want:
  val add5 = curriedAdder(5) _ // this translates to: compiler, to an ETA-extension after you apply the first param list

  // EXERCISE
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  // define add7:Int => Int = y => 7+y
  // as many impl for add7 using the above
  // be creative

  val add7 = simpleAddFunction(7, _)
  println(add7(3))
  val add7_2 = simpleAddFunction.curried(7)

  val simpleAdd7 = simpleAddMethod(_, 7) // alternative syntax for turning methods into function values

  val curriedAdd7   = curriedAddMethod(7) _ // PAF - lift a method to a function
  val curriedAdd7_2 = curriedAddMethod(_: Int)(7) // PAF alternative syntax

  // _ are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c

  val insertName = concatenator("Hello, I'm ", _: String, ", how are you?") // this will tell the compiler to do an eta-expansion
  // x:String => concatenator(hello, x, howareyou)

  println(insertName("Alex"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String) // (x, y) => concatenator("Hello", x, y)
  println(fillInTheBlanks("Alex", " Scala is awesome")) // each param is injected in the corresp _

  // EXERCISES
  /*
  1. Process a list of numbers and return their string representations with different formats
  Use the %4.2f, %8.6f and %14.12f with a curried formatter function
   */

  def curriedFormatter(format: String)(n: Double) = format.format(n)

  val f42   = curriedFormatter("%4.2f") _
  val f86   = curriedFormatter("%8.6f") _
  val f1412 = curriedFormatter("%14.12f") _

  List(1.7, 2.3, 3.5).map(f42) foreach println // the compiler does sweet eta-expansion for us
  List(1.2, 2.3, 3.5).map(f86) foreach println
  List(1.7, 2.3, 3.5).map(f1412) foreach println

  /*
  2. difference between
  - functions vs methods
  - parameters: by-name vs 0-lambda
   */
  def byName(n: => Int) = n + 1

  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42

  def parenMethod(): Int = 42

  /*
  call byName and byFunction
  - int
  - method
  -parenMethod
  -lambda
  - PAF
   */
  println(byName(22)) // ok
  println(byName(method)) //ok
  println(byName(parenMethod())) //ok
  println(byName(parenMethod)) // ok but beware ==> byName(parenMethod())

  //  val lambda = () => 10
  //  println(byName(lambda) // not ok
  println(byName((() => 42)())) // ok!!
  def curried(x: Int)(y: Int) = x + y

//    byName(parenMethod _)// not ok
//byFunction(10) // not ok
  println(byFunction(() => 10))
  //  println(byFunction(method)) // the method has not () in its definition, the compiler does not eta-expansion
  println(byFunction(parenMethod)) // the compiler does ETA-expansion
  println(byFunction(parenMethod _)) // the _ is not necessary because the compiler does ETA-expansion on its own
}
