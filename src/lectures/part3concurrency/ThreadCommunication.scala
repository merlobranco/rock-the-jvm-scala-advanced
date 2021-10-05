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
          buffer.notify()
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
          buffer.notify()
        }
        Thread.sleep(r.nextInt(250))
      }
    })

    consumer.start()
    producer.start()
  }

  thirdApproach()
}
