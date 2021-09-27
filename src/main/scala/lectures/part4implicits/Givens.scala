package lectures.part4implicits

object Givens extends App {

  val aLst = List(2,4,3,1)

  val anOrderedList = aLst.sorted // implicit Ordering[Int]

  println(anOrderedList)

  object Implicits {

    implicit val descendingOrdering:Ordering[Int] = Ordering.fromLessThan(_ > _)
  }

  // SCALA 3 style
  object Givens {
    //  given descendingOrdering:Ordering[Int] = Ordering.fromLessThan(_ > _)
  }

  // instantiating an anon class
  object GivenAnonClassNaive {
    //  given descendingOrdering:Ordering[Int] = new Ordering[Int]{
    //    override def compare(x: Int, y: Int): Int = x-y
    //  }
  }

//  given descendingOrdering_v3:Ordering[Int] with {
//        override def compare(x: Int, y: Int): Int = x-y
//  }

  import GivenAnonClassNaive._ // in Scala 3 this does not import givens as well
  // because in large codebase, it is difficult to track where the given came from
  //  import GivenAnonClassNaive.given - import all given
  // import GivenAnonClassNaive.descendingOrdering - import a specific given

  // implicit arguments
//  def extreme_version2 [A](list: List[A])(using ordering:Ordering[A]):(A,A) =???

  // implicit defs
  trait Combinator[A]{ // semigroup
    def combine(x:A, y:A):A
  }

  implicit def listOrdering[A](implicit simpleOrdering:Ordering[A], combinator: Combinator[A]):Ordering[List[A]] =
    (x: List[A], y: List[A]) => {
    val sumX = x.reduce(combinator.combine)
    val sumY = y.reduce(combinator.combine)
    simpleOrdering.compare(sumX, sumY)
  }

  // equivalent in Scala 3 with givens
//  given list_ordering_v2[A](using simpleOrdering:Ordering[A], combinator: Combinator[A]):Ordering[List[A]]) with {
//    (x: List[A], y: List[A]) => {
//      val sumX = x.reduce(combinator.combine)
//      val sumY = y.reduce(combinator.combine)
//      simpleOrdering.compare(sumX, sumY)
//  }

  // implicit conversions: abused in Scala 2
  case class Person(name:String){
    def greet():String = s"Hi, my name is $name"
  }

  implicit def string2Person(string: String):Person = Person(string)
  val alexGreet = "Alex".greet() // the compiler invokes the string2Person conversion on "Alex" (finds the wrapper)
  // and then it calls greet() on that

  // 2. extension methods
  // in Scala 2 having implicit defs can cause issues in large code bases => it is highly  discouraged in Scala 3
  // in Scala 3
  import scala.language.implicitConversions // required in Scala 3
//  given string2PersonConversion: Conversion[String, Person] with {
//    override def apply(x:String) = Person(x)
//  }
}
