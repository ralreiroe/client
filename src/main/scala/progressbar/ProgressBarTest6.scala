package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object ProgressBarTest6 extends App {

  object Utils {

    var keepShowing = true

    def showProgress(b: Boolean = true)(gran: Int = 500) = if (b==false) keepShowing = false else _showProgress(1, gran)

    def _showProgress(x: Int, gran: Int): Unit = {
      if (keepShowing) {
        System.out.print(s"\r Elapsed time: ${x*gran}ms")
        Thread.sleep(gran)
        _showProgress(x+1, gran)
      }
    }
  }


  Future {
    Utils.showProgress()(400)
  }

  var x = 0
  while (x < 2000) {
    Thread.sleep(2)
    x = x + 1
  }

  Utils.showProgress(false)()

  Thread.sleep(10000)





}
