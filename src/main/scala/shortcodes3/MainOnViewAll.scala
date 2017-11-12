package shortcodes3

import java.io.FileWriter

/**
  * Using View rather than Iterator. No difference in this case but in general:
  * Iterators are expected to release what has been seen. Once next has been called, the previous is, hopefully, let go.
  * Views are the converse. They promise to not acquire what has not been requested.
  * https://stackoverflow.com/questions/4798043/what-is-the-difference-between-the-methods-iterator-and-view
  * ...See Iterator#map, it creates a new iterator that lazily evaluates the function passed to map for each 'next'. So it seems to act exactly like a view ... True, but that's not in the contract.
  *
  */

/**
  * Can produce a query result for some query (just in int in this mock case)
  */
trait QueryResultProducingCapability {
  def runQuery(queryType: Int = 1): ViewSeq[String] = {
    val toAdd = queryType * 1000 //just to produce something queryType-dependent
    (101 to 200).zip(201 to 300).view.map {
      case (i, j) => {
        println("producing query result line")
        s"${toAdd + i},${toAdd + j}"
      }
    }
  }
}

/**
  * Runs a number of configured queries against Cesium - just a mock in this case. A real one would read queries from a config file
  */
object ProducerForAllConfigured {
  def cesiumOutput(cs: QueryResultProducingCapability): Map[Int, ViewSeq[String]] = (for (qt <- Set(1,2,3)) yield (qt, cs.runQuery(qt))).toMap
}

/**
  * Injects values from a query result into a string by replacing certain labels in the string
  */
object Injector {

  //1101,1201 and xetra,lc,sc makes xetra,1201,1101
  def inject(co: ViewSeq[String], ff: String): ViewSeq[String] = co.map {
    case linestr => {
      val linevals = linestr.split(",").toList
      println("injecting")
      ff.replace("sc", linevals(0)).replace("lc", linevals(1))
    }
  }
}

object Publisher {
  def write(name: Int, values: ViewSeq[String]) = {
    val fw = new FileWriter(s"${name}.txt", true) //append=true
    values.foreach { v =>
      println("writing line")
      fw.write(v)
    }
    fw.close
  }
}

/**
  * output:
  *
...
writing line
producing query result line
injecting
writing line
producing query result line
injecting
writing line
producing query result line
injecting
writing line
producing query result line
injecting
writing line
producing query result line
...

  *
  * Line to be written to file triggers line injection which triggers producing a query result line
  *
  * So every line is produced and gc'able as soon as they are written out.
  */
object MainOnViewAll extends App {

//  val fullPipeline = toCesiumOutputs.andThen(toExchangeOutputs).andThen(toFiles)
//  fullPipeline(new QueryResultProducingCapability {})

  //====Calling Functions====

  toFiles.compose(toExchangeOutputs).compose(toCesiumOutputs).apply(new QueryResultProducingCapability {})


  //====Declaring Functions===

  lazy val toCesiumOutputs: QueryResultProducingCapability => Map[Int, ViewSeq[String]] = qr => ProducerForAllConfigured.cesiumOutput(new QueryResultProducingCapability {})

  lazy val toExchangeOutputs: Map[Int, ViewSeq[String]] => Map[Int, ViewSeq[String]] = cesiumOutput => cesiumOutput.map {
    case (i, it) => (i, Injector.inject(it, "xetra,lc,sc\n"))
  }

  lazy val toFiles: Map[Int, ViewSeq[String]] => Unit = exchangeOutput => exchangeOutput.foreach {
    case (i,it) => Publisher.write(i, it)
  }
}
