package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ProgressBarTest3 extends App {

  var keepShowing = true

//  Future {
//    var x = 1
//    while (showProgress) {
//      x = x+1
//      System.out.print(s"\r Elapsed time: ${x*100}ms")
//      Thread.sleep(100)
//    }
//  }

  def showProgress(gran: Int) = _showProgress(keepShowing, 1, gran)

  def _showProgress(show: Boolean, x: Int, gran: Int): Unit = {
    if (show) {
      System.out.print(s"\r Elapsed time: ${x*gran}ms")
      Thread.sleep(gran)
      _showProgress(show, x+1, gran)
    }
  }

  Future {
    showProgress(500)
  }

  var x = 0
  while (x < 2000) {
    Thread.sleep(2)
    x = x + 1
  }

  keepShowing = false

  Thread.sleep(10000)





}
