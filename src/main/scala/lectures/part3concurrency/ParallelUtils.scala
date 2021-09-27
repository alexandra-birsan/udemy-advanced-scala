package lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

import scala.collection.parallel.immutable.ParVector
import scala.collection.parallel.CollectionConverters._

object ParallelUtils extends App {

  // 1 - parallel collection

  val parList = List(1, 2, 3).par

  val aParVector = ParVector[Int](1, 2, 3)

  /*
  Seq
  Vector
  Array
  Map - Hash, Trie
  Set - Hash, Trie
   */

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list       = (1 to 100000).toList
  val serialTime = measure(list.map(_ + 1))
  val parallelTime = measure {
    list.par.map(_ + 1)
  }

  println("serial time " + serialTime)
  println("parallel time " + parallelTime)

  // !! when the upper range is lower (e.g. 10000), the serial time is smaller than parallel time
  // because parallel collections operate on the map-reduce model
  // a parallel collection will split the input into chunks (using a Splitter)-> then perform the operation on each chunck
  // then combine the results using a Combiner

  // map, flatMap, filter, foreach, reduce, fold

  // !!! fold, reduce with non-associative operators
  println(List(1, 2, 3).reduce(_ - _)) // -4
  println(List(1, 2, 3).par.reduce(_ - _)) // !! we don't know the exact result because we don't know the order in which the values will be brought in !!

  // synchronization
  var sum = 0
  List(1, 2, 3).par.foreach(sum += _)
  println(sum) // !! race conditions!

  // configuring a parallel collection
//  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2)) // new thread manager??
  // task support = an internal member of a parallel collection
  /*
  alternatives
   - ThreadPoolTaskSupport - deprecated
   - ExecutionContextTaskSupport(EC)
   */
  //  aParVector.taskSupport = new TaskSupport{
  //      ...
  //  }

  // 2 - atomic ops and references
  val atomic = new AtomicReference[Int](2)
  // atomic types have atomic operations
  val currentValue = atomic.get() // thread-safe read
  atomic.set(4) // thread-safe write

  atomic.getAndSet(5)

  atomic.compareAndSet(38, 56)
  // if the value is 38, then set it to 56, otherwise, do nothing
  //reference equality, shallow equality, not deep

  atomic.updateAndGet(_ + 1) // thread-safe function run
  atomic.getAndUpdate(_ + 1)

  atomic.accumulateAndGet(12, _ + _) // thread-safe accumulation
  atomic.getAndAccumulate(12, _ + _)

  val atomic2 = new AtomicInteger(6)
  atomic2.accumulateAndGet(12, _ + _)
  println(atomic2)

}
