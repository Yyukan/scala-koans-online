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

  val koanPattern = """koan\(\"(.*)\"\)\s""".r

  def block(code: String):String = {

    def blockStart(block: String, brackets:Stack[Char], accumulator: String):String  = {
      if (brackets.isEmpty) return accumulator

      block.toList match {
        case Nil => accumulator
        case x :: xs => {
          x match {
            case '}' => brackets.pop(); blockStart(xs.mkString, brackets, accumulator + '}')
            case '{' => brackets.push('{'); blockStart(xs.mkString, brackets, accumulator + '{')
            case  _ => blockStart(xs.mkString, brackets, accumulator + x)
          }
        }
      }
    }

    code.toList match {
      case x :: xs => {
        x match {
          case '{' => blockStart(xs.mkString, Stack('{'), "{")
          case _ => block(xs.mkString)
        }
      }
      case Nil => ""
    }
  }


  def parse(source: String):Seq[Koan] = {
    koanPattern.findAllMatchIn(source).map(koan => {
      Koan(description = koan.group(1), content = block(source.substring(koan.start, source.length - 1)))
    }).toSeq
  }
}

