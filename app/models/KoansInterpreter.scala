package models

import java.io.File
import scala.tools.nsc._
import scala.tools.nsc.interpreter._
import scala.tools.nsc.util.ClassPath
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import play.api.Logger
import scala.tools.nsc.interpreter.Logger

/**
 * Compiles and execute koan
 */
object KoansInterpreter {

  val settings = new Settings
  settings.bootclasspath.value +=
    ClassPath.join(
      scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/scala-library.jar",
      scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/scalatest_2.10-2.0.jar")
  settings.sourcepath.value +=
    scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/src"

  def execute(koan: String, suite: String): (String, Int) = {
    val in = new IMain(settings) {
      override protected def parentClassLoader = settings.getClass.getClassLoader
    }

    val source = s"""
    import org.scalatest.matchers.ShouldMatchers
    import support.KoanSuite

    class ${suite} extends KoanSuite with ShouldMatchers { ${koan} }"""

    // first interpret koan itself and then execute
    val result = exec(source, in)
    result._2 match {
      case 0 => exec(s"(new ${suite}).execute()", in)
      case error => result
    }
  }

  private def exec(cmd: String, in: IMain): (String, Int) = {
    val buffer = new ByteArrayOutputStream()
    val stream = new PrintStream(buffer)
    try {
      Console.withOut(stream)(in.interpret(cmd)) match {
        case IR.Success => (buffer.toString, 0)
        case IR.Error => (buffer.toString, 1)
        case IR.Incomplete => (buffer.toString, 2)
      }
    } finally {
      stream.flush()
    }
  }

}
