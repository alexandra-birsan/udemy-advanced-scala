package lectures.part4implicits

object PimpMyLibrary extends App {


  // 2.isPrime - we can do this via implicit classes
  implicit class RichInt(val value: Int) { // declares the value as a field of the class

    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def * (values:List[Int]): List[Int] = (1 to value).flatMap(_ => values).toList

    def times(f:Function) =
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
  implicit class RichString(val value: String) {

    def asInt: Int = Int.unbox(value)

    def encrypt = value.map(_ - 3)
  }



}
