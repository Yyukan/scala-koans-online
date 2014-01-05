package koans


import org.specs2.mutable._

import scala.io._
import models.KoansParser

/**
 * Test for Koan parser
 */
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

  }
}

