package lectures.part5typesystem

import lectures.part5typesystem

object TypeMembers extends App {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <:Animal // abstract type member upper bounded with Animal
    type SuperAnimal >: Dog <:Animal // abstract type  upper bounded with Dog and lower bounded with Animal
    type AnimalC = Cat // type alias

    val ac = new AnimalCollection
    val dog:ac.AnimalType = ??? // I cannot associate anything to it
    val cat:ac.BoundedAnimal = ??? // the same
    val pup:ac.SuperAnimal = new Dog // this works

    val cat2:ac.AnimalC = new Cat

  }
  type CatAlias = Cat // type alias
  val anotherCat:CatAlias = new Cat

  // abstract types are sometimes used in APIs that look similar to generics
  trait MyList {
    type T
    def add(elem:T):MyList
  }

  class NonEmptyList(value:Int) extends MyList{
    override type T = Int // override a type member

    override def add(elem: Int): MyList = ???
  }


  //.type
  val cat = new part5typesystem.TypeMembers.CatAlias
  type CatsType = cat.type  // this is a type alias
//  new CatsType -  this does not compile, we cannot use type aliases to instantiate

  /*
  Enforce a type to be applicable to some types only
   */
  trait MList{
    type A
    def head:A
    def tail:MList
  }

  trait ApplicableToNumbers{
    type A <:Number
    def head:A
    def tail:MList
  }

  // this should not compile
//  class CustomList(hd:String, tl:CustomList) extends MList with ApplicableToNumbers {
//     override type A = String
//     override def head = hd
//   override def tail = tl
//  }

  // this should compile
  class IntegerList(hd:Integer, tl:IntegerList) extends MList with ApplicableToNumbers {
    override type A = Integer
    override def head = hd
    override def tail = tl
  }

  // Number
  // type member and type member constraints (bounds)

}
