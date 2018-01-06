package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object ProgressBarTest8 extends App {

  object Utils {

    def p(implicit i:Int) = print(i)

    def multiply(implicit by: Int) = 5 * by


    class A {
      def f(a: String)(implicit b: String): String = a + b
    }
    val a = new A

    implicit val string = "349"
    val m = a.f(_) // takes the implicit in this scope


    def showProgress(gran: Int, f: => Boolean) = _showProgress(f, 1, gran)

    def _showProgress(f: => Boolean, x: Int, gran: Int): Unit = {
      if (f) {
        System.out.print(s"\r Elapsed time: ${x*gran}ms")
        Thread.sleep(gran)
        _showProgress(f, x+1, gran)
      }
    }
  }

  val monday = true
  implicit def f: Int = if (monday) 4 else 2

  println(Utils.multiply)

  println(Utils.p)



  var bool = true
  implicit def keepShowing = bool

  Future {
    Utils.showProgress(500, keepShowing)
  }

  var x = 0
  while (x < 2000) {
    Thread.sleep(2)
    x = x + 1
  }

  bool = false

  Thread.sleep(10000)




}
