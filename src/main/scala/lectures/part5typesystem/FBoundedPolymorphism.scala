package lectures.part5typesystem

object FBoundedPolymorphism extends App {

  // SOLUTION 1 - naive
//  trait Animal{
//    def breed():List[Animal]
//  }
//  class Cat extends Animal {
//    override def breed(): List[Cat] = ??? // what if I want to return a List[Cat], not a List[Animal]?
//  }
//  class Dog extends Animal {
//    override def breed(): List[Cat] = ??? // List[Dog]
//  }

  // we want each override to return a specific list

  // SOLUTION 2 - F-Bounded polymorphism
//  trait Animal[A <:Animal[A]]{ // recursive type: F-Bounded Polymorphism
//        def breed():List[Animal[A]]
//      }
//      class Cat extends Animal[Cat] {
//        override def breed(): List[Cat] = ??? // what if I want to return a List[Cat], not a List[Animal]?
//      }
//      class Dog extends Animal[Dog] {
//        override def breed(): List[Dog] = ??? // List[Dog]
//      }

  trait Entity[E <: Entity[E]] // present often in ORM
  class Person extends Comparable[Person] { // used for comparisons
    override def compareTo(o: Person): Int = ???
  }

  // mistake that I can make
//  class Crocodile extends Animal[Dog]{
//    override def breed(): List[Animal[Dog]] = ???
//  }

  // SOLUTION 3
  trait AnimalRoot[T] {
    type T
  }
  trait Animal[A <: Animal[A]] { self: A =>
//    override type T = V
    def breed(): List[A]
  }
  class Cat extends Animal[Cat] {
    override def breed(): List[Cat] = ??? // what if I want to return a List[Cat], not a List[Animal]?
  }
  class Dog extends Animal[Dog] {
    override def breed(): List[Dog] = ??? // List[Dog]
  }

  // downsides
  trait Fish extends Animal[Fish]
  class Shark extends Fish {
    override def breed(): List[Fish] =
      List(new Cod) // this is wrong. Once we bring the hierarchy down one level, then we
    // encounter this issue
  }
  class Cod extends Fish {
    override def breed(): List[Fish] = ???
  }

  // exercise
  // SOLUTION 4: TYPE CLASSES
  trait Animal2 {}
  class Cat2 extends Animal2 {}
  class Dog2 extends Animal2 {}

  trait AnimalBreeder[T <: Animal2] {

    def breed(value: T): List[T]
  }

  implicit object CatBreeder extends AnimalBreeder[Cat2] {
    def breed(cat: Cat2): List[Cat2] = ???
  }

  implicit object DogBreeder extends AnimalBreeder[Dog2] {

    def breed(dog: Dog2): List[Dog2] = ???
  }

  implicit class BreedOps[T <: Animal2](value: T) {

    def breed(implicit breeder: AnimalBreeder[T]) = breeder.breed(value)
  }

  new Dog2().breed
  /*
  new BreedOps(dog).breed -> search for implicit breeder => DogBreeder
   */
  new Cat2().breed

  // solution 5: the Animal trait being the type class itself
  trait Animal5[A] { // pure type class
    def breed(a: A): List[A]
  }

  class Dog5
  object Dog5 {
    implicit object DogAnimal extends Animal5[Dog5] {
      override def breed(a: Dog5): List[Dog5] = ???
    }
  }

  class Cat5
  object Cat5 {
    implicit object CatAnimal extends Animal5[Cat5] {
      override def breed(a: Cat5): List[Cat5] = ???
    }
  }

  implicit class AnimalOps[A](value: A) {

    def breed(implicit animal5: Animal5[A]) = animal5.breed(value)
  }

  new Dog5().breed
}
