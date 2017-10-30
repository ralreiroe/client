package example

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.scalatest._

class HttpClientSpec extends FlatSpec with MustMatchers {
  """getRestContent("http://localhost:9000")""" must """include ("Welcome to Play")""" in {

    getRestContent("http://localhost:9000") must include ("Welcome to Play")

    /**
      * Returns the text content from a REST URL. Returns a blank String if there
      * is a problem.
      */
    def getRestContent(url: String): String = {
      val httpClient = new DefaultHttpClient()
      val httpResponse = httpClient.execute(new HttpGet(url))
      val entity = httpResponse.getEntity()
      var content = ""
      if (entity != null) {
        val inputStream = entity.getContent()
        content = io.Source.fromInputStream(inputStream).getLines.mkString
        inputStream.close
      }
      httpClient.getConnectionManager().shutdown()
      return content
    }
  }
}
