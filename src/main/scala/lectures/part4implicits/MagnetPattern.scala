package lectures.part4implicits

import scala.concurrent.Future

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

  trait MessageMagnet[Result]

}

