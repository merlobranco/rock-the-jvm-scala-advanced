package lectures.part4implicits

/*
  TYPE CLASS
  Defines a collection of operations that could be apply to a certain type
  All the implementers of the Type class are called Type Class instances
 */
// TYPE CLASS TEMPLATE
trait MyTypeClassTemplate[T] {
  def action(value: T): String
}

object MyTypeClassTemplate {
  def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance
}