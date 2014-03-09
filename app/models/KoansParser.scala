package models

import scala.collection._
import scala.sys.process._
import java.net.URL
import java.io.File
import scala.Predef._
import scala.collection.Map
import scala._
import scala.collection.Seq
import scala.collection.JavaConverters._
import java.security.MessageDigest


/**
 * Parser for Koan
 */
object KoansParser {

  /** koan description pattern */
  val koanPattern = """koan\s*\(\s*\"{1,3}([\w\W]+?)\"{1,3}\s*\)""".r

  /**
   * Finds and returns block of code between {}
   *
   * For example: some_method_name{ some block } returns "{ some block }".
   * Nested parentheses are taken into account
   */
  private def block(code: String):String = {

    def blockMatcher(block: String, brackets:mutable.Stack[Char], result: String):String  = {
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
          case '{' => blockMatcher(xs.mkString, mutable.Stack('{'), "{")
          case _ => block(xs.mkString)
        }
      }
    }
  }

  /**
   * Finds all koans in source code
   *
   * @param suite - suite name as string
   * @param source - code as string
   * @return - sequence of koans
   */
  def parse(suite: String, source: String):Seq[Koan] = {
    koanPattern.findAllMatchIn(source).zipWithIndex.map( koan => {
      Koan(suite = suite, description = koan._1.group(1), content = block(source.substring(koan._1.start, source.length - 1)))
    }).toSeq
  }

  /**
   * Loads koans
   */
  def load():Map[KoanSuite, Seq[Koan]] = {

    def md5(s: String) = {
      MessageDigest.getInstance("MD5").digest(s.getBytes)
    }

    /**
     * Returns Map (koan class name -> koan class content)
     *
     * @param zipFileName - zip archive file path with koans
     */
    def sources(zipFileName: String):Map[String, String] = {
      val zip = new java.util.zip.ZipFile(zipFileName)
      val entries = zip.entries.asScala

      val sources = entries.filter(entry => entry.getName.contains("/src/test/scala/org/functionalkoans/forscala/"))

      sources.flatMap{ entry =>
        Map(entry.getName -> scala.io.Source.fromInputStream(zip.getInputStream(entry)).getLines().mkString("\n"))
      }.toMap
    }

    val zipFileName = "/tmp/koans.zip"
    val file = new File(zipFileName)

    if (!file.exists()) {
      (new URL("https://bitbucket.org/dmarsh/scalakoansexercises/get/tip.zip") #> new File(zipFileName)).!!
    }

    sources(zipFileName).map {
      case (key, value) => {
        val suite = KoanSuite(name = key)
        suite -> parse(suite.name, value)
      }
    }
  }

}
