package lectures.part4implicits

object ImplicitsIntro extends App {

  val pair    = "Daniel" -> "555"
  val intPair = 1        -> 2 // -> is an implicit method , method of an implicit class
  println(intPair) // creates the tuple (1,2)

  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet) // the compiler does: println(fromStringToPerson("Peter").greet

  // if we have multiple implicits for the same conversion, the compiler will throw an exception

  // implicit params
  def increment(x: Int)(implicit amount: Int) = x + amount

  implicit val defaultAmount = 10

  //  implicit val defaultAmount2 = 10 // this generates compilation failure
  println(increment(2))
  println(increment(2))

  implicit val defaultAmount2 = 10 // this DOES NOT generate compilation failure

}
