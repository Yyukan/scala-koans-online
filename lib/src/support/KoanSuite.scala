package support

import org.scalatest.exceptions.TestPendingException
import org.scalatest._
import org.scalatest.matchers.{ Matcher, ShouldMatchers }
import org.scalatest.events.{ TestPending, TestFailed, TestIgnored, TestSucceeded, Event }

abstract class KoanSuite extends FunSuite with ShouldMatchers {

  var isPending: Boolean = false

  def koan(name: String)(fun: => Unit) {
    isPending = false
    test(name.stripMargin('|'))(fun)
  }

  def __ : Matcher[Any] = {
    isPending = true
    throw new TestPendingException
  }

  protected class ___ extends Exception {
    override def toString = "___"
  }

  protected override def runTest(testName: String, args: org.scalatest.Args) = {
    val result: Status = super.runTest(testName, args)
    if (result.succeeds()) {
      if (isPending) {
        alert(" PENDING ")
      } else {
        note(" SUCCESS ")
      }
    }
    result
  }

}
