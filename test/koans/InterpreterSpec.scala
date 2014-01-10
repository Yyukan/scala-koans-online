package koans

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner


/**
 * Test for Koan Interpreter
 */
@RunWith(classOf[JUnitRunner])
class InterpreterSpec extends Specification {

//  private val writer = new java.io.StringWriter()
//  private val interpreter = new ScalaInterpreter(new Settings(),new PrintWriter(writer));
//
//  def exec(code : String) {
//    // Clear the previous output buffer
//    writer.getBuffer.setLength(0)
//
//    // Execute the code and catch the result
//    val ir = interpreter.interpret(code);
//
//    // Return value or throw an exception based on result
//    ir match {
//      case Success => ()
//      case Error => throw new ScriptException("error in: '" +
//        code + "'\n" + writer toString)
//      case Incomplete => throw new ScriptException("incomplete in :'" +
//        code + "'\n" + writer toString)
//    }
//  }


  "Koan Interpreter" should {

    "test koan interpret" in {
       val source = """ """


       source.length === 1
    }

  }

}
