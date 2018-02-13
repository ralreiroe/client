package example.p180210

import scala.collection.immutable.Stream
import scala.util.{Failure, Try}

object StreamExample extends App {

  def computation = {
    println(s"sleeping ${cnt}")
    Thread.sleep(200)
    cnt = cnt + 1
    if (cnt < 3) throw new Exception
    555
  }

  println("++++")
  var cnt = 0
  val y = UntilSuccessfulWithStream(computation)
  println(y)

  println("++++")

}

object UntilSuccessfulWithStream {

  def rep(i: Int): Stream[String] = s"${i} times" #:: rep(i + 1)    //(part 1) the definition of a stream as recursive method

  val s = rep(1)      //(part 2) the stream instance

  def apply[T](f: => T, i: Int = 0): Try[T] = {
    Try(f) match {
      case Failure(e) => {
        println(s"Failed ${s(i)}")
        apply(f, i + 1)
      }
      case succ => succ
    }
  }

}

