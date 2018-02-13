package io

import java.io.{File, FileWriter, PrintWriter}
import java.nio.file.{Files, Paths}

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.io.Source

/**
  *   """Goal 1: test closing
    |Goal 2: externalize op
    |Goal 3: test without requiring PrintWriter (as it requires a file to be created each time)
  """.stripMargin in {
  */
class FileWriteTest5 extends FlatSpec with Matchers with BeforeAndAfter {

  before {
    clean
  }

  private def clean = {
    Files.deleteIfExists(Paths.get("testfile"))
  }

  after {
    clean
  }

  "split" should """separate two concerns""" in {

    val writer = new PrintWriter(new File("testfile"))
    try {
      writer.print("teststring") //<=====goal: externalize this as it is the only thing that varies
    } finally {
      writer.close
    }
    Source.fromFile("testfile").getLines().mkString("") shouldBe ("teststring")
  }


  "split3" should """separate two concerns""" in {

    def ttt(resource: PrintWriter, resourcefct: (PrintWriter) => Unit) = {
      try {
        resourcefct(resource) //<=====goal: externalize this as it is the only thing that varies
      } finally {
        resource.close
      }
    }

    ttt(new PrintWriter(new File("testfile")), _.print("teststring"))
    Source.fromFile("testfile").getLines().mkString("") shouldBe ("teststring")
  }

  "split4" should """separate two concerns""" in {

    def ttt[A <: { def close(): Unit }](resource: A)(resourcefct: (A) => Unit) = {
      try {
        resourcefct(resource) //<=====goal: externalize this as it is the only thing that varies
      } finally {
        resource.close
      }
    }

    ttt(new PrintWriter(new File("testfile")))(_.print("teststring"))

    def using[A <: {def close(): Unit}, B](resource: A)(f: A => B): B =
      try f(resource) finally resource.close()

    using(new FileWriter("testfile"))(_.write("teststring"))


    Source.fromFile("testfile").getLines().mkString("") shouldBe ("teststring")
  }

  "split2" should """separate two concerns""" in {

    def ttt[A <: { def close(): Unit }](closeable: A)(resourceWritingFct: A => Unit) = {
      try {
        resourceWritingFct(closeable) //<=====goal: externalize this as it is the only thing that varies
      } finally {
        closeable.close
      }
    }

    val closeable: PrintWriter = new PrintWriter(new File("testfile"))
    ttt(closeable)(_.print("teststring"))

    Source.fromFile("testfile").getLines().mkString("") shouldBe ("teststring")
  }


}




