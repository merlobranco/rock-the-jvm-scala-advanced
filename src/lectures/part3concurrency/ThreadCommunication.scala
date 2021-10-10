package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  /*
    The Producer-Consumer Problem

    Producer -> [ X ] -> Consumer
   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0
    def set(newValue: Int) = value = newValue
    def get = {
      val result = value
      value = 0
      result
    }
  }

  def firstApproachProCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[Consumer] waiting...")
      while(container.isEmpty) {
        println("[Consumer] actively waiting...")
      }

      println("[Consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer] computing...")

      Thread.sleep(500)
      val value = 42
      println("[Producer] I have produced, after long work, the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

//  firstApproachProCons()

  // Wait and Notify
  def secondApproach(): Unit = {

    val container = new SimpleContainer

    val consumer = new Thread(() => {
      container.synchronized {
        container.wait()
      }
      println("[Consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer] Hard at work...")
      Thread.sleep(2000)
      val value = 42
      container.synchronized {
        println("[Producer] I am producing " + value)
        container.notify()
        container.set(value)
      }
    })

    consumer.start()
    producer.start()
  }

//  secondApproach()

  /*
    The Producer-Consumer Problem with Buffer

    Producer -> [ X X X ] -> Consumer
   */

  def thirdApproach(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3
    val r = Random

    val consumer = new Thread(() => {
      while(true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[Consumer] I am waiting. Buffer empty...")
            buffer.wait()
          }
          println("[Consumer] I have consumed " + buffer.dequeue())
          buffer.notifyAll()
        }
        Thread.sleep(r.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      while(true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[Producer] I am waiting. Buffer full...")
            buffer.wait()
          }
          val x = r.nextInt(100)
          println("[Producer] I have produced " + x)
          buffer.enqueue(x)
          buffer.notifyAll()
        }
        Thread.sleep(r.nextInt(250))
      }
    })

    consumer.start()
    producer.start()
  }

//  thirdApproach()

  /*
    The Producer-Consumer Problem with Buffer and several producers and consumers

    Producer1..N -> [ X X X ] -> Consumer1..N
   */

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run()
    {
      // We need to double check the status of the buffer after waking up
      while(true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"[Consumer $id] I am waiting. Buffer empty...")
            buffer.wait()
          }
          println(s"[Consumer $id] I have consumed " + buffer.dequeue())
          buffer.notify()
        }
        Thread.sleep(Random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run()
    {
      while(true) {
        buffer.synchronized {
          // We need to double check the status of the buffer after waking up
          while (buffer.size == capacity) {
            println(s"[Producer $id] I am waiting. Buffer full...")
            buffer.wait()
          }
          val x = Random.nextInt(100)
          println(s"[Producer $id] I have produced " + x)
          buffer.enqueue(x)
          buffer.notify()
        }
        Thread.sleep(Random.nextInt(500))
      }
    }
  }

  def fourthApproach(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    (1 to nConsumers).foreach(x => new Consumer(x, buffer).start())
    (1 to nProducers).foreach(x => new Producer(x, buffer, capacity).start())
  }

//  fourthApproach(3,3)

  /*
    Exercises
      1) Think of an example where notifyAll acts in a different way than notify
      2) Create a deadlock
      3) Create a lifelock (Threads are active, they are not blocked, but they cannot continue)
   */

  // 1) NotifyAll
  def testNotifyAll(): Unit = {
    val bell = new Object

    (1 to 10).foreach(x => new Thread(() => {
      bell.synchronized {
        println(s"[Thread $x] waiting...")
        bell.wait()
        println(s"[Thread $x] hooray!")
      }
    }).start())

    new Thread(() => {
      println("Ring the bells")
      Thread.sleep(2000)
      bell.synchronized {
        bell.notifyAll()
      }
    }).start()
  }

//  testNotifyAll()

  // 2) Deadlock
  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")

      }
    }
    def rise(other: Friend) = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"
    def switchSide(): Unit = {
      if (side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: Oh, but please, $other, feel free to pass...")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

//  new Thread(() => sam.bow(pierre)).start()
//  new Thread(() => pierre.bow(sam)).start()

  // 3) Lifelock
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()
}

