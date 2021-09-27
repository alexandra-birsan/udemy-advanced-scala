package lectures.part3concurrency

import org.graalvm.compiler.nodeinfo.StructuralInput.Condition

import scala.concurrent.{Await, ExecutionContextExecutor, Future, Promise}
import scala.util.{Failure, Random, Success, Try}

//important for Futures
import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration._

object FuturesAndPromises extends App {

  def calculateTheMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateTheMeaningOfLife // calculates the meaning of life on another thread
  }(global)

  println("Waiting for the future")
  aFuture.onComplete {
    case Success(value)     => println(s"The meaning of life is $value")
    case Failure(exception) => println(s"I failed with $exception")
  }(global) // some thread
  Thread.sleep(2000)

  // mini social network
  case class Profile(id: String, name: String) {

    def poke(anotherProfile: Profile) =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  implicit val context: ExecutionContextExecutor = global

  object SocialNetwork {
    // "database"

    val names = Map(
      "fb.id.1-zuk"   -> "Mark",
      "fb.id.2-bill"  -> "Bill",
      "fb.id-0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuk" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] =
      Future {
        // fetching from the db
        Thread.sleep(random.nextInt(300))
        val profile = Profile(id, names(id))
        println("Found profile")
        profile
      }(global)

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      val bf   = Profile(bfId, names(bfId))
      println("Found profile")
      bf
    }

    // client: mark poke bill
    //    val mark: Future[Profile] = SocialNetwork.fetchProfile("fb.id.1-zuk")
    //    mark.onComplete {
    //      case Success(markProfile) => {
    //        val bill = SocialNetwork.fetchBestFriend(markProfile)
    //        bill.onComplete {
    //          case Success(billProfile) => markProfile.poke(billProfile)
    //          case Failure(e) => e.printStackTrace()
    //        }
    //      }
    //      case Failure(ex) => ex.printStackTrace()
    //    }
    //    Thread.sleep(1000)

    // functional composition
    // map, flatMap, filter
    //    val nameOnTheWall: Future[String] = mark.map(profile => profile.name)
    //
    //    val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
    //
    //    val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("M"))

  }

  // for-comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuk")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown-id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Forever Alone")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown-id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown-id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  // online banking app
  case class User(name: String)

  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future {
      // simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      // simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from the DB
      // create a transaction
      // WAIT FOR THE TRANSACTION TO FINISH
      val eventualTransaction = fetchUser(username)
        .flatMap(user => createTransaction(user, merchantName, cost))
        .map(_.status)
      Await.result(eventualTransaction, 2.seconds) // implicit conversion -> pimp my library

    }

  }

  println(BankingApp.purchase("Daniel", "iPhone 12", "Apple", 2000))

  // promises
  val promise = Promise[Int]()
  private val future: Future[Int] = promise.future

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers ...")
    Thread.sleep(1000)
    // "fulfilling" the promise
    promise.success(42)
    println("[producer] done")
  })

  //  producer.start()
  //
  //  Thread.sleep(1000)

  // ex1: fulfill a future immediately with a value
  def fulfillWithValue(value: String) = {
    Future(value)
      .onComplete {
        case Success(_) => println(s"I have set the value $value")
        case Failure(e) => println(s"Error while setting the value: ${e.getMessage}")
      }
  }

  fulfillWithValue("Alex")
  Thread.sleep(1000)

  // ex2: inSequence
  def inSequence[T, U](fa: Future[T], fb: Future[U]) = fa.andThen(_ => fb)

  // ex3: first(fa, fb) => new future with the first value of the 2 futures
  def first[T](fa: Future[T], fb: Future[T]) = {
    val p = Promise[T]()
    val completeFirst: Try[T] => Unit = p tryComplete _
    List(fa, fb) foreach {
      _ onComplete completeFirst
    }
    p.future
  }

  first(Future {
    Thread.sleep(100)
    "Alex"
  }, Future {
    val value = "Birsan"
    value
  }).onComplete(value => print(value))

  // ex4: future with the last value
  def last[T](fa: Future[T], fb: Future[T]): Future[T] = {
    // 1 promise which both futures will try to complete
    // 2 promise which the LAST future will complete
    val bothPromise = Promise[T]
    val lastPromise = Promise[T]
    val checkAndComplete = (result: Try[T]) =>
      if (!bothPromise.tryComplete(result)) // tryComplete returns false if the promise has already been written, so that onComplete is called only once
        lastPromise.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  last(Future {
    "Alex2"
  }, Future {
    Thread.sleep(100)
    val value = "Birsan2"
    value
  }).onComplete(value => println(value))

  // retry until
  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] =
    action()
      .filter(condition) // will either pass with Success or it will fail with NoSuchElementException
      .recoverWith {
        case _ =>
          println("condition not satisfied!")
          retryUntil(action, condition)
      }

  val random = new Random()

  retryUntil[Int](
    () =>
      Future {
        Thread.sleep(100)
        val value = random.nextInt()
        println(s"GENERATED $value")
        value
      },
    value => value > 100
  )

  Thread.sleep(1000)
}
