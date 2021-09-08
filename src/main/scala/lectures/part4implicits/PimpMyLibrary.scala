package lectures.part4implicits

object PimpMyLibrary extends App {


  // 2.isPrime - we can do this via implicit classes
  implicit class RichInt(val value: Int) { // declares the value as a field of the class

    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def *(values: List[Int]): List[Int] = {
      def concatenate(n: Int): List[Int] = if (n <= 0) List()
      else concatenate(n - 1) ++ values

      concatenate(value)
    }

    def times(f: () => Unit): Unit = {
      def timesAux(n: Int): Unit = if (n <= 0) ()
      else {
        f
        timesAux(n - 1)
      }

      timesAux(value)
    }
  }

  42.isEven // the compiler: new RichInt(42).isEven

  // type enrichment = pimping

  1 to 10

  import scala.concurrent.duration._

  3.seconds

  // the compiler doesn't do multiple implicit searches

  implicit class RicherInt(richInt: RichInt) {

    def isOdd: Boolean = richInt.value % 2 != 0

  }

  //  42.isOdd // will not compile: the compiler will wrap 42 into a RichInt, but it won't wrap a RichInt into a RicherInt

  /*
  Enrich the String class
   -as Int
   - encrypt method: John -> Lnjp
    Keep enriching the Int class:
     - times(function)
     3.times(()=> ...)

     3* List(1,2) = List(1,2,1,2,1,2)
   */
  implicit class RichString(string: String) {

    def asInt: Int = Int.unbox(string)

    def encrypt(cypherDistance: Int) = string.map(c => (c + cypherDistance).asInstanceOf[Char])
  }

  println(RichString("JOhn").encrypt(2))

  3.times(() => println("Scala rocks!"))
  println(4 * List(1, 2))

  // "3" / 4
  implicit def stringToInt (string: String):Int = Integer.parseInt(string)

  println( "6" / 2)

  // equivalent of an implicit class: implicit class RichAltInt(value: Int)
  class RichAltInt(value:Int)
  implicit def enrich(value:Int):RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intBoolean(i:Int):Boolean = i == 1

  /*
  if (n) do something
  else do something else
   */

  private val aConditionedVAlue: String = if (3) "OK" else "SOMETHING WRONG"
  println(aConditionedVAlue) // SOMETHING WRONG

  //!!! IMPLICIT METHODS ARE REALLY DIFFICULT TO DEBUG => it could cost you a lot

}
