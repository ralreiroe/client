package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Utils1 {

  implicit var bool: Boolean

  private def keepShowing = () => bool


  def showProgress(gran: Int) = _showProgress(1, gran)

  def _showProgress(x: Int, gran: Int): Unit = {
    if (keepShowing()) {
      System.out.print(s"\r Elapsed time: ${x*gran}ms")
      Thread.sleep(gran)
      _showProgress(x+1, gran)
    }
  }
}

object ProgressBarTest91 extends App {



  var bool1 = true

    val ut = new Utils1 {
      override implicit var bool: Boolean = bool1
    }
  Future {
      ut.showProgress(500)
  }

  var x = 0
  while (x < 2000) {
    Thread.sleep(2)
    x = x + 1
  }

  bool1 = false

  Thread.sleep(10000)




}
