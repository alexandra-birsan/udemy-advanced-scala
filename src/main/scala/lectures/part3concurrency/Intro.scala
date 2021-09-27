package lectures.part3concurrency

import java.util
import java.util.concurrent.Executors

object Intro extends App {

  // interface Runnable {
  // public void run()
  //}
  // JVM threads
  val aThread = new Thread(() => {
    println("Running in parallel")
  })

  aThread.start() // gives the signal to the JVM to start a JVM thread
  // create a JVM thread => OS thread
  // runnable.run() doesn\t do anything in parallel
  aThread.join() // blocks until aThread finishes running

  val threadHello   = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
  //  threadHello.start()
  //  threadGoodbye.start()
  // different runs produce different results!

  // executors
  val pool = Executors.newFixedThreadPool(10)
  //  pool.execute(() => println("Something in the thread pool"))
  //
  //  pool.execute(() => {
  //    Thread.sleep(1000)
  //    println("done after 1 second")
  //  })
  //
  //  pool.execute(() => {
  //    Thread.sleep(1000)
  //    println("almost done")
  //    Thread.sleep(1000)
  //    println("done after 2 seconds")
  //  })

  //  pool.shutdown()
  //  pool.execute(()=> println("Should not appear")) // throws exception in the MAIN THREAD (the calling thread)
  // pool.isShutdown()  - true
  //  pool.shutdownNow() // nothing printed because the pool interrupted the running threads

  def runInParallel = {
    var x = 0

    val thread1 = new Thread(() => x = 1)
    val thread2 = new Thread(() => x = 2)

    thread1.start()
    thread2.start()
    println(x)

  }

  //  for (_ <- 1 to 100) runInParallel

  //race condition

  class BankAccount(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }

  //  def buy(account: BankAccount, thing: String, price: Int) = {
  //    account.amount -= price
  //    println("I've bought " + thing)
  //    println("My account is now " + account)
  //  }

  //  for (_ <- 1 to 1000) {
  //    val account = new BankAccount(50000)
  //    val thread1 = new Thread(() => buy(account, "shoes", 3000))
  //    val thread2 = new Thread(() => buy(account, "iPhone12", 4000))
  //
  //    thread1.start()
  //    thread2.start()
  //    Thread.sleep(10)
  //    if (account.amount != 43000) println("AHA: " + account.amount)
  //  }

  /*
  thread1 (shoes): 50000
   - acoount = 50000 = 3000 = 47000
  thread2 (iPhone): 50000
   - account = 50000 = 4000 = 46000 overwrites the memory of account.amount
   */

  // option #1: use synhronize()
  //  def buySafe(account: BankAccount, thing: String, price: Int) =
  //    account.synchronized {
  //      // no 2 threads can evaluate this at the same time
  //      account.amount -= price
  //      println("I've bought " + thing)
  //      println("My account is now " + account)
  //    }

  // option #2:use @volatile

  /*
  Exercises

  1) Construct 50 "inception" threads
      Thread 1 -> thread 2-> ..
      println("hello from thread #3)

      in REVERSE ORDER
   */

  def inception(maxThreads: Int, index: Int): Thread =
    new Thread(() => {
      if (index < maxThreads) {
        val newThread = inception(maxThreads, index + 1)
        newThread.start()
        newThread.join()
      }
      println(s"Hello from thread $index")
    })

  inception(50, 1).start()
  /*
  2)
   */
  var x = 0
  val threads: Seq[Thread] = (1 to 100).map(_ => new Thread(() => x += 1))
  /*
  1. what is the biggest possible value for x? // 100
  2. what is the smallest value possible for x // 1 - when all threads read x=0
   */

  threads.foreach(_.start())
  threads.foreach(_.join())
  println(x)
  /*
  3. sleep fallacy
   */
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })
  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(1001)
  awesomeThread.join()
  println(message)
  /*
  what's the value of the message? // almost always Scala is awesome
  is it guaranteed? // not guaranteed
  why? why not?

  (main thread)
    message = "Scala sucks"
    awesomethread.start()
    sleep() - relieves exec
   (awesome thread)
     sleep() - relieves exex
    (OS gives the CPU to some important thread - takes CPU for more than 2 seconds)
    (OS gives the CPU back to the main thread)
      println("Scala sucks")
     (OS gives the CPU back to the awesomethread)
        message = "Scala is awesome"
   */

  // how to fix this?
  // synchronize doesn't work here
}
