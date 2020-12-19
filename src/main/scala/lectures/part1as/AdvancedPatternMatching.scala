package lectures.part1as

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"The only element is $head")
    case _ =>
  }

  class Person(val name: String, val age: Int)

  //we define a companion object and a method unapply
  object Person {
    def unapply(person: Person): Option[(String, Int)] = if (person.age < 21) None
    else Some(person.name, person.age)

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(name, age) => s"Hello, $name of age $age! "
    case _ => "Hello, stranger!"
  }

  println(greeting)

  var legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }
  println(legalStatus)

  /*
  Exercise.
   */
  val n: Int = 44
  val mathProperty = n match {
    case singleDigit(_) => "single digit" //
    case even() => "an even number"
    case _ => "no property"
  }

  object even {
    def unapply(n: Int): Boolean = n % 2 == 0
  }

  object singleDigit {
    def unapply(n: Int): Option[Boolean] = if (n < 10) Some(true) else None
  }

  println(mathProperty)

  //infix patterns
  case class Or[A, B](a: A, b: B) // Either

  val either = Or(2, "two")
  val humanDescription = either match {
    case number Or string => s"$number is written as $string" // the compiler rewrites that
  }
  println(humanDescription)

  // decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }


  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))

  val decomposed = myList match {
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "something else"
  }

  println(decomposed)

  // How this works? the compiler will look for unapply or unapplySeq and because we have _* in MyList(1, 2, _*),
  // it will look for unapplySeq. Then, it will find the unapplySeq method which takes a MyList as parameter and returns
  // an Option[Seq[A]]. At runtime, when the pattern matching is actually run, the values 1, 2, _* will be matched against
  // the sequence returned by unapplySeq on the given list

  // custom return types for unapply
  // isEmpty:Boolean, get: something

  abstract class Wrapper[T] {
//    def isEmpty: Boolean

    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person) = new Wrapper[String] {
      def isEmpty = false

      def get = person.name
    }
  }

  println(bob match {
    case (PersonWrapper(n)) => s"This is the person's name is $n"
    case _ => "An alien"
  })

  // if we remove the isEmpty method from PersonWrapper, the code will no lon

}

