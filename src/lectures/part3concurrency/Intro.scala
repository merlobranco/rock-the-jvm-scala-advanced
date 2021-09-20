package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {
  /*
    interface Runnable {
      public void run()
    }
   */
  // JVM threads
  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel")
  }
  val aThread = new Thread(runnable)

  aThread.start() // Gives the signal to the JVM to start a JVM thread
  // Create a JVM thread => (runs on a) OS thread
  runnable.run() // Doesn't do anything in parallel! Just calling a defined method
  aThread.join() // Blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
//  threadHello.start()
//  threadGoodbye.start()
  // Different runs produce different results

  // Executors. Useful for avoiding delays produced by starting and stopping the thread each time
  val pool = Executors.newFixedThreadPool(10)
//  pool.execute(() => println("Something in the Thread pool"))

//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("Done after 1 second")
//  })
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("Almost done")
//    Thread.sleep(1000)
//    println("Done after 2 second")
//  })

  pool.shutdown() // No more actions can be submitted
//  pool.execute(() => println("Should not appear")) // Throws an exception in the calling thread
//  pool.shutdownNow() // Interrupts the sleeping threads in the pool

  println(pool.isShutdown)

  /*
    Concurrency problems on the JVM
   */

  /*
    RACE CONDITION
    Two threads are trying to access to the same memory zone at the same time
   */
  def runInParallel = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })
    val thread2 = new Thread(() => {
      x = 2
    })
    thread1.start()
    thread2.start()
    println(x)
  }

//  for (_ <- 1 to 10000) runInParallel

  class BankAccount(var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(@volatile account: BankAccount, thing: String, price: Int) = {
    account.amount -= price // This operation is not atomic
//    println("I've bought " + thing)
//    println("My account is now " + account)
  }


  for (_ <- 1 to 10000) {
    val account = new BankAccount(50000)
    val thread1 = new Thread(() => buy(account, "shoes", 3000))
    val thread2 = new Thread(() => buy(account, "iPhone12", 4000))

    thread1.start()
    thread2.start()
    Thread.sleep(10)
    if (account.amount != 43000) println("AHA: " + account.amount)
//    println
  }

  /*
    thread1 (shoes): 50000
      - account = 50000 - 3000 = 47000
    thread2 (iphone12): 50000
      - account = 50000 - 4000 = 46000 overwrites the memory of account.amount
   */

  /*
    SOLUTIONS:
   */

  // OPTION #1: Use synchronized()

  def buySafe(account: BankAccount, thing: String, price: Int) = {
    account.synchronized {
      account.amount -= price // This operation is not atomic
      // No 2 threads can evaluate this at the same time
      println("I've bought " + thing)
      println("My account is now " + account)
    }
  }

  // OPTION #2: use @volatile annotated on a val or a var means all the reads and writes to it are synchronized

  /*
    EXERCISES

    1) Construct 50 "inception" threads
      Thread1 -> Thread2 -> Thread3 -> ...
      println("Hello from thread #n")
      in REVERSE ORDER

    2) Considering the code bellow
      1) What is the biggest value possible for x?
      2) What is the SMALLEST value possible for x?
   */
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x +=1))
  threads.foreach(_.start())


  /*
    3) Sleep fallacy
      What's the value of message?
      Is it guaranteed?
      Why? Why not
   */
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })

  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(2000)
  println(message)
}
