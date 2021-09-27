package lectures.part4implicits

object HigherKindedTypes extends App {

  trait AHigherKindedType[F[_]]

  trait MyList[T] {
    def flatMap[B](f: T => B): MyList[B]
  }

  trait MyOption[T] {
    def flatMap[B](f: T => B): MyOption[B]
  }

  trait MyFuture[T] {
    def flatMap[B](f: T => B): MyFuture[B]
  }

  //combine/multiply List(1,2) x List("a", "b") => List(1a, 1b,2a,2b)
  def multiply[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
    for {
      a <- listA
      b <- listB
    } yield (a, b)

  def multiply[A, B](optionA: Option[A], optionB: Option[B]): Option[(A, B)] =
    for {
      a <- optionA
      b <- optionB
    } yield (a, b)

  // how to write a reusable method? USE A HKT
  trait Monad[F[_], A] { // higher-kinded type class
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f:     A => B):    F[B]
  }

  implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)

    override def map[B](f: A => B): List[B] = list.map(f)
  }

  implicit class MonadOption[A](value: Option[A]) extends Monad[Option, A] {
    override def flatMap[B](f: A => Option[B]): Option[B] = value.flatMap(f)

    override def map[B](f: A => B): Option[B] = value.map(f)
  }

  val monadList = new MonadList[Int](List(1, 2))
  monadList.flatMap(x => List(x, x)) // List[Int]
  // Monad[List, Int] => List[Int]

  // by adding implicit: we force the compiler to search for wrappers over List and Option into their Monad counterparts
  def multiply[F[_], A, B](implicit monad1: Monad[F, A], monad2: Monad[F, B]): F[(A, B)] = {
    for {
      a <- monad1
      b <- monad2
    } yield (a, b)
    /*
    a.flatMap(a=> mb.map(b=> (a,b)))
   */
  }

  println(multiply(new MonadList(List(1, 2)), new MonadList(List("a", "v"))))
  println(multiply(new MonadOption(Some(1)), new MonadOption(Option("a"))))

  // because we used implicits
  println(multiply(List(1, 2), List("a", "v")))
  println(multiply(Some(1), Option("a")))
}
