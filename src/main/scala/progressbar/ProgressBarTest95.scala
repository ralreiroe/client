package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Utils5 {

  private var inUse = false

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
    if (inUse) throw new IllegalStateException("progress bar can only be used one at a time")
    inUse = true
    Utils5.stopShowing = false
    Future {
      Utils5.showProgress(gran: Int, msg)
    }
    f()
    Utils5.stopShowing = true
    inUse = false

  }
}

object ProgressBarTest95 extends App {


  private def ttt: () => Unit = () => {
    var x = 0
    while (x < 2000) {
      Thread.sleep(2)
      x = x + 1
    }
    x
  }


  Future { Utils5.showProgressWhile(ttt, 500, "abc====================================================") }
  Thread.sleep(20)
  Future { Utils5.showProgressWhile(ttt, 500, "xyz++++++++++++++++++++++++++++++++++++++++++++++++++++") } recover {
    case e: IllegalStateException => e.printStackTrace()
  }

  Thread.sleep(10000)




}
