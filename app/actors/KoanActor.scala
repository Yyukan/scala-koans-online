package actors

import akka.actor.Actor
import models.KoanSuite
import models.Koan
import play.modules.reactivemongo.{ReactiveMongoPlugin, MongoController}
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import play.api.libs.json.Json
import scala.concurrent.Future
import play.api.Logger
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

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

  private def koansCollection: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("koans")
  private def suiteCollection: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("suite")


  override def receive = {
    case ListAllSuites => onListAllSuites()
//    case GetKoan(id) => sender ! KoanResult(getKoan(id))
//    case GetKoanSuite(id) => sender ! SuiteResult(suites.get(id))
    case x => println(x)
  }

  def onListAllSuites():Unit = {
    import models.JsonFormats.suiteFormat
    import models.JsonFormats.koanFormat

    val cursor: Cursor[KoanSuite] = suiteCollection.find(Json.obj()).cursor[KoanSuite]

    val result: Future[List[KoanSuite]] = cursor.collect[List]()

    result.map { list =>
      Logger.info(s"Found ${list.size} suites")
      sender ! SuitesResult(List())
    }

    Await.result(result, 5 seconds)
  }

}