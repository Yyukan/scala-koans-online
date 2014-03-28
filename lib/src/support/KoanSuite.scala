package support

import org.scalatest.exceptions.TestPendingException
import org.scalatest._
import org.scalatest.matchers.{Matcher, ShouldMatchers}
import org.scalatest.events.{TestPending, TestFailed, TestIgnored, TestSucceeded, Event}

trait KoanSuite extends FunSuite with ShouldMatchers {

  def koan(name : String)(fun: => Unit) { test(name.stripMargin('|'))(fun) }

  def  __ : Matcher[Any] = {
    throw new TestPendingException
  }

  protected class ___ extends Exception {
    override def toString = "___"
  }

  protected override def runTest(testName: String, args : org.scalatest.Args) = {
    val result: Status = super.runTest(testName, args)
    if (result.succeeds()) {
      note(" SUCCESS ")
    }
    result
  }

}
