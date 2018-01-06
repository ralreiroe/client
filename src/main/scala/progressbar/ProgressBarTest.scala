package progressbar

class ProgressBarTraditional extends Thread {
  var showProgress = true

  override def run(): Unit = {
    val anim = "|/-\\"
    var x = 0
    while (showProgress) {
      x = x+1
      System.out.print("\r Processing " + anim.charAt(x % anim.length))
      Thread.sleep(500)
    }
  }
}


object ProgressBarTest extends App {

  val pb = new ProgressBarTraditional()
  pb.start()

  var x = 0
  while (x < 2000) {
    Thread.sleep(2)
    x = x + 1
  }

  pb.showProgress = false

  Thread.sleep(10000)
}
