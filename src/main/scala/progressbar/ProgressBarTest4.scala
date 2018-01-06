package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ProgressBarTest4 extends App {

  var keepShowing = true

//  Future {
//    var x = 1
//    while (showProgress) {
//      x = x+1
//      System.out.print(s"\r Elapsed time: ${x*100}ms")
//      Thread.sleep(100)
//    }
//  }

  def showProgress(gran: Int) = _showProgress(1, gran)

  def _showProgress(x: Int, gran: Int): Unit = {
    if (keepShowing) {
      System.out.print(s"\r Elapsed time: ${x*gran}ms")
      Thread.sleep(gran)
      _showProgress(x+1, gran)
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
