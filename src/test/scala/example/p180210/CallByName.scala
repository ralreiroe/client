package example.p180210

import scala.collection.immutable.Stream
import scala.concurrent.Future
import scala.util.{Failure, Try}

object CallByName extends App {

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

  var cnt = 0

  def computation = {
    println(s"sleeping ${cnt}")
    Thread.sleep(2000)
    cnt = cnt + 1
    if (cnt < 3) throw new Exception
    555
  }

  var x: Try[Int] = null

  x = runRepeatedly(computation) //computation is not called before being passed
  println(x)

cnt = 0
  x = withProgress(runRepeatedly(computation)) //computation is not called before being passed
  println(x)

cnt = 0
  x = runRepeatedly(withProgress(computation)) //computation is not called before being passed
  println(x)

cnt = 0
  x = Try.apply(withProgress(computation))//computation is not called before being passed
  println(x)


  println("++++")


  def computationWithInput(input: String) = {
    println(s"sleeping ${input}")
    Thread.sleep(2000)
    cnt+=1
    if (cnt < 3) throw new Exception
    555
  }

  cnt = 0
  x = runRepeatedly(computationWithInput("value")) //shouldBe Success(555)
  println(x)

  cnt = 0
  x = withProgress(runRepeatedly(computationWithInput("value"))) //shouldBe Success(555)
  println(x)

  cnt = 0
  x = runRepeatedly(withProgress(computationWithInput("value"))) //shouldBe Success(555)
  println(x)

  cnt = 0
  x = Try.apply(withProgress(computationWithInput("value")))//shouldBe Failure(java.lang.Exception)
  println(x)

}
