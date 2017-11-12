import java.security.MessageDigest

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
}
