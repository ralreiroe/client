package shortcodes1

import java.io.FileWriter

trait QueryRunner {
  def runQuery(): Iterator[String]
}

class Producer {
  def cesiumOutput(cs: QueryRunner): Map[Int, Iterator[String]] = (for (qt <- Set(1,2,3)) yield (qt, cs.runQuery())).toMap
}

object Injector {
  def inject(co: Iterator[String], ff: String): Iterator[String] = co.map {
    case linestr => {
      val linevals = linestr.split(",").toList
      println("injecting")
      ff.replace("sc", linevals(0)).replace("lc", linevals(1))
    }
  }
}


object MainOnFullQueryResultIteratorRest extends App {

  val cesiumOutput: Map[Int, Iterator[String]] = (new Producer).cesiumOutput(new QueryRunner {
    override def runQuery() = {
      (101 to 200).zip(201 to 300).map{
        case (i,j) => {
          println("producing query result line")
          s"${i},${j}"
        }
      }
    }.toIterator
  })

  val exchangeOutput: Map[Int, Iterator[String]] = cesiumOutput.map {
    case (i, it) => (i, Injector.inject(it, "xetra,lc,sc\n"))
  }

  exchangeOutput.foreach {
    case (i,it) => {

      val fw = new FileWriter(s"${i}.txt", true)
      while (it.hasNext) {
        println("writing line")
        fw.write(it.next)
      }
      fw.close
    }
  }

}
