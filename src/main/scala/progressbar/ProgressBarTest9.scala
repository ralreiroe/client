package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object ProgressBarTest9 extends App {

  object Utils {

    def showProgress(gran: Int)(implicit x: Function0[Boolean]) = _showProgress(x, 1, gran)

    def _showProgress(f: () => Boolean, x: Int, gran: Int): Unit = {
      if (f()) {
        System.out.print(s"\r Elapsed time: ${x*gran}ms")
        Thread.sleep(gran)
        _showProgress(f, x+1, gran)
      }
    }
  }

  var bool = true
  implicit def keepShowing = () => bool

  Future {
    Utils.showProgress(500)
  }

  var x = 0
  while (x < 2000) {
    Thread.sleep(2)
    x = x + 1
  }

  bool = false

  Thread.sleep(10000)




}
