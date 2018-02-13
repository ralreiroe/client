import java.util

/**
  * mutable immutable prepend append
  */
object MemoryTestS extends App {

  val mutableMap = new util.HashMap[Integer, NewObject]

    val list = scala.collection.mutable.ListBuffer.empty[NewObject]
  var immutableList = List.empty[NewObject]


  //  https://stackoverflow.com/questions/17374743/how-can-i-get-the-memory-that-my-java-program-uses-via-javas-runtime-api


  def createAndAddToMutableBuffer(i: Int): Unit = {
    mutableMap.put(i, new NewObject)
    ()
  }
  def prependToImmutableList(i: Int): Unit = {
    list :+ new NewObject
    ()
  }
  def appendToImmutableList(i: Int): Unit = {
    immutableList = new NewObject :: immutableList
  }

  @volatile var stop = false


  def elapsed[T](f: => T): T = {
    val s = System.currentTimeMillis
    val res = f
    println(s"${(System.currentTimeMillis - s) / 1000} seconds")
    res
  }


  val rt = Runtime.getRuntime
  var prevTotal = 0L
  var prevFree = rt.freeMemory

  def block(testCode: Int => Unit) =

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

      testCode(i)

    }

//  elapsed(block(createAndAddToMutableBuffer))
//  elapsed(block(appendToImmutableList))
  elapsed(block(prependToImmutableList))

  println("finished")

}

class NewObject {
  val i = 0L
  val j = 0L
  val k = 0L
}

