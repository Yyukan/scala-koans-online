package actors

import akka.actor.Actor
import models.KoanSuite
import models.Koan

object KoanActor {

  object ListAllSuites
  case class SuitesResult(suites: Seq[KoanSuite])

  case class GetKoan(id: Long)
  case class KoanResult(koan: Koan)

  case class GetKoanSuite(id: Long)
  case class SuiteResult(suite: Option[KoanSuite])
}

/**
 * Actor to retrieve koans:
 * <li>koan by id
 * <li>list all koan suites
 */
class KoanActor extends Actor {

  import KoanActor._

  val suites = Map(0l -> KoanSuite(0, "TestSuite1", List(1, 2)),
    1l -> KoanSuite(1, "TestSuite2", List(3, 4)))

  override def receive = {
    case ListAllSuites => sender ! SuitesResult(suites.values.toSeq)
    case GetKoan(id) => sender ! KoanResult(getKoan(id))
    case GetKoanSuite(id) => sender ! SuiteResult(suites.get(id))
    case _ =>
  }

  private def getKoan(id: Long) =
    Koan("Test Description 1", s"""
koan("some koan ${id}") { 
    println("id=${id}") 
}""")
}