package shortcodes1

import java.io.FileWriter

/**
  * Can produce a query result for some query (just in int in this mock case)
  */
trait QueryResultProducingCapability {
  def runQuery(queryType: Int = 1): Iterator[String] = {
    val toAdd = queryType * 1000 //just to produce something queryType-dependent
    (101 to 200).zip(201 to 300).map {
      case (i, j) => {
        println("producing query result line")
        s"${toAdd + i},${toAdd + j}"
      }
    }
  }.toIterator
}

/**
  * Runs a number of configured queries against Cesium - just a mock in this case. A real one would read queries from a config file
  */
object ProducerForAllConfigured {
  def cesiumOutput(cs: QueryResultProducingCapability): Map[Int, Iterator[String]] = (for (qt <- Set(1,2,3)) yield (qt, cs.runQuery(qt))).toMap
}

/**
  * Injects values from a query result into a string by replacing certain labels in the string
  */
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

object Publisher {
  def write(name: Int, values: Iterator[String]) = {
    val fw = new FileWriter(s"${name}.txt", true) //append=true
    while (values.hasNext) {
      println("writing line")
      fw.write(values.next)
    }
    fw.close
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
  * Ie. all query result lines are produced upfront and into memory. but
  * injecting only happens if the final line is to be written to the file.
  *
  * So while the queryResult is fully loaded into memory, the injected lines
  * are produced and gc'able as soon as they are written out.
  */
object MainOnFullQueryResultIteratorRest extends App {

//  val fullPipeline = toCesiumOutputs.andThen(toExchangeOutputs).andThen(toFiles)
//  fullPipeline(new QueryResultProducingCapability {})

  //====Calling Functions====

  toFiles.compose(toExchangeOutputs).compose(toCesiumOutputs).apply(new QueryResultProducingCapability {})


  //====Declaring Functions===

  lazy val toCesiumOutputs: QueryResultProducingCapability => Map[Int, Iterator[String]] = qr => ProducerForAllConfigured.cesiumOutput(new QueryResultProducingCapability {})

  lazy val toExchangeOutputs: Map[Int, Iterator[String]] => Map[Int, Iterator[String]] = cesiumOutput => cesiumOutput.map {
    case (i, it) => (i, Injector.inject(it, "xetra,lc,sc\n"))
  }

  lazy val toFiles: Map[Int, Iterator[String]] => Unit = exchangeOutput => exchangeOutput.foreach {
    case (i,it) => Publisher.write(i, it)
  }
}
