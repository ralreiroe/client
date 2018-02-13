import java.util

object MemoryTestS extends App {

  val map = new util.HashMap[Integer, NewObject]

    val list = scala.collection.mutable.ListBuffer.empty[NewObject]
//  var list = List.empty[NewObject]


  //  https://stackoverflow.com/questions/17374743/how-can-i-get-the-memory-that-my-java-program-uses-via-javas-runtime-api


  def elapsed[T](f: => T): T = {
    val s = System.currentTimeMillis
    val res = f
    println(s"${(System.currentTimeMillis - s) / 1000} seconds")
    res
  }


  val rt = Runtime.getRuntime
  var prevTotal = 0L
  var prevFree = rt.freeMemory

  def block =

    for (i <- 0 until 33000000) {
      val total = rt.totalMemory / 1000000
      val free = rt.freeMemory / 1000000
      if (total != prevTotal || free != prevFree) {
        val used = total - free
        val prevUsed = prevTotal - prevFree

        println(f"# $i%8d, Total: $total%4d, Used: $used%4d, Free: $free%4d, ∆Used: ${(used - prevUsed)}%4d")
        if (used < prevUsed) println("GC'd!")
        prevTotal = total
        prevFree = free

      }
      //    list = new NewObject :: list
      list :+ new NewObject
      //    map.put(i, new NewObject)

    }

  elapsed(block)

  println("finished")

}

class NewObject {
  val i = 0L
  val j = 0L
  val k = 0L
}

