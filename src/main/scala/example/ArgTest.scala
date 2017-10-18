package example


//cd /i/p/ralfoenning/client/;
//find /i/p/ralfoenning/client/lib_managed/jars/ -type f -exec cp {} lib_managed/ \;
//java -cp ./target/scala-2.12/client_2.12-0.1.0-SNAPSHOT.jar:lib_managed/* example.ArgTest 1 2 3

object ArgTest extends App {

  println(s"===")
  args.foreach(println)


}