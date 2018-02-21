import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, ExecutionException, OnCompleteRunnable, Promise}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}


class CallbackRunnable[T](val executor: ExecutionContext, val func: Try[T] => Any) extends Runnable with OnCompleteRunnable {

  var value: Try[T] = null

  override def run() = {
    require(value ne null)
    try func(value) catch { case NonFatal(e) => executor reportFailure e }
  }

  def executeWithValue(v: Try[T]): Unit = {
    require(value eq null) // can't complete it twice
    value = v
    try executor.execute(this) catch { case NonFatal(t) => executor reportFailure t }
  }
}


class MyPromise[T] extends AtomicReference[AnyRef](Nil) {
  def future: this.type = this

  final def isCompleted: Boolean = get() match {
    case _: Try[_] => true
    case _ => false
  }

  final def tryComplete(value: Try[T]): Boolean = {
    val resolved: Try[T] = resolveTry(value)
    tryCompleteAndGetListeners(resolved) match {
      case null             => false
      case rs if rs.isEmpty => true
      case rs               => rs.foreach(r => r.executeWithValue(resolved)); true
    }
  }


  @tailrec
  private def tryCompleteAndGetListeners(v: Try[T]): List[CallbackRunnable[T]] = {
    get() match {
      case raw: List[_] =>
        val cur = raw.asInstanceOf[List[CallbackRunnable[T]]]
        if (compareAndSet(cur, v)) cur else tryCompleteAndGetListeners(v)
      case _ => null
    }
  }

  final def onComplete[U](func: Try[T] => U)(implicit executor: ExecutionContext): Unit =
    dispatchOrAddCallback(new CallbackRunnable[T](executor.prepare(), func))

  @tailrec
  private def dispatchOrAddCallback(runnable: CallbackRunnable[T]): Unit = {
    get() match {
      case r: Try[_]          => runnable.executeWithValue(r.asInstanceOf[Try[T]])
      case listeners: List[_] => if (compareAndSet(listeners, runnable :: listeners)) ()
      else dispatchOrAddCallback(runnable)
    }
  }

  def complete(result: Try[T]): this.type =
    if (tryComplete(result)) this else throw new IllegalStateException("Promise already completed.")


  private def resolveTry[T](source: Try[T]): Try[T] = source match {
    case Failure(t) => resolver(t)
    case _          => source
  }

  private def resolver[T](throwable: Throwable): Try[T] = throwable match {
    case t: scala.runtime.NonLocalReturnControl[_] => Success(t.value.asInstanceOf[T])
    case t: scala.util.control.ControlThrowable    => Failure(new ExecutionException("Boxed ControlThrowable", t))
    case t: InterruptedException                   => Failure(new ExecutionException("Boxed InterruptedException", t))
    case e: Error                                  => Failure(new ExecutionException("Boxed Error", e))
    case t                                         => Failure(t)
  }
}


object ImplementFutureMap extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  var i = 0
  val f: Unit => Int = (x: Unit) => {
    println(Thread.currentThread())
    while (i < 4) {
      Thread.sleep(1000);
      println(s"${i}-" + Thread.currentThread())
      i = i + 1
    }
    println(";;;")
    333
  }

  val f2: Try[Unit] => Try[Int] = (x: Try[Unit]) => x map f

  val secondPromise: MyPromise[Int] = new MyPromise[Int]()

  val startPromise = new MyPromise[Unit]()
  startPromise.set(Success())
  startPromise.onComplete((result: Try[Unit]) => {  //runs f2 on value in startPromise
    val triedInt: Try[Int] = try {
      f2(result)
    } catch {
      case NonFatal(t) => Failure(t)
    }
    secondPromise.complete(triedInt)    //complete and trigger secondPromise's callbacks
  } )





//  (new CallbackRunnable(global.prepare(), f2)).executeWithValue(Success())

  Thread.sleep(50000)

}