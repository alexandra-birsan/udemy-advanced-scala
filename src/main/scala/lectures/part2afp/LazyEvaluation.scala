package lectures.part2afp

import scala.runtime.Nothing$

object LazyEvaluation extends App {

  //lazy delays the evaluation of values
  lazy val x: Int = {
    println("Hello")
    42
  }

  println(x)

  println(x)

  // examples of implications
  // 1. side effects
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if (simpleCondition && lazyCondition) "yes" else "no")

  // BOO is not printed because the evaluation of lazyCondition is not needed

  // 2. call by name
  def byName(n: => Int): Int = n + n + n + 1 // the wait time is 3 times because the value is evaluated 3 times

  def retrieveMagicValue = {
    // side effect or long computation
    println("waiting")
    Thread.sleep(1000)
    42
  }

  // the wait time is 3 times because the value is evaluated 3 times

  println(byName(retrieveMagicValue))

  // use lazy vals
  def byName2(n: => Int): Int = {
    lazy val t = n
    t + t + t + 1 // CALL BY NEED - now we wait only for 1 evaluation
  }

  ///3. filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers filter lessThan30 // this does ETA-expansion
  val gt20 = lt30 filter greaterThan20
  println(gt20)

  val lt30lazy = numbers.withFilter(lessThan30) // withFilter uses lazy values under the hood
  val gt20Lazy = lt30lazy.withFilter(greaterThan20)
  println
  println(gt20Lazy)
  gt20Lazy foreach println // predicates are checked on a by need basis

  // for-comprehension use withFilter with guards
  for {
    a <- List(1, 2, 3) if a % 2 == 0 // !! if-guards use lazy vals
  } yield a + 1
  // this translates to
  List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1)

  }
