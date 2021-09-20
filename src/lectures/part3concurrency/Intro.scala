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
  threadHello.start()
  threadGoodbye.start()
  // Different runs produce different results

  // Executors. Useful for avoiding delays produced by starting and stopping the thread each time
  val pool = Executors.newFixedThreadPool(10)
  pool.execute(() => println("Something in the Thread pool"))

  pool.execute(() => {
    Thread.sleep(1000)
    println("Done after 1 second")
  })
  pool.execute(() => {
    Thread.sleep(1000)
    println("Almost done")
    Thread.sleep(1000)
    println("Done after 2 second")
  })

  pool.shutdown() // No more actions can be submitted
//  pool.execute(() => println("Should not appear")) // Throws an exception in the calling thread
//  pool.shutdownNow() // Interrupts the sleeping threads in the pool

  println(pool.isShutdown)


}
