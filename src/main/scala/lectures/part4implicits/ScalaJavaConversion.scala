package lectures.part4implicits

import java.util.Optional
import java.{util => ju}

import scala.collection.mutable // package alias
object ScalaJavaConversion extends App {

  import collection.JavaConverters._

  val javaSet: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  val scalaSet
    : mutable.Set[Int] = javaSet.asScala // JavaConverters has implicit conversion methods
  /*
  Iterator
  Iterable
  ju.List - collection.scala.mutable.Buffer
  ju.Set - collection.scala.mutable.Set
  ju.Map - collection.scala.mutable.Map
   */

  // converse from mutable.Scala to Java

  import collection.mutable._
  val numbersBuffer = ArrayBuffer[Int](1, 2, 3)
  private val juNumbersBuffer: ju.List[Int] = numbersBuffer.asJava

  println(juNumbersBuffer.asScala eq numbersBuffer) // returns true, so the conversion returns the initial reference. !!! Not all the conversions work like this.

  val numbers = List(1, 2, 3)
  val juNumbers = numbers.asJava
  val backToScala = juNumbers.asScala // does not return the initial reference. The original List is immutable

  println(backToScala eq numbers) // false. Shallow equals
  print(backToScala == numbers) // true. Deep equals

  /*
  Exercise
  create a Scala-Java Optional-Optional .asScala
   */

  // Optional(2).asScala = Some(2)

  class ToScala[T](value: => T) {

    def asScala: T = value
  }

  implicit def asScalaOptional[T](value: Optional[T]): ToScala[Option[T]] =
    new ToScala[Option[T]](if (value.isPresent) {
      Some(value.get())
    } else {
      None
    })

  println(Optional.of("Alex").asScala)
  println(Optional.empty().asScala)

}
