package lectures.part4implicits

object ExtensionMethods extends App {

  case class Person(name:String){
    def greet():String = s"Hi, my name is $name"
  }

  extension greetAsPerson(string:String) { // extension method
    def greetAsPerson():String = Person(string).greet()
  }

  // this allows me to use the greetAsPerson method on String, even if it does not belong to the original type
  val AlexGreet = "Alex".greetAsPerson()

  // cleaner than using the RichInt implicit class
// extension (value: Int) {
//
//    def isEven: Boolean = value % 2 == 0
//
//    def sqrt: Double = Math.sqrt(value)
//
//    def *(values: List[Int]): List[Int] = {
//      def concatenate(n: Int): List[Int] = if (n <= 0) List()
//      else concatenate(n - 1) ++ values
//
//      concatenate(value)
//    }
//
//    def times(f: () => Unit): Unit = {
//      def timesAux(n: Int): Unit = if (n <= 0) ()
//      else {
//        f
//        timesAux(n - 1)
//      }
//
//      timesAux(value)
//    }
//  }

  // generic extensions
//  extension [A](values:List[A]){
//
//    def ends:(A,A) = (values.head, values.last)
//    def extremes(using ordering:Ordering[List[A]]): (A, A)= { // <- I can have implicit params
//      sortedValues = values.sorted.ends // <- I can call extension methods here
//    }
//  }
}
