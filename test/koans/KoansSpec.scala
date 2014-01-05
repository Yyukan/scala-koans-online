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

  def koanSource: String = resourceAsString("AboutAdvancedOptions")

  "Koan parser" should {

    "test koans source proper size" in {
      koanSource must have size(1650)
    }

    "test koans size" in {
      val koans = KoansParser.parse(koanSource)

      koans.foreach(println(_))

      koans.size shouldEqual 3
    }

  }
}

