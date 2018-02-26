package httpclient

import java.io.File

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import scala.collection.immutable
import scala.io.Source

object ScalaApacheHttpRestClient2 {

  /**
    * Returns the text content from a REST URL. Returns a blank String if there
    * is a problem.
    */


  def main(args: Array[String]) {
    val httpClient = new DefaultHttpClient()

    def getRestContent(url: String): String = {
      val httpResponse = httpClient.execute(new HttpGet(url))
      val entity = httpResponse.getEntity()
      var content = ""
      if (entity != null) {
        val inputStream = entity.getContent()
        content = scala.io.Source.fromInputStream(inputStream).getLines.mkString
        inputStream.close
      }
      return content
    }

    val ints = Stream.from(1).iterator

//    val r: immutable.Seq[Int] = 1 to 7; r.iterator.zip(ints).foreach { case (l,r) => println(l+" "+r)}

    val p = new java.io.PrintWriter(new File("gtt.result.txt"))     //a printwriter without auto-flush
    val notActiveOrIssued = Source.fromFile(new File("queryResult.51.gttleis.csv")).getLines.zip(ints).filterNot {
      case (leistr, idx) =>
        if (idx % 100 ==0) {
          println(idx)
          p.flush
        }
        Thread.sleep(5)
        val str = getRestContent("https://leilookup.gleif.org/api/v1/leirecords?lei=" + leistr.trim)
        val b = str.contains("ACTIVE") && str.contains("ISSUED")
        b
    }


    try {
      notActiveOrIssued foreach p.println
    } finally {
      println("closing file")
      p.close
    }



    println("Closing")
    httpClient.getConnectionManager().shutdown()


  }
}
