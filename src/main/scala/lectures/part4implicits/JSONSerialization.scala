package lectures.part4implicits

import java.util.Date

object JSONSerialization extends App {

  /*
  Users, posts, feeds
  Serialize to JSON
   */

  case class User(name:    String, age:       Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user:    User, posts:       List[Post])

  /*
  1 - intermediate data types: Int, String, List, Date
  2 - type classes for conversion to intermediate data types
  3 - serialize to JSON
   */

  sealed trait JSONValue { // intermediate data type
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = String.valueOf(value)
  }

  final case class JSONDate(value: Date) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {

    override def stringify: String =
      values
        .map {
          case (k: String, v: JSONValue) => JSONString(k).stringify + ":" + v.stringify
        }
        .mkString("{", ",", "}")

  }

  val data = JSONObject(
    Map(
      "user"  -> JSONString("Daniel"),
      "posts" -> JSONArray(List(JSONString("Scala rocks!"), JSONNumber(453)))
    )
  )

  println(data.stringify)
  // type class
  /*
  1 - type class
  2 - type class instances (implicit)
  3 - pimp library to use type class instances
   */

  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  implicit object IntConverter extends JSONConverter[Int] {
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  implicit object StringConverter extends JSONConverter[String] {
    override def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object UserConverter extends JSONConverter[User] {
    override def convert(value: User): JSONValue =
      JSONObject(
        Map(
          "name"  -> JSONString(value.name),
          "age"   -> JSONNumber(value.age),
          "email" -> JSONString(value.email)
        )
      )
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(value: Post): JSONValue =
      JSONObject(
        Map(
          "content"   -> JSONString(value.content),
          "createdAd" -> JSONDate(value.createdAt)
        )
      )
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(value: Feed): JSONValue =
      JSONObject(Map("user" -> value.user.toJSON, "posts" -> JSONArray(value.posts.map(_.toJSON))))
  }

  implicit class JSONEnhancer[T](value: T) {
    def toJSON(implicit jsconConverter: JSONConverter[T]): JSONValue = jsconConverter.convert(value)
  }
  // call stringify on the result
  val now = new Date(System.currentTimeMillis())
  println("Alex".toJSON.stringify)
  println(2.toJSON.stringify)
  val alexandra: User = User("Alexandra", 25, "a@b.com")
  println(alexandra.toJSON.stringify)
  println(
    Feed(
      alexandra,
      List(
        Post("hello", now),
        Post("look at this cute puppy", now)
      )
    ).toJSON.stringify
  )
}
