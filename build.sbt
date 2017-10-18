import Dependencies._
import sbt.Keys.libraryDependencies

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.1",
      version      := "0.1.0-SNAPSHOT"
    )),
//    retrieveManaged := true,
    name := "client",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "com.jcraft" % "jsch" % "0.1.54",
    libraryDependencies += "org.apache.commons" % "commons-vfs2" % "2.1",

    libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.1.2",

    libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.3",

    libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6"


  )
