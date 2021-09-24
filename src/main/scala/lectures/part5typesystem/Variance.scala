package lectures.part5typesystem

object Variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // what is variance
  // "inheritance" - type substitution of generics

  class Cage[T] // the question is should a Cage[Cat] be a subtype of Cage[Animal]?
  // yes - covariance - can replace a general case with it CCage[Cat]
  class CCage[+T] // this will be covariant
  val ccage: CCage[Animal] = new CCage[Cat]

  // no - invariance
  // the types are completely different
  class ICage[T] // this type cannot be replaced by another one
  val icage: ICage[Cat] = new ICage[Cat]
  // similar to val x:Int = "Alex"

  // hell no - opposite =  contravariance
  class XCage[-T] extends Animal
  val xcage: XCage[Cat] = new XCage[Cat]
  // I'm replacing a specific Cage[Cat] with a general Cage[Animal]

  class InvariantCage[T](animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal:T) // COVARIANT position:in this position the compiler accepts a field declared with a covariant type
  // covariant positions (e.g. class field declaration) also accept invariant types
  val ccage2:CCage[Animal] = new CCage[Cat]
//  class ContravariantCage[-T](val animal:T) // compilation error: contravariant type occurs in covariant position
// if the compiler had allowed the declaration above:
  // val catCage : XCage[Cat] = new XCage[Animal](new Crocodile)
//  class CovariantVariableCage[+T](var animal: T) // compilation error: covariant type occurs in contravariant position
  // !!! types of vars are in CONTRAVARIANT positions
  // if the compiler had allowed the declaration above:
  /*
  val ccage: CCage[Animal] = new CCage[Cat](new Cat)
  ccage.animal = new Crocodile
   */
//  class ContraVariantCage[-T](var animal:T) // compilation: contravariant type occurs in covariant position
  // the only acceptable type for a var field is invariant

  class InvariantVariableCage[T](var animal: Animal) // ok

//  trait AnotherCovariantCage[+T]{
//    def addAnimal(animal: T) // !!! method args in CONTRAVARIANT position
//  }

  // if the code above compiled => I would be able to write:
  /*
  val ccage: CCage[Animal] = new CCage[Dog]
  ccage.add(new Cat)
   */
  class AnotherContraVariantCage[-T]{
    def addAnimal(animal: T) = true
  }
 val acc:AnotherContraVariantCage[Cat] =  new AnotherContraVariantCage[Animal]
 acc.addAnimal(new Cat)
//  acc.addAnimal(new Dog) // does not compile
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  class MyList[+A] {
    def add[B>:A](element:B):MyList[B] = new MyList[B] // !!! WIDENING THE TYPE
  }
  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat) // without the hack above this would work
  val evenMoreAnimals: MyList[Animal] = moreAnimals.add(new Dog) // the compiler widens the return type to the closest parent: Animal

  // the method arguments are in contravariant position

  // return type
  class PetShop[-T] {
//    def get(isItAPuppy:Boolean): T  // METHOD RETURN TYPES ARE IN COVARIANT POSITIONS

    def get[S <: T](isItAPuppy:Boolean, defaultAnimal:S):S = defaultAnimal
  }

  // if the code above works:
  /*
  val catShop = new PetShop[Animal] {
  def get(isItAPuppy: Boolean):Animal = new Cat
  }
  val dogShop:PetShop[Dog] = catShop
  dogShop.get(true) // EVIL CAT!
   */
  val shop:PetShop[Dog] =  new PetShop[Animal]
  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
  Big rule:
  - method args are in CONTRAVARIANT position
  - method return types are in COVARIANT position
   */

/*
  1. invariant, covariant , contravariant
     Parking[T](things:List[T]){
      park(vehicle:T)
      impound(vehicle:List[T])
      checkVehicles(condition:String): List[T]
     }

   2. use someone else's API: IList[T]
   3. Parking = monad
     - flatMap
 */

class Vehicle
class Bike extends Vehicle
class Car extends Vehicle
class IList[T]

  class IParking[T](vehicles: List[T]){

    def park(vehicle: T) = "parked"
    def impound(vehicles: List[T]) = "impound"
    def checkVehicles(condition:String):List[T] = vehicles
    def flatMap[V](f:IParking[T] => IParking[V] ) = ???

  }

  class CParking[+T](vehicles: List[T]){

    def park [B>:T](vehicle: B) = "parked"
    def impound[B>:T](vehicles: List[B]) = "impound"
    def checkVehicles(condition:String):List[T] = vehicles
    def flatMap[V](f:CParking[T] => CParking[V] ) = ???
  }

  class ContraParking[-T](vehicles: List[T]){

    def park (vehicle: T) = "parked"
    def impound(vehicles: List[T]) = "impound"
    def checkVehicles[B<:T](condition:String):List[B] = List.empty
    def flatMap[B<:T, V](f:CParking[B] => CParking[V] ) = ???
  }


  class CParking2[+T](vehicles: IList[T]){

    def park [B>:T](vehicle: B) = "parked"
    def impound[B>:T](vehicles: IList[B]) = "impound" // T is covariant, but IList is invariant, so we need to apply the hack again
    def checkVehicles[B>:T](condition:String):IList[B] = new IList[B]()
  }

  class ContraParking2[-T](vehicles: IList[T]){

    def park (vehicle: T) = "parked"
    def impound[B<:T](vehicles: IList[B]) = "impound"
    def checkVehicles[B<:T](condition:String):IList[B] = new IList[B]()
  }

  // flatMap
  class Parking[T](vehicles:List[T]){

    def flatMap[V](f:Parking[T] => Parking[V] ) = f.apply(this)
  }
}
