package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Utils2 {

  var stopShowing: Boolean = false

  private def keepShowing = () => !stopShowing


  def showProgress(gran: Int) = _showProgress(1, gran)

  def _showProgress(x: Int, gran: Int): Unit = {
    if (keepShowing()) {
      System.out.print(s"\r Elapsed time: ${x*gran}ms")
      Thread.sleep(gran)
      _showProgress(x+1, gran)
    }
  }
}

object ProgressBarTest92 extends App {

  Future {
      Utils2.showProgress(500)
  }

  var x = 0
  while (x < 2000) {
    Thread.sleep(2)
    x = x + 1
  }

  Utils2.stopShowing = true

  Thread.sleep(10000)




}
