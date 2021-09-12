package lectures.part2fp

object CurriesPAF extends App {

  // Curried functions: functions returning other functions
  val supperAdder: Int => Int => Int =
    x => y => x + y

  val add3 = supperAdder(3) // Int => Int = y => 3 + y
  println(add3(5))
  println(supperAdder(3)(5)) // Curried function

  // METHOD! We need to pass all parameter list
  // We cannot use methods in Higher order functions unless they are transformed into function values
  def curriedAdder(x: Int)(y: Int): Int = x + y // Curried method

  // Lifting: transforming methods into function values (ETA-EXPANSION)
  // Here we converted a method into a function value Int => Int
  // Functions != methods (JVM limitation)
  val add4: Int => Int = curriedAdder(4) // Here the type function should be provided

  // ETA-EXPANSION
  def inc(x: Int): Int = x + 1
  List(1,2,3).map(inc) // ETA-EXPANSION
  // The compiler translates it to List(1,2,3).map(x => inc(x))

  // Partial function applications
  // We the _ we are telling to the compiler to do an ETA - EXPANSION
  // Turning the method into a value function so we don't need to provide the function type like before
  val add5 = curriedAdder(5) _

  /*
    EXERCISE
    Having the following implementations
   */

  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  /*
    Then implement
      add7: Int => Int = y => 7 + y
      As many different implementations of add 7 using the above
      Be creative!!!
   */

  val add7= (x: Int) => simpleAddFunction(7, x) // Simplest
  val add7_2 = simpleAddFunction.curried(7) // Returns a function
  val add7_6 = simpleAddFunction(7, _: Int) // Works as well
  val add7_7: Int => Int = x => simpleAddFunction(7, x)

  val add7_3 = curriedAddMethod(7) _ // PAF
  val add7_4 = curriedAddMethod(7)(_) // PAF

  val add7_5 = simpleAddMethod(7, _: Int) // Alternative syntax for turning methods into function values
                                          // y => simpleAddMethod(7, y)

  // UNDERSCORES ARE POWERFUL

  def concatenator(a: String, b: String, c: String) = a + b +c
  val insertName = concatenator("Hello, I'm ", _: String, ", how are you?") // x: String => concatenator("Hello, I'm ", x, ", how are you?")
  println(insertName("Brais"))

  val fillInTheBlanks = concatenator("Hello, I'm ", _: String, _: String) // (x, y) => concatenator("Hello, I'm ", x, y)
  println(fillInTheBlanks("Brais", "I am learning Scala"))

  /*
    EXERCISES

    1. Process a list of numbers and return their String representations with different format
      Use the %4.2f, %8.6f and %14.12f with a curried formatter function
  */

  def curriedFormatter(s: String)(number: Double): String = s.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f") _ // Lift
  val seriousFormat = curriedFormatter("%8.6f") _ // Lift
  val preciseFormat = curriedFormatter("%14.12f") _ // Lift

  println(numbers.map(simpleFormat))
  println(numbers.map(seriousFormat))
  println(numbers.map(preciseFormat))

  println(numbers.map(curriedFormatter("%14.12f"))) // Compiler does Sweet ETA - EXPANSION for us


  /*
    EXERCISES

    2. Difference between
      - functions vs methods
      - parameters: By-name vs 0-lambda
   */
  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parentMethod(): Int = 42

  /*
    The exercise wants us to call byName and byFunction
    With the following expressions:
      - int
      - method
      - parentMethod
      - lambda
      - PAF
   */

  byName(23) // OK because 23 is a 'by Name' evaluate expression
  byName(method) // OK because the method will be evaluated through his code, which is 42
  byName(parentMethod()) // OK
  byName(parentMethod) // OK but beware ==> the Compiler will consider it as byName(parentMethod())
                       // byName is using the value NOT the function itself
//  byName(() => 42) // Not OK
  byName((() => 42)()) // Ok because we are calling the function which is providing a value
//  byName(parentMethod _)  // Not OK because we are providing a function value, and a function value is not a substitute for a binding parameter

//  byFunction(45) // Not OK
//  byFunction(method) // Not OK!!! because AGAIN the method is valued to his value 42
                       // Compiler does not do ETA-EXPANSION
  byFunction(parentMethod) // OK Compiler does ETA-EXPANSION
  byFunction(() => 42) // OK
  byFunction(parentMethod _) // OK. The compiler will translate to byFunction(parentMethod)
                             // So this approach is unnecessary
}
