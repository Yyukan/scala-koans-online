package koans


import org.specs2.mutable._
import scala.io._
import models.{Koan, KoansParser}
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

  def aboutAdvancedOptions(): String = resourceAsString("AboutAdvancedOptions")
  def aboutManifest(): String = resourceAsString("AboutManifest")
  def aboutConstructors(): String = resourceAsString("AboutConstructors")
  
  "Koan parser" should {

    "test koans source proper size" in {
      aboutAdvancedOptions must have size 1650
    }

    "test koans size" in {
      val koans = KoansParser.parse("AboutAdvancedOptions", aboutAdvancedOptions())

      koans.size shouldEqual 3
    }

    "test parse about manifest " in {
      val koans = KoansParser.parse("AboutManifest", aboutManifest())

      koans.size shouldEqual 2
    }

    "test download and parse " in {
      val koans = KoansParser.load()
      koans.size shouldEqual 51
    }

    "test parse context " in  {

      val context = KoansParser.parseSuiteContext(aboutConstructors())
      context.contains("AboutClassWithNoClassParameter") shouldEqual true
      context.contains("AboutConstructorWithAuxiliaryConstructor") shouldEqual true
      context.contains("KoanSuite") shouldEqual false
      context.contains("koan") shouldEqual false
    }
  }

}

