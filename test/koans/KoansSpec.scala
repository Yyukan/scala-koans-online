package koans


import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._

import scala.io._
import org.specs2.matcher.ShouldMatchers
import scala.collection.mutable.Stack

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

case class Koan(description: String, content: String)

object KoansParser {

  /** koan description pattern */
  val koanPattern = """koan\(\"(.*)\"\)\s""".r

  /**
   * Finds and returns block of code between {}
   *
   * For example: some_method_name{ some block } returns "{ some block }".
   * Nested parentheses are taken into account
   */
  def block(code: String):String = {

    def blockMatcher(block: String, brackets:Stack[Char], result: String):String  = {
      if (brackets.isEmpty) return result

      block.toList match {
        case Nil => result
        case x :: xs => {
          x match {
            case '}' => brackets.pop(); blockMatcher(xs.mkString, brackets, result + '}')
            case '{' => brackets.push('{'); blockMatcher(xs.mkString, brackets, result + '{')
            case  _ => blockMatcher(xs.mkString, brackets, result + x)
          }
        }
      }
    }

    code.toList match {
      case Nil => ""
      case x :: xs => {
        x match {
          case '{' => blockMatcher(xs.mkString, Stack('{'), "{")
          case _ => block(xs.mkString)
        }
      }
    }
  }

  /**
   * Finds all koans in source code
   *
   * @param source - code as string
   * @return - sequence of koans
   */
  def parse(source: String):Seq[Koan] = {
    koanPattern.findAllMatchIn(source).map(koan => {
      Koan(description = koan.group(1), content = block(source.substring(koan.start, source.length - 1)))
    }).toSeq
  }
}

