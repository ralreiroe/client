package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Utils3 {

  private var stopShowing: Boolean = false

  private def keepShowing = () => !stopShowing

  def showProgress(gran: Int) = _showProgress(1, gran)

  def _showProgress(x: Int, gran: Int): Unit = {
    if (keepShowing()) {
      System.out.print(s"\r Elapsed time: ${x*gran}ms")
      Thread.sleep(gran)
      _showProgress(x+1, gran)
    }
  }


  def showProgressWhile(f: () => Unit, gran: Int = 500) = {
    Future {
      Utils3.showProgress(gran: Int)
    }
    f()
    Utils3.stopShowing = true

  }
}

object ProgressBarTest93 extends App {


  private def ttt: () => Unit = () => {
    var x = 0
    while (x < 2000) {
      Thread.sleep(2)
      x = x + 1
    }
  }


  Utils3.showProgressWhile(ttt)

  Thread.sleep(10000)




}
