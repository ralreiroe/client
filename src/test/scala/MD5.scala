import java.security.MessageDigest

import utils.Spec

class MD5 extends Spec {

  /**
    * An MD5 checksum is a 32-character hexadecimal number that is computed on a file. If two files have the same MD5 checksum value, then there is a high probability that the two files are the same.
    */
  //https://stackoverflow.com/questions/38855843/scala-one-liner-to-generate-md5-hash-from-string
  "generate md5" in {

    def getMd5(inputStr: String): String = {
      val md: MessageDigest = MessageDigest.getInstance("MD5")
      md.digest(inputStr.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft("") {_ + _}
    }

    println(getMd5("This is the input"))
  }


  "streaming" in {

    import java.security.{MessageDigest, DigestInputStream}
    import java.io.{File, FileInputStream}

    // Compute a hash of a file
    // The output of this function should match the output of running "md5 -q <file>"
    def computeHash(path: String): String = {
      val buffer = new Array[Byte](8192)
      val md5 = MessageDigest.getInstance("MD5")

      val dis = new DigestInputStream(new FileInputStream(new File(path)), md5)
      try { while (dis.read(buffer) != -1) { } } finally { dis.close() }

      md5.digest.map("%02x".format(_)).mkString
    }
  }
}
