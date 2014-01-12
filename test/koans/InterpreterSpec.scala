package koans

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner


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
    assert(false) // should be true
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
  
  

  "Koan Interpreter" should {

    "test koan interpret" in {


       source.length === 742
    }

  }

}
