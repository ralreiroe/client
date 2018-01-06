package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Utils4 {

  private var stopShowing: Boolean = false

  private def keepShowing = () => !stopShowing

  def showProgress(gran: Int, msg: String) = _showProgress(1, gran, msg)

  def _showProgress(x: Int, gran: Int, msg: String): Unit = {
    if (keepShowing()) {
      System.out.print(s"\r ${msg}: ${x*gran}ms")
      Thread.sleep(gran)
      _showProgress(x+1, gran, msg)
    }
  }


  def showProgressWhile(f: () => Unit, gran: Int = 500, msg: String) = {
    Utils4.stopShowing = false
    Future {
      Utils4.showProgress(gran: Int, msg)
    }
    f()
    Utils4.stopShowing = true

  }
}

object ProgressBarTest94 extends App {


  private def ttt: () => Unit = () => {
    var x = 0
    while (x < 2000) {
      Thread.sleep(2)
      x = x + 1
    }
  }


  Utils4.showProgressWhile(ttt, 500, "abc")
  Thread.sleep(2000)
  Utils4.showProgressWhile(ttt, 500, "xyz")

  Thread.sleep(10000)




}
