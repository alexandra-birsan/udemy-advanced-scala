package lectures.part2afp

import scala.annotation.tailrec

object StreamsPlayground extends App {

  /*
  Exercise: implement a lazily evaluated, singly linked STREAM of elements

  naturals = MyStream.from(1)(x=> x+1) =  stream of natural numbers (potentially infinite!)
  natural.take(100) // lazily evaluated stream of the first 100 naturals (finite stream)
  naturals.forEach println // will crash - infinite
  naturals.map(_ * 2)// stream of all even numbers (potentially infinite)
   */
  abstract class MyStream[+A] {
    def isEmpty: Boolean

    def head: A

    def tail: MyStream[A]

    def #::[B >: A](element:      B): MyStream[B] // preprend operator
    def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenate 2 streams
    def foreach(f:                A => Unit): Unit

    def map[B](f: A => B): MyStream[B]

    def flatMap[B](f: A => MyStream[B]): MyStream[B]

    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A]

    def takeList(n: Int): List[A]

    @tailrec
    final def toList[B >: A](acc: List[B] = Nil): List[B] =
      if (isEmpty) acc.reverse
      else tail.toList(head :: acc)
  }

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = {
      new NonEmptyStream(start, MyStream.from(generator(start))(generator))
    }
  }

  class EmptyStream[+A] extends MyStream[A] {

    override def isEmpty: Boolean = true

    override def head: A = throw new NoSuchElementException

    override def tail: MyStream[A] = throw new NoSuchElementException

    override def #::[B >: A](element: B): MyStream[B] = new NonEmptyStream[B](element, this)

    override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

    override def foreach(f: A => Unit): Unit = ()

    override def map[B](f: A => B): MyStream[B] = new EmptyStream[B]

    override def flatMap[B](f: A => MyStream[B]): MyStream[B] = new EmptyStream[B]

    override def filter(predicate: A => Boolean): MyStream[A] = this

    override def take(n: Int): MyStream[A] = this

    override def takeList(n: Int): List[A] = Nil
  }

  class NonEmptyStream[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {

    override def isEmpty: Boolean = false

    override val head: A = hd // !! val

    override lazy val tail: MyStream[A] = tl // call by need =  lazy + val

    /*
    val s  = new Cons(1, EmptyStream)
    val prepend = 1 #:: s =  new Cons(1,s)
     */
    override def #::[B >: A](element: B): MyStream[B] = new NonEmptyStream[B](element, this)

    override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] =
      new NonEmptyStream(hd, tail ++ anotherStream)

    override def foreach(f: A => Unit): Unit = {
      f(hd)
      tl foreach f
    }

    override def map[B](f: A => B): MyStream[B] = {
      new NonEmptyStream(f(hd), tail map f) // still preserves early evaluation
    }

    override def flatMap[B](f: A => MyStream[B]): MyStream[B] = {
      f(hd) ++ tail.flatMap(f)
      // initially Stackoverflow error: because in ++ the param is eagerly evaluated
    }

    override def filter(predicate: A => Boolean): MyStream[A] = {
      if (predicate(hd)) new NonEmptyStream(head, tail.filter(predicate))
      else tail.filter(predicate) // this will force the evaluation only of the first element in the tail =>
      // the lazy eval is still preserved
    }

    override def take(n: Int): MyStream[A] = {
      if (n <= 0) new EmptyStream[A]
      else if (n == 1) new NonEmptyStream[A](head, new EmptyStream[A])
      else new NonEmptyStream[A](head, tail.take(n - 1)) // still lazily evaluated
    }

    override def takeList(n: Int): List[A] = take(n).toList()

  }

  val naturals: MyStream[Int] = MyStream.from(1)(_ + 1)
  //  naturals take 10 foreach println
  //  new EmptyStream[Int] ++ new NonEmptyStream[Int](1,new NonEmptyStream[Int](2, new EmptyStream[Int])) foreach println
  //  2 #:: 1 #:: new EmptyStream[Int] foreach println

  val startFrom0 = 0 #:: naturals // right associative
  println(startFrom0.head)
  //  startFrom0.take(10000).foreach(println)
  //  println(startFrom0.map(_ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new NonEmptyStream(x, new NonEmptyStream(x + 1, new EmptyStream))).take(12).toList())

  println(startFrom0.filter(_ < 10).take(3).toList())

  // Exercises on streams
  // 1 - stream of Fibonacci numbers
  // 2- stream of prime with Eratosthenes sieve
  /*
  [2 3 4 5 ...]
  filter out all the numbers divisible by 2
  [2 3 5 7 9 11 ...]
  filter out all numbers divisible by 3
  [2 3 5 6 11 13 15 ...]
  filter out all numbers divisible by 5
  ...
   */

  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] = {
    new NonEmptyStream(first, fibonacci(second, first + second))
  }

  // println(fibonacci(1,1).take(100).toList())

  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] = {
    if (numbers.isEmpty) numbers
    else new NonEmptyStream(numbers.head, eratosthenes(numbers.tail.filter(_ % numbers.head != 0)))
  }

  println(eratosthenes(MyStream.from(2)(_ + 1)).take(1000).toList())
}
