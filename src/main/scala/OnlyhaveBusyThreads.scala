import java.util.concurrent.Callable

/**
  * A Future is a runnable that in the run method calls a Callable, stores the result (or an exception if not successful) and
  * unparks any waiting threads
  * The Java version of it also has a get() method that parks the calling thread to sleep until the result is available
  *
  *
  * https://www.artima.com/insidejvm/ed2/threadsynch.html
  */
case class SyncObject() {
  def addThreadToWaitQueue = wait()
  def awakeAndRemoveThreadsFromWaitQueue = notifyAll()
}

class MyFutureTask[T](c: Callable[T]) extends Runnable {

  var outcome: T = null.asInstanceOf[T]
  var syncObj = SyncObject()


  override def run(): Unit = {

    val result = c.call()
    outcome = result
    syncObj.synchronized { syncObj.awakeAndRemoveThreadsFromWaitQueue }
  }

  def get() = {
    println(
      s"""
         |
         |==>idle-waiting in ${Thread.currentThread}. No CPU now, but still a thread consuming memory doing nothing - just waiting to be awoken""".stripMargin)

    syncObj.synchronized{
      while (outcome==null) {
        syncObj.addThreadToWaitQueue }
    }
    outcome
  }
}

object MyExecutor {

  /** run the callable code in another thread */
  def submit[T](c: Callable[T]) = {

    val ft = new MyFutureTask[T](c)
    val t = new Thread(ft)
    t.start()

    ft

  }
}

/**
  * We want the minimum number of threads.
  * Ie. only threads that really do work.
  * Ie. no threads that do nothing.
  * And not at all any threads that are just busy waiting.
  */
object OnlyhaveBusyThreads extends App{

  var i = 0
  val callable = new Callable[String] {
    override def call(): String = {
      println(Thread.currentThread())
      while (i<15) {
        Thread.sleep(1000);
        println(s"${i}-" + Thread.currentThread())
        i = i + 1
      }
      println("+++callable about to finish+++")
      "333"
    }
  }

  val ft: MyFutureTask[String] = MyExecutor.submit(callable)


  var j = 0
  while (j<5) {
    Thread.sleep(1000);
    println(s"{j}doing stuff in-" + Thread.currentThread())
    j = j + 1
  }

  println(ft.outcome)

  val s = System.currentTimeMillis
  while (ft.outcome==null && System.currentTimeMillis-s < 4000) {
    Thread.sleep(1000); println(s"===>busy-waiting in ${Thread.currentThread}. Still consuming CPU by periodically checking and sleeping if outcome not ready"); println(ft.outcome)
  }
  println(ft.get())



}
