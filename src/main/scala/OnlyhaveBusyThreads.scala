import java.util.concurrent.Callable

/**
  * In Java, a FutureTask is a Runnable that is created by an Executor with a function (a Callable) and a null result initially.
  * Upon Executor.submit you get the future back immediately and can then either poll it for the result or wait for it using a
  * self-thread-blocking call to get().
  * The FutureTask also has a get() function that will block the sending thread.
  *
  * Once the Executor runs the FutureTask, all waiting threads are awoken.
  *
  *
  * https://www.artima.com/insidejvm/ed2/threadsynch.html
  */
case class SyncObject() {
  def addThreadToWaitQueue = wait()
  def awakeAndRemoveThreadsFromWaitQueue = notifyAll()
}

class MyFutureTask[T](c: => T) extends Runnable {

  var outcome: T = null.asInstanceOf[T]
  var syncObj = SyncObject()


  override def run(): Unit = {

    val result = c
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
  def submit[T](c: => T) = {

    val ft = new MyFutureTask(c)
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
  def call(): String = {
      println(Thread.currentThread())
      while (i<15) {
        Thread.sleep(1000);
        println(s"${i}-" + Thread.currentThread())
        i = i + 1
      }
      println("+++callable about to finish+++")
      "333"
    }

  val ft: MyFutureTask[String] = MyExecutor.submit(call)


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
