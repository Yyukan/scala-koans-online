package koans


import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._

import scala.io._
import org.specs2.matcher.ShouldMatchers

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

    "test koans size " in {
      KoansParser.parse(koanSource).size shouldEqual 3
    }
  }
}

case class Koan(content: String, description: String)

object KoansParser {

  val koanPattern = """koan\(\"(.*)\"\)\s""".r

  def parse(source: String):Seq[Koan] = {
    koanPattern.findAllMatchIn(source).map(koan => {
      Koan("", koan.group(1))
    }).toSeq
  }
}

