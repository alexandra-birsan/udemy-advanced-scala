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

  // all the implementers of this type class template need to provide an implementation for this action

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

  // part 3
  implicit class HTMLEnrichment[T](value:T){

    def toHTML(implicit serializer:HTMLSerializer[T]):String = serializer.serialize(value)
  }

  println(john.toHTML(UserSerializer)) // rewritten by the compiler as println(HTMLEnrichment[User](john).toHtml(UserSerializer))
  // the compiler tries to wrap john in whatever implicit that has the toHtml method and takes a UserSerializer

  // the code above is equivalent to :
  println(john.toHTML) // because the UserSerializer is IMPLICIT
  /*
  - we can extend the functionality to new types
  - we can have different implementations for the same type ( we can choose the implementation)
  - super expressive <3
   */

  println(2.toHTML)

  /*
   -  type class itself HTMLSerialzer[T] {...}
   - type class instances (some of which are implicit) - - - UserSerializer, IntSerializer
   - conversion with implicit classes -- HTMLEnrichment
   */

  // context bounds
  def htmlBoilerplate[T](content:T)(implicit serializer: HTMLSerializer[T]) =
    s"<html><body> ${content.toHTML(serializer)} </body></html>"

  // the method above written in a nicer way
  // T: HTMLSerializer is a CONTEXT BOUND which tells the compiler to inject an implicit param of type HTMLSerializer
  // advantage: super compact method signature
  // Disadvantage: we can't pass a parameter by name (it will take the implicit one)
  def htmlSugar[T: HTMLSerializer](content:T):String =
    s"<html><body> ${content.toHTML} </body></html>"

  // this is the best of both worlds: you have the super compact method signature & you can choose the serializer implementation
  def htmlSugarImproved[T:HTMLSerializer](content:T):String = {
    val serializer = implicitly[HTMLSerializer[T]]
    // use serializer
    s"<html><body> ${content.toHTML(serializer)} </body></html>"
  }

  // implicitly
  case class Permissions(mask:String)

  implicit val defaultPermissions:Permissions = Permissions("Permissions with 0744")

  // in other part of the code we want to surface out what is the default value for Permissions
  val standartPerms = implicitly[Permissions]

}
