package lectures.part4implicits

import java.util.Date

object TypeClasses extends App {

  trait HTMLWritable {

    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age yo) </div>"
  }

  private val john: User = User("John", 32, "john@rockthejvm.com")
  john.toHtml

  /*
  Disadvantages
  1 - it only works for the type WE write
  2 - ONE implementation out of quite a number
   */

  // option 2 - pattern matching
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e) =>
    }
  }

  /*
  1 - we lose the type safety
  2 - need to modify the code every time
  3 - still ONE implementation
   */

  trait HTMLSerializer[T] {

    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {

    def serialize(value: User): String = s"<div>$value.name (${value.age} yo) </div>"

  }

  println(UserSerializer.serialize(john))

  /*
  Advantages:
  1 - we can write serializer for other types (that were not defined by us, e.g. java.util.Date)


   */
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(value: Date): String = s"<div>${value.toString} </div>"
  }

  // 2 - we can define multiple serializers (e.g. for User we can have one serializer only for the user name)

  //TYPE CLASS
  trait MyTypeClassTemplate[T] {

    def action(value: T): String
  }

  object MyTypeClassTemplate {

    def apply[T](implicit instance: MyTypeClassTemplate[T]): MyTypeClassTemplate[T] = instance // surface out the entire instance trait
  }

  // all the implementers of this type class template need to provide an implementation for this action

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

  // PART 2

  object HTMLSerializer {

    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    //an even better design
    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer // makes the compiler surface out the serializer of type T
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
  }

  println(HTMLSerializer.serialize(42))

  println(HTMLSerializer.serialize(john))

  // HTMLSerializer[User] gives us access to the entire type class trait (other methods of it as well)
  println(HTMLSerializer[User].serialize(john))

  // exercise: implement the type class pattern for the equality class
  val johnWithDifferentEmail = john.copy(email = "alex@rockthejvm.com")
  println(Equal[User](john, johnWithDifferentEmail)) // AD-HOC polymorphism: if 2 distinct types have equal implemented(e.g. User), then we can call equal on them.
  // Polymorphism because depending on the actual types of the values being compared, the compiler fetches the correct TYPE CLASS INSTANCE FOR OUR CLASS
  println(Equal[User](john, johnWithDifferentEmail)(NameAndEmailEquality))
}
