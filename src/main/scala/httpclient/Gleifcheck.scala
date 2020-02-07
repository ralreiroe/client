package httpclient

import java.io.File

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import scala.io.Source

object Gleifcheck {

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

    val resultFile = new java.io.PrintWriter(new File("lmeinvalidleis.result.txt"))     //a printwriter without auto-flush


    val source = Source.fromFile(new File("lmeinvalidleis.txt")).getLines filter {
      case leistr =>
       Thread.sleep(5)
        val str = getRestContent("https://leilookup.gleif.org/api/v1/leirecords?lei=" + leistr.trim)
        val b = str.contains("ACTIVE") && str.contains("ISSUED")
        b
    }


    try {
      source foreach resultFile.println
    } finally {
      println("closing file")
      resultFile.close
    }



    println("Closing")
    httpClient.getConnectionManager().shutdown()


  }
}
