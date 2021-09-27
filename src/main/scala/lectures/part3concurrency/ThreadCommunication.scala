package lectures.part3concurrency

import lectures.part3concurrency.ThreadCommunication.consume

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  /*
  The producer - consumer problem

  producer -> [ ? ]-> consumer
   */
  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def setValue(newValue: Int) = value = newValue

    def get = {
      val result = value;
      value = 0;
      result
    }
  }

  def naiveProdCons() = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting ...")
      while (container.isEmpty) {
        println("[consumer] actively waiting")
      }
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing ...")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produced the value " + value)
      container.setValue(value)
    })

    consumer.start()
    producer.start()
  }

  def smartProducerConsumer() = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting ...")
      container.synchronized(container.wait())
      // consumer must have some value
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing ...")
      Thread.sleep(500)
      container.synchronized({
        container.setValue(400)
        println("[producer] I have set the value and notified")
        container.notify()
      })
    })

    consumer.start()
    producer.start()
  }

  //  smartProducerConsumer()

  //  naiveProdCons()

  /*
  A buffer where producers can produce values and consumers consume values
  producer -> [ ? ? ?] -> consumer

  both the producer and the consumer can block each other
   */

  def prodsConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]()
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()
      while (true) {
        buffer.synchronized({
          if (buffer.isEmpty) {
            println("[consumer] Buffer empty. Waiting...")
            buffer.wait()
          }
          // there must be at least one value in the buffer
          val x = buffer.dequeue()
          println("[consumer] I got the value " + x)

          buffer.notify() // hey, producer! are you sleeping?
        })
      }
      Thread.sleep(random.nextInt(500))
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i      = 0
      while (true) {
        buffer.synchronized({
          if (buffer.size == capacity) {
            println("[producer] The buffer is full")
            buffer.wait()
          }
          buffer.enqueue(i)
          println("[producer] Producing the value " + i)
          i = i + 1
          buffer.notify() // hey, consumer? are you lazy?

        })
      }
      Thread.sleep(random.nextInt(250))
    })

    consumer.start()
    producer.start()
  }

  //  prodsConsLargeBuffer()

  /*
  Prod -  cons level 3

  producer 1 ->  [ ? ? ? ] -> cons1
  producer 2 ->  same buffer -> cons2
  producer 2 ->  same buffer -> cons2
   */

  def prodsConsLargeBufferEnhanced(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]()
    val capacity      = 10
    val consumerTotal = 3;
    val producerTotal = 6;

    val consumers = Range
      .apply(1, consumerTotal)
      .map(i => new Thread(() => consume(buffer, i)))
      .toList
    val producers = Range
      .apply(1, producerTotal)
      .map(i => new Thread(() => produce(buffer, capacity, i)))
      .toList

    consumers.foreach(_.start())
    producers.foreach(_.start())
  }

  private def produce(buffer: mutable.Queue[Int], capacity: Int, index: Int) = {

    val random = new Random()
    var i      = 0
    while (true) {
      buffer.synchronized({
        while (buffer.size == capacity) {
          println(s"[producer $index] The buffer is full")
          buffer.wait()
        }
        buffer.enqueue(i)
        println(s"[producer $index] Producing the value " + i)
        i = i + 1
        buffer.notify() // hey, consumer? are you lazy?

      })
    }
    Thread.sleep(random.nextInt(2000))

  }

  private def consume(buffer: mutable.Queue[Int], i: Int) = {
    val random = new Random()
    while (true) {
      buffer.synchronized({
        while (buffer.isEmpty) {
          println("[consumer " + i + "] Buffer empty. Waiting...")
          buffer.wait()
        }
        // there must be at least one value in the buffer
        val x = buffer.dequeue()
        println("[consumer " + i + "] I got the value " + x)

        buffer.notify() // hey, producer! are you sleeping?
      })
    }
    Thread.sleep(random.nextInt(2000))

  }

  //  prodsConsLargeBufferEnhanced();
  /*
  Exercises.
  1. think of an example where notifyAll would behave differently
  2. create a deadlock
  3. create a livelock
   */

  // notifyAll
  def testNotifyAll() = {
    val bell = new Object
    (1 to 10).foreach(
      i =>
        new Thread(() => {
          bell.synchronized {
            println(s"[thread $i] waiting...")
            bell.wait()
            println(s"[thread $i] hooray!")
          }
        }).start()
    )

    new Thread(() => {
      Thread.sleep(2000)
      bell.synchronized {
        println("Rock'n roll!")
        bell.notifyAll()
      }
    }).start()
  }

//  testNotifyAll()

  // deadlock
  case class Friend(name: String) {
    def bow(other: Friend) = {
      println(s"$this: I am bowing to my friend  $other")
      other.rise(this)
      println(s"$this: my friend $other has risen")
    }

    def rise(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"

    def switchSide = {
      if (side == "right")
        side    = "left"
      else side = "right"
    }

    def pass(other: Friend) = {
      while (this.side == other.side) {
        println(s"Oh, but please, $other, feel free to pass...")
        this.switchSide
        Thread.sleep(1000)
      }
    }
  }

  val sam    = Friend("Sam")
  val pierre = Friend("Pierre")
//  new Thread(()=> sam.bow(pierre)).start()
//  new Thread(()=> pierre.bow(sam)).start()

  // 3. livelock
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()
}
