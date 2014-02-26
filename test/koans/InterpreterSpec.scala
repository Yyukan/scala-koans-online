package koans

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import scala.tools.nsc.interpreter.IMain
import java.io.File
import scala.tools.nsc._
import scala.tools.nsc.interpreter._
import scala.tools.nsc.util.ClassPath

/**
 * Test for Koan Interpreter
 */
@RunWith(classOf[JUnitRunner])
class InterpreterSpec extends Specification {

  val source = """import org.scalatest.matchers.ShouldMatchers
import support.KoanSuite

// meditate on AboutAsserts to see how the Scala Koans work
class AboutAsserts extends KoanSuite with ShouldMatchers {

  koan("asserts can take a boolean argument") {
    assert(true) // should be true
  }

  koan("asserts can include a message") {
    assert(false, "This should be true")
  }

  koan("true and false values can be compared with should matchers") {
    true should be(__) // should be true
  }

  koan("booleans in asserts can test equality") {
    val v1 = 4
    val v2 = 4
    v1 === __ // === is an assert. It is from ScalaTest, not from the Scala language
  }

  koan("sometimes we expect you to fill in the values") {
    assert(__ == 1 + 1)
  }
}"""

  val source1 =
    """
      import org.scalatest.FlatSpec

      class SetSpec extends FlatSpec {

        "An empty Set" should "have size 0" in {
          assert(Set.empty.size == 0)
        }

        it should "produce NoSuchElementException when head is invoked" in {
          intercept[NoSuchElementException] {
            Set.empty.head
          }
        }
      }


    """
  
  "Koan Interpreter" should {

    "test interpret simple expression and scalatest" in {

      val settings = new Settings
      settings.bootclasspath.value +=
      ClassPath.join(
         scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/scala-library.jar",
         scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/scalatest_2.10-2.0.jar" )


      val in = new IMain(settings){
        override protected def parentClassLoader = settings.getClass.getClassLoader()
      }
      val res = in.interpret("val x = 1; case class A {}")
      println("Result " + res)

      in.interpret(source1) match {
        case x  => println(x)
      }

      val output = in.interpret("(new SetSpec).execute()")
      println("Execution result " + output)

      output shouldEqual Results.Success
    }

    "test interpret and execute koan" in {

      val settings = new Settings
      settings.bootclasspath.value +=
        ClassPath.join(
          scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/scala-library.jar",
          scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/scalatest_2.10-2.0.jar" )
      settings.sourcepath.value +=
        scala.tools.util.PathResolver.Environment.javaBootClassPath + File.pathSeparator + "lib/src"

      val in = new IMain(settings){
        override protected def parentClassLoader = settings.getClass.getClassLoader()
      }

      in.interpret(source) match {
        case x  => println(x)
      }

      val output = in.interpret("(new AboutAsserts).execute()")
      println("Execution result " + output)
      source.length === 741

      output shouldEqual Results.Success
    }

  }

}
