package exercises

import lectures.part4implicits.TypeClasses.{HTMLSerializer, User, john}

object EqualityPlayground extends App {

  /**
   * Equality
   */
  trait Equal[T] {

    def apply(first: T, second: T): Boolean
  }

  implicit object Equal {

    def apply[T](first:T, second:T )(implicit equality: Equal[T]): Boolean = equality.apply(first, second)
  }

  implicit object NameEquality extends Equal[User] {

    override def apply(first: User, second: User): Boolean = first.name == second.name
  }

  object NameAndEmailEquality extends Equal[User] {

    override def apply(first: User, second: User): Boolean = NameEquality.apply(first, second) && first.email == second.email
  }

  // NameEquality and NameAndEmailEquality are type class instances

  // exercise: implement the type class pattern for the equality class
  private val john: User = User("John", 32, "john@rockthejvm.com")

  val johnWithDifferentEmail = john.copy(email = "alex@rockthejvm.com")
  println(Equal[User](john, johnWithDifferentEmail))
  // AD-HOC polymorphism: if 2 distinct types have equal implemented(e.g. User), then we can call equal on them.
  // Polymorphism because depending on the actual types of the values being compared, the compiler fetches the correct TYPE CLASS INSTANCE FOR OUR CLASS
  println(Equal[User](john, johnWithDifferentEmail)(NameAndEmailEquality))

  /*
  Exercise: improve the Equal TC with an implicit conversion class
   === (anotherValue: T)
   !==(anotherValue: T)
   */
  implicit class EqualEnhancer[T](value:T){

    def ===(anotherValue:T)(implicit equal: Equal[T]): Boolean = Equal(value, anotherValue)

    def !==(anotherValue:T)(implicit  equal: Equal[T]): Boolean = !Equal(value, anotherValue)
  }

  println(john===johnWithDifferentEmail) // EqualEnhancer(john).===(anotherValue)
  println(john!==johnWithDifferentEmail)
}
