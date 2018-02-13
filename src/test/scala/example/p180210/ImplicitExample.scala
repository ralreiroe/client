package example.p180210

import scala.collection.immutable.Stream
import scala.concurrent.Future
import scala.util.{Failure, Try}

class Wrap[T](f: => T) {
  def withProgress = RepeatedAndWithProgress.withProgress(f)

  def runRepeatedly = RepeatedAndWithProgress.runRepeatedly(f)
}

object ImplicitExample extends App {

  implicit def conv[T](f: => T) = new Wrap[T](f)      //or simply say 'implicit class Wrap'


  def computation = {
    println(s"sleeping ${cnt}")
    Thread.sleep(2000)
    cnt = cnt + 1
    if (cnt < 3) throw new Exception
    555
  }

  println("++++")
  var cnt = 0
  val y = computation.runRepeatedly.withProgress    //compiles to new Wrap(new Wrap(computation).runRepeatedly).withProgress
  println(y)

  println("++++")

}

object RepeatedAndWithProgress {

  def rep(i: Int): Stream[String] = s"${i} times" #:: rep(i + 1)

  val s = rep(1)

  def runRepeatedly[T](f: => T, i: Int = 0): Try[T] = {
    Try(f) match {
      case Failure(e) => {
        println(s"Failed ${s(i)}")
        runRepeatedly(f, i + 1)
      }
      case succ => succ
    }
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  def withProgress[T](f: => T): T = {
    var i = 0
    var ready = false
    Future {
      while (!ready) {
        i = i + 1
        Thread.sleep(500)
        print(s"\rElapsed: ${i * 500} ms")
      }
    }

    val r = Try.apply(f)
    ready = true
    r.get
  }

}

