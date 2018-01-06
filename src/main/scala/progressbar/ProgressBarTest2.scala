package progressbar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ProgressBarTest2 extends App {

  var showProgress = true

  def run() = {
    val anim = "|/-\\"
    var x = 0
    while (showProgress) {
      x = x+1
      System.out.print("\r Processing " + anim.charAt(x % anim.length))
      Thread.sleep(100)
    }
  }

  Future { run() }

  var x = 0
  while (x < 2000) {
    Thread.sleep(2)
    x = x + 1
  }


  showProgress = false

  Thread.sleep(10000)





}
