package models

import scala.collection._

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
   * @param source - code as string
   * @return - sequence of koans
   */
  def parse(source: String):Seq[Koan] = {
    koanPattern.findAllMatchIn(source).map(koan => {
      Koan(description = koan.group(1), content = block(source.substring(koan.start, source.length - 1)))
    }).toSeq
  }
}
