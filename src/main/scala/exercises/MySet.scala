package exercises

object FunctionalCollectionsPlayground extends App {

  trait MySet[A] extends (A => Boolean) {
    /*
  EXERCISE - implement a functional set
     */
    def contains(elem: A): Boolean

    def +(elem: A): MySet[A]

    def ++(anotherSet: MySet[A]): MySet[A]

    def map[B](f: A => B): MySet[B]

    def flatMap[B](f: A => MySet[B]): MySet[B]

    def filter(predicate: A => Boolean): MySet[A]

    def foreach(f: A => Unit): Unit

    def -(element: A): MySet[A]

    def &(another: MySet[A]): MySet[A]

    def --(another: MySet[A]): MySet[A]

    def unary_! : MySet[A]

    def apply(elem: A): Boolean = this.contains(elem)
  }

  class EmptySet[A] extends MySet[A] {
    override def contains(elem: A): Boolean = false

    override def +(elem: A): MySet[A] = NonEmptySet(elem, this)

    override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

    override def map[B](f: A => B): MySet[B] = new EmptySet[B]

    override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

    override def filter(predicate: A => Boolean): MySet[A] = this

    override def foreach(f: A => Unit): Unit = ()

    override def -(element: A): MySet[A] = this

    override def &(another: MySet[A]): MySet[A] = this

    override def --(another: MySet[A]): MySet[A] = this

    override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
  }

  case class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {

    override def contains(elem: A): Boolean =
      if (head == elem) true
      else tail.contains(elem)

    override def +(elem: A): MySet[A] =
      if (this.contains(elem)) this
      else NonEmptySet(elem, this)

    override def ++(anotherSet: MySet[A]): MySet[A] =
      tail ++ anotherSet + head

    override def map[B](f: A => B): MySet[B] = NonEmptySet(f(head), tail.map(f))

    override def flatMap[B](f: A => MySet[B]): MySet[B] = f(head) ++ tail.flatMap(f)

    override def filter(predicate: A => Boolean): MySet[A] =
      if (predicate(head)) NonEmptySet(head, tail.filter(predicate)) else tail.filter(predicate)

    override def foreach(f: A => Unit): Unit = {
      f(head)
      tail.foreach(f)
    }

    override def -(element: A): MySet[A] = filter(a => a != element)

    override def &(another: MySet[A]): MySet[A] =
      //      if (another.contains(head)) NonEmptySet(head, new EmptySet[A]) ++ this.tail.&(another)
      //      else this.tail.&(another)
      filter(another) // intersection and filtering are the same thing because our set is functional (anotherSet.contains(x) = anotherSet(x))

    override def --(another: MySet[A]): MySet[A] =
      if (!another.contains(this.head)) NonEmptySet(head, new EmptySet[A]) ++ this.tail.--(another)
      else this.tail.--(another)

    // implement a unary_! = negation of a set
    // set[1,2,3] => return all the elements except those not in the set
    def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this(x))
  }

  // all elements of type A which satisfy a property
  // {x in A | property(x)}
  class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {

    override def contains(elem: A): Boolean = property(elem)

    // { x in A | property(x)} + elem = { x in A | property(x) || x == elem}
    def +(elem: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || x == elem)

    def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet(x))

    // all integers => (_%33) => [0,1,2]
    def map[B](f: A => B): MySet[B] = politelyFail

    def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

    def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))

    def foreach(f: A => Unit): Unit = politelyFail

    def -(element: A): MySet[A] = filter(x => x != element)

    def &(another: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) && another(x))

    def --(another: MySet[A]): MySet[A] = filter(!another)

    def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

    def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole!")
  }

  private val value: NonEmptySet[Int] = NonEmptySet(1, NonEmptySet(2, new EmptySet[Int]))
  //  value foreach println
  //  println(value.contains(1000))
  //  println(value.contains(1))
  //  println(value.contains(2))
  //  value map (x => x + 2) foreach println
  //  value flatMap (x => NonEmptySet(x * 10, new EmptySet[Int])) foreach println
  //  value + 7 foreach println
  //    value ++ NonEmptySet(5, NonEmptySet(6, new EmptySet[Int])) foreach println

  /*
  EXERCISE
  - removing an element
  - intersection with another element
  - difference with another set
   */
  //  value - 2 foreach println
  //  value - 10 foreach println
  //  value -- new EmptySet[Int] foreach println
  //  value -- NonEmptySet(1, NonEmptySet(10, new EmptySet[Int])) foreach println
  //  value -- NonEmptySet(1, NonEmptySet(2, new EmptySet[Int])) foreach println
  //  value & NonEmptySet(1, NonEmptySet(2, new EmptySet[Int])) foreach println
  //  value & NonEmptySet(1, NonEmptySet(10, new EmptySet[Int])) foreach println
  //  value & new EmptySet[Int] foreach println
  val negative = !value // all the naturals not equal to 1 , 2
  //  println(negative(2))
  //  println(negative(5))

  val negativeEven = negative filter (_ % 2 == 0)
//  println(negativeEv

  val negativeEven5 = negativeEven + 5
  println(negativeEven5(5))

}
