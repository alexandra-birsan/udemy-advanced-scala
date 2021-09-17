package lectures.part4implicits

import scala.concurrent.{ExecutionContext, Future}

object MagnetPattern extends App {

  // method overloading
   class P2PRequest
  class P2PResponse
  class Serializer[T]
  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest):Int
    def receive(response:P2PResponse):Int
    def receive[T: Serializer](message:T): Int // context bound
    def receive[T:Serializer] (message:T, statusCode:Int):Int
    def receive(future:Future[P2PRequest]):Int
    // lots of overloads

    // Issues with this implementation:
    /*
        1 - type erasure (we can't have another method that accepts a Future[P2PResponse])
        2 - lifting doesn't work for all overloads
            val receiveFV = receive _ // ?!
        3 - code duplication
        4 - type inference and default args (if we had a default arg, actor.receive(?) would use which impl?)
     */
  }

  trait MessageMagnet[Result]{

    def apply():Result
  }

  def receive[R](magnet:MessageMagnet[R]):R =  magnet()

  implicit class FromPsPRequest(request:P2PRequest) extends MessageMagnet[Int]{

    override def apply(): Int = {
      // logic for handling a P2P request
      println("Handling P2P request")
      42
    }
  }

  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logic for handling a PSPResponse
      println("Handling P2P response")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse) // an implicit conversion is made from a P2PResponse to a message magnet, which will then be sent to the
  // receive method, which will then call the apply method, which has our handling logic

  /*
  MAGNET PATTERN
  benefits:
  - no more type erasure problems!
   */

  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int]{

    override def apply(): Int = 4
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  // 2 - lifting works
  trait MathLib {

    def add1(x:Int): Int = x+ 1
    def add1(s:String): Int = s.toInt + 1
    // add1 overloads
  }

  // "magnetize": magnet trait, magnet method, implicit conversions
  trait AddMagnet { // OBS: it has not type param here
    def apply(): Int
  }

  def add1(magnet:AddMagnet):Int = magnet() // a magnet method

  implicit class AddInt(x:Int) extends AddMagnet {
    override def apply(): Int = x +1
  }

  implicit class AddString(s:String) extends AddMagnet {
    override def apply(): Int = s.toInt + 1
  }

  val addFV = add1 _
  println(addFV(1)) // we can lift our magnet function for use in HOF. It works because we didn't put the type param to the AddMagnet trait.
  // The compiler would not know what it is
  println(addFV("3"))

  /*
  Drawbacks:
  - super verbose
  2 - the API is harder to read
  3 - you can't name or place default args (receive() ?)
  4 - call by name does not work correctly (hint: side effects)
   */

  // tricky to use with methods that deal with side effects
  trait HandleMagnet{
    def apply():Unit
  }

  def handle(magnet: HandleMagnet) = magnet()

  implicit class StringHandle(s:String) extends HandleMagnet {
    override def apply(): Unit = {
     println(s)
     println(s)
    }
  }

  handle({
    println("Hello!") // this one is not wrapped into a StringHandle!
    "magnet" // only this value is wrapped into a StringHandle
  })
  // if you use the magnet pattern for logging, you may have some logs missing :D
}

