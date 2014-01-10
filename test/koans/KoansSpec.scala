package koans


import org.specs2.mutable._
import scala.io._
import models.KoansParser
import org.junit.runner._
import org.specs2.runner.JUnitRunner
/**
 * Test for Koan parser
 */
@RunWith(classOf[JUnitRunner])
class KoansSpec extends Specification {

  def resourceAsString(name: String): String = {
    val resource = Source.fromURL(this.getClass.getResource(name))
    if (resource == null) {
      println(s"Resource is not found $resource")
    }
    resource.mkString
  }

  def aboutAdvancedOptions: String = resourceAsString("AboutAdvancedOptions")
  def aboutManifest: String = resourceAsString("AboutManifest")
  
  
  "Koan parser" should {

    "test koans source proper size" in {
      aboutAdvancedOptions must have size(1650)
    }

    "test koans size" in {
      val koans = KoansParser.parse(aboutAdvancedOptions)

      koans.foreach(println(_))

      koans.size shouldEqual 3
    }

    "test parse about manifest " in {
      val koans = KoansParser.parse(aboutManifest)

      koans.foreach(println(_))

      koans.size shouldEqual 2
    }

    "test download and parse " in {
      import sys.process._
      import java.net.URL
      import java.io.File

      val zipFileName = "/tmp/koans.zip"
      val file = new File(zipFileName)

      if (!file.exists()) {
        // download last koans archive
        new URL("https://bitbucket.org/dmarsh/scalakoansexercises/get/tip.zip") #> new File(zipFileName) !!
      }

      val zip = new java.util.zip.ZipFile(zipFileName)

      import scala.collection.JavaConverters._
      val entries = zip.entries.asScala

      val sources = entries.filter(entry => entry.getName().contains("/src/test/scala/org/functionalkoans/forscala/"))

      val sourceMap:Map[String, String] = sources.flatMap{ entry =>
        Map(entry.getName -> scala.io.Source.fromInputStream(zip.getInputStream(entry)).getLines().mkString("\n"))
      }.toMap

      var sum = 0

      sourceMap.foreach {
        case (key, value) => {

          val koans = KoansParser.parse(value)
          println("File " + key.substring(key.lastIndexOf("/"), key.length) + "koans " + koans.size)
          sum += koans.size
        }
      }

      sum shouldEqual 324
    }
  }
}

