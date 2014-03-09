package models

import java.io.File
import scala.tools.nsc._
import scala.tools.nsc.interpreter._
import scala.tools.nsc.util.ClassPath

/**
 * Compiles and execute koan
 */
object KoansInterpreter {

  val settings = new Settings
  settings.bootclasspath.value +=
    ClassPath.join(
      scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/scala-library.jar",
      scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/scalatest_2.10-2.0.jar" )
  settings.sourcepath.value +=
    scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/src"

  def execute(koan: String):Unit = {
    val in = new IMain(settings){
      override protected def parentClassLoader = settings.getClass.getClassLoader
    }

    val source = s"""
    import org.scalatest.matchers.ShouldMatchers
    import support.KoanSuite

    class AboutAdvancedOption extends KoanSuite with ShouldMatchers { ${koan} }"""

    in.interpret(source) match {
      case x  => println(x)
    }

    val output = in.interpret("(new AboutAdvancedOption).execute()")
    // TODO:oshtykhno return resule
    println("Execution result " + output)
  }

}
