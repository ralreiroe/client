import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.concurrent._
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}


/**
  * A runnable that executes the given func on a value
  */
class CallbackRunnable[T](val executor: ExecutionContext, val func: Try[T] => Any) extends Runnable with OnCompleteRunnable {

  var value: Try[T] = null

  override def run() = {
    require(value ne null)
    try func(value) catch {
      case NonFatal(e) => executor reportFailure e
    }
  }

  def executeWithValue(v: Try[T]): Unit = {
    require(value eq null) // can't complete it twice
    value = v
    try executor.execute(this) catch {
      case NonFatal(t) => executor reportFailure t
    }
  }
}

/**
  * A promise is either completed or a list of runnables
  *
  * @tparam T
  */
class MyPromise[T] extends AtomicReference[AnyRef](Nil) {
  def future: this.type = this

  final def isCompleted: Boolean = get() match {
    case _: Try[_] => true
    case _ => false
  }

  private def registerCallback[U](func: Try[T] => U)(implicit executor: ExecutionContext) = runAsyncOnceComplete(func)
  private def complete[U](value: Try[T])(implicit executor: ExecutionContext) = runAsync(value)

  @tailrec
  final def runAsyncOnceComplete[U](func: Try[T] => U)(implicit executor: ExecutionContext): Unit = {

    val runnable = new CallbackRunnable[T](executor.prepare(), func)

    get() match {
      case r: Try[_] => runnable.executeWithValue(r.asInstanceOf[Try[T]])
      case listeners: List[_] => if (compareAndSet(listeners, runnable :: listeners)) ()
      else runAsyncOnceComplete(func)
    }

  }

  /**
    * the secret to combining futures.
    * the "parent" promise swaps list of deferred functions for value and triggers list execution
    * @param v
    */
  @tailrec
  final def runAsync(v: Try[T]): Unit =

    get() match {

      case raw: List[_] =>
        val runnables = raw.asInstanceOf[List[CallbackRunnable[T]]]

        if (compareAndSet(runnables, v))
          runnables.foreach(r => r.executeWithValue(v)) else runAsync(v)

      case _ => throw new IllegalStateException("Promise already completed.")
    }


}


trait Future[T] {

  def map[S](f: T => S)(implicit executor: ExecutionContext): Future[S] = {



    null
  }


}

object MyFuture {

  import scala.concurrent.ExecutionContext.Implicits.global

  def apply[T](f: Unit => T) = {
    val tPromise: MyPromise[T] = new MyPromise[T]()

    val startPromise = new MyPromise[Unit]()

    val triedUnitToTriedT = (triedUnit: Try[Unit]) => try { triedUnit map f } catch { case NonFatal(t) => Failure(t) }

    startPromise.runAsyncOnceComplete(triedUnitToTriedT.andThen(tPromise.runAsync))

    startPromise.runAsync(Success()) //complete startPromise and trigger its callbacks

    tPromise
  }
}

object ImplementFutureMap extends App {

  var i = 0
  val f1: Unit => Int = (x: Unit) => {
    println(Thread.currentThread())
    while (i < 4) {
      Thread.sleep(1000);
      println(s"${i}-" + Thread.currentThread())
      i = i + 1
    }
    println(";;;")
    333
  }

  val intPromise = MyFuture{ f1 }



  while (!intPromise.isCompleted) {
    println(s"sleeping in ${Thread.currentThread()}")
    Thread.sleep(1000)
  }

  println(intPromise.get())

  //  (new CallbackRunnable(global.prepare(), f2)).executeWithValue(Success())


}