package lectures.part2afp

object Monads extends App {

  // our own Try monad

  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Success[A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Failure(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*
  left-identity

  unit.flatMap(f) = f(x)
  Attempt(x).flatMap(f) = f(x) // Success case!
  Success(x).flatMap(f) = f(x) // proved
   */

  /*
  right-identity

  attempt.flatMap(unit) = attempt
  Success(x).flatMap(Attempt(_)) =  Success(x)
  Failure(_).flatMap(unit) = Failure(_)
   */

  /*
  associativity

  attempt.flatMap(f).flatMap(g) = attempt.flatMap(f(_).flatMap(g))
  Success(x).flatMap(f).flatMap(g) = f(x).flatMap(g) OR Failure(e)
  Success(x).flatMap(f(_).flatMap(g)) = f(v).flatMap(g) OR Failure(e) // equal to the left hand-side

  Failure(e).flatMap(f).flatMap(g) = Failure(e)
  Failure(e).flatMap(f(_).flatMap(g)) =  Failure(e) // satisfy the assoc
   */

  val attempt = Attempt {
    throw new RuntimeException("My new monad, yes!")
  }

  println(attempt)

  /*
   Exercise:
   1) Implement a lazy monad, Lazy[T] = abstracts away a computation which will be exec when it's needed

   unit/ apply
   flatMap

   2) Monads =  unit + flatMap
     An alternative way to define monads =  unit + map + flatten

     Monad[T] with a flatMap, how would you impl a map and flatten

     Monad[T]{

      def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

      def map[B](f: T => B): Monad[B] = ???
      def flatten(m:Monad[Monad[T]]): Monad[T] = ???
    }
   */

  class LazyMonad[+T](value: => T) {
    // cal by need
    private lazy val internalValue = value

    def use: T = internalValue

    def flatMap[U](f: (=> T) => LazyMonad[U]): LazyMonad[U] = f(internalValue) // make T param by name

    def map[U](f: T => U): LazyMonad[U] = this.flatMap(x => LazyMonad(f(x)))

  }

  object LazyMonad {

    def apply[T](value: => T): LazyMonad[T] = new LazyMonad(value)

    def flatten[T](m: LazyMonad[LazyMonad[T]]): LazyMonad[T] = m.flatMap(x => x)
  }

  val lazyMonad = LazyMonad {
    println("Today I don't feel like doing anything.")
    42
  }

  println(lazyMonad.use)
  println(lazyMonad.flatMap(x => LazyMonad(x * 10)).use)
  println(lazyMonad.map(_ * 100).use)
  println(
    LazyMonad
      .flatten(LazyMonad(LazyMonad {
        println("I'm the inner monad")
        10
      }))
      .use
  )
}
