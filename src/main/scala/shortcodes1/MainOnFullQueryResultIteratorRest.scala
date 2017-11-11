package shortcodes1

import java.io.FileWriter

trait QueryRunner {
  def runQuery(queryType: Int = 1): Iterator[String]
}

class Producer {
  //run query for querytypes 1,2,3
  def cesiumOutput(cs: QueryRunner): Map[Int, Iterator[String]] = (for (qt <- Set(1,2,3)) yield (qt, cs.runQuery(qt))).toMap
}

object Injector {

  //1101,1201 and xetra,lc,sc makes xetra,1201,1101
  def inject(co: Iterator[String], ff: String): Iterator[String] = co.map {
    case linestr => {
      val linevals = linestr.split(",").toList
      println("injecting")
      ff.replace("sc", linevals(0)).replace("lc", linevals(1))
    }
  }
}

/**
  * output:
  *
...
producing query result line
producing query result line
producing query result line
producing query result line
producing query result line
producing query result line
writing line
injecting
writing line
injecting
writing line
injecting
...

  *
  * Ie. all query result lines are produced upfront and into memeory. but
  * injecting only happens if the final line is to be written to the file.
  *
  * So while the queryResult is fully loaded into memory, the injected lines
  * are produced and gc'able as soon as they are written out.
  */
object MainOnFullQueryResultIteratorRest extends App {

  val cesiumOutput: Map[Int, Iterator[String]] = (new Producer).cesiumOutput(new QueryRunner {
    override def runQuery(queryType: Int) = {
      val toAdd = queryType*1000  //just to produce something queryType-dependent
      (101 to 200).zip(201 to 300).map{
        case (i,j) => {
          println("producing query result line")
          s"${toAdd+i},${toAdd+j}"
        }
      }
    }.toIterator
  })

  val exchangeOutput: Map[Int, Iterator[String]] = cesiumOutput.map {
    case (i, it) => (i, Injector.inject(it, "xetra,lc,sc\n"))
  }

  exchangeOutput.foreach {
    case (i,it) => {

      val fw = new FileWriter(s"${i}.txt", true)    //append=true
      while (it.hasNext) {
        println("writing line")
        fw.write(it.next)
      }
      fw.close
    }
  }

}
