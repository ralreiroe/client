package httpclient

import java.io._
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import scala.collection.mutable.StringBuilder

object ScalaApacheHttpRestClient {

  def main(args: Array[String]) {

    // (1) get the content from the yahoo weather api url
    val content = getRestContent("http://weather.yahooapis.com/forecastrss?p=80020&u=f")

    // (2) convert it to xml
    val xml = scala.xml.XML.loadString(content)
    assert(xml.isInstanceOf[scala.xml.Elem])  // needed?

    // (3) search the xml for the nodes i want
    val temp = (xml \\ "channel" \\ "item" \ "condition" \ "@temp") text
    val text = (xml \\ "channel" \\ "item" \ "condition" \ "@text") text

    // (4) print the results
    printf("The current temperature is %s degrees, and the sky is %s.", temp, text.toLowerCase())
  }

  /**
    * Returns the text content from a REST URL. Returns a blank String if there
    * is a problem.
    */
  def getRestContent(url:String): String = {
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
