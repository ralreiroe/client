import java.io.File

import scala.io.Source
import scala.util.matching.Regex

/**
  * extract from log file to big to fit in memory
  *
  * https://stackoverflow.com/questions/14700574/scala-how-to-traverse-stream-iterator-collecting-results-into-several-different
  */
class HugeLogExtractor extends Spec {

  val errorPat = """(\w+),([0-9]+),.*15.*"""r
  val loginPat = """(\w+),([0-9]+),.*25.*"""r

  def streamData(file: File, errorPat: Regex, loginPat: Regex): List[(String,String)] = {
    val lines: Iterator[String] = Source.fromFile(file).getLines  //<====iterator
    val (err, log) = lines.collect {
      case errorPat(inf, ip) => (Some((ip, inf)), None)
      case loginPat(_, _, ip, id) => (None, Some((ip, id)))
    }.toList.unzip
    val ip2id: Map[String, String] = log.flatten.toMap
    err.collect{ case Some((ip,inf)) => (ip2id.getOrElse(ip,"none") + "" + ip, inf) }
  }

  "extract matches from huge log file w/o reading file into mem" in {

    val r = streamData(new File("1.txt"), errorPat, loginPat)
    println(r)



  }
}
