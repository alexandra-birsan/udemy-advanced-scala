package lectures.part4implicits

object OrganizingImplicits extends App {

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  //  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _) // this will not compile

  println(List(1, 2, 3, 4, 2).sorted)

  // scala.Predef - package that is automatically imported when you write code

  /*
  Implicits ( used as implicit params)
   - val/ var
   - object
   - accessor methods = defs WITHOUT PARENTHESES
   */

  // exercise

  class AnObj {
  }

  object AnObj {
    //    implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)

  }

  case class Person(name: String, age: Int) extends AnObj

  //  implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age) // will take precedence over alphabetic ordering

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

  //    implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)

  object SomeObject {

    implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
    // if we leave the ordering here, the code no longer compiles - no ordering found. The compiler does not look at all the
    // objects created, only in those specified (companion objects, A or any superType). It will work if make this object a companion object of Person
  }

  //  println(persons.sorted)


  /*
  Implicit scope =  the places where the compiler looks for implicit values
    - normal scope = LOCAL SCOPE (highest priority) - it's the place where we write the code
    - imported scope
    - companion objects of all types involved in the method signature(e.g. sorted from List: List -> Ordering -> companion objects all the types involved = A or any supertype)
     !!! it will look only in the companion objects, if we declare the ordering in the super class, it won't compile
   */

  // if there are multiple "good" values for the implicit vals => put them in separate containers and make the user explicitly
  // import one container

  object AlphabeticOrdering {

    implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)

  }

  object AgeOrdering {

    implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)

  }

  def sortByAgeAndPrint() = {
    import AgeOrdering.ageOrdering

    println(persons.sorted)
  }

  def sortByNameAndPrint() = {
    import AlphabeticOrdering.alphabeticOrdering

    println(persons.sorted)
  }

  sortByAgeAndPrint()
  sortByNameAndPrint()

  /*
  Exercise

  - totalPrice =  most used (50%)
  - by unit count = 25%
  - by unit price = 25%
   */
  case class Purchase(nUnits: Int, unitPrice: Int)

  object Purchase {

    implicit val orderingByTotalPrice: Ordering[Purchase] = Ordering.fromLessThan((p1, p2) =>
      calculateTotalPrice(p1) < calculateTotalPrice(p2))

    private def calculateTotalPrice(p1: Purchase) = {
      p1.unitPrice * p1.nUnits
    }
  }

  object UnitPriceOrdering {

    implicit val orderingByUnitPrice: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)

  }

  object UnitCountOrdering {

    implicit val orderingByUnitCount: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)

  }

  val purchases = List(
    Purchase(2, 50),
    Purchase(12, 1),
    Purchase(15, 4)
  )

  println(purchases.sorted)
}
