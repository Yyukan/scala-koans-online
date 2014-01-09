package actors

import akka.actor.Actor
import models.KoanSuite
import models.Koan

object KoanActor {

  object ListAllSuites
  case class SuitesResult(suites: Seq[KoanSuite])

  case class GetKoan(id: Long)
  case class KoanResult(koan: Koan)

}

/**
 * Actor to retrieve koans:
 * <li>koan by id
 * <li>list all koan suites
 */
class KoanActor extends Actor {

  import KoanActor._

  override def receive = {
    case ListAllSuites => sender ! SuitesResult(List(KoanSuite("TestSuite1", List(1, 2)),
      KoanSuite("TestSuite2", List(3, 4))))
    case GetKoan(id) => sender ! KoanResult(Koan("Test Description 1", s"""
koan("some koan ${id}") { 
    println("id=${id}") 
}"""))
    case _ =>
  }

}