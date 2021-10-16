package lectures.part3concurrency

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}

// Important for Futures
// ExecutionContext handles thread allocation of Futures
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // Calculates the meaning of life on ANOTHER thread
  } // (global) is available here and passed by the compiler

  println(aFuture.value) // Returns a Option[Try[Int]], not the proper way of calling it

  println("Waiting for the future to finish")
  aFuture.onComplete(t => t match { // Manages a Try[Int]
    case Success(meaningOfLive) => println(s"The meaning of life is $meaningOfLive")
    case Failure(e) => println(s"I have failed with $e")
  })

  // Same as the following partial function
  aFuture.onComplete { // Manages a Try[Int]
    case Success(meaningOfLive) => println(s"The meaning of life is $meaningOfLive")
    case Failure(e) => println(s"I have failed with $e")
  } // The callback represented by the partial function will be call by some thread, we don't know which one

  Thread.sleep(3000)

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // Database
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.3-dummy" -> "Dummy"
    )
    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )
    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      // Fetching from the DB
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val friendId = friends(profile.id)
      Profile(friendId, names(friendId))
    }
  }

  // Client: Mark to poke friend Bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
//  mark.onComplete {
//    case Success(markProfile) => {
//      val bill = SocialNetwork.fetchBestFriend(markProfile)
//      bill.onComplete {
//        case Success(billProfile) => markProfile.poke(billProfile)
//        case Failure(e) => e.printStackTrace()
//      }
//    }
//    case Failure(e) => e.printStackTrace()
//  }

  // Functional composition of futures
  // map, flatMap, filter
  // Much better approach of the provided solution above
  val nameOnTheWall = mark.map(profile => profile.name)
  nameOnTheWall.onComplete {
    case Success(name) => println(name)
  }

  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  marksBestFriend.onComplete {
    case Success(profile) => println(profile.name)
  }

  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))
  zucksBestFriendRestricted.onComplete {
    case Success(profile) => println(profile.name)
    case Failure(_) => println("There is no friend staring with Z")
  }

  // For-comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill) // Shortest version of "yield mark.poke(bill)"

  Thread.sleep(1000)

  // Fallbacks
  // 1) Recovery fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
  }

  // Will trigger an error on the second future
  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  // Despite the error will be triggered on the second future, it will throw its own error
  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  // Promises
  // Sometimes we need to block on a future with a promise pattern

  // Online banking app
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM banking"

    // API
    def fetchUser(name: String): Future[User] = Future {
      // Simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // Fetch the user from the DB
      // Create a transaction
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status
      Await.result(transactionStatusFuture, 2.seconds) // 2.seconds is coming from implicit conversions -> Pimp my library
    }
  }

  println(BankingApp.purchase("Daniel", "iPhone 12", "Rock the jvm store", 3000))

  // Manual manipulation of Futures with Promises

  val promise = Promise[Int]() // A Promise is a kind of Controller over a Future
  val future = promise.future

  // Thread 1 - "Consumer"
  future.onComplete {
    case Success(r) => println(s"[Consumer] I've received $r")
  }

  // Thread 2 - "Producer"
  val producer = new Thread(() => {
    println("[Producer] crunching numbers...")
    Thread.sleep(500)
    // "Fulfilling" the promise
    promise.success(42)
    println("[Producer] done")
  })
  producer.start()
}
