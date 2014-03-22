package models

import java.io.File
import scala.tools.nsc._
import scala.tools.nsc.interpreter._
import scala.tools.nsc.util.ClassPath
import java.io.ByteArrayOutputStream
import java.io.PrintStream

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

  def execute(koan: String): String = {
    val in = new IMain(settings) {
      override protected def parentClassLoader = settings.getClass.getClassLoader
    }

    val source = s"""
    import org.scalatest.matchers.ShouldMatchers
    import support.KoanSuite

    class AboutAdvancedOption extends KoanSuite with ShouldMatchers { ${koan} }"""

    val output1 = exec(source, in)
    val output2 = exec("(new AboutAdvancedOption).execute()", in)

    s"$output1\n$output2"
  }

  private def exec(cmd: String, in: IMain): String = {
    val baos = new ByteArrayOutputStream();
    val stream = new PrintStream(baos);
    try {
      Console.withOut(stream)(in.interpret(cmd));
      baos.toString
    } finally {
      stream.flush
    }
  }

}
