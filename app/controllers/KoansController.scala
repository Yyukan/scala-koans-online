package controllers

import play.api._
import play.api.mvc._
import models.{ KoansParser, Koan }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import reactivemongo.api._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import models.KoanSuite
import scala.collection.Map
import actors.KoanActor.SuitesResult
import actors.KoanActor
import reactivemongo.bson.BSONDocument

object KoansController extends Controller with MongoController {
  private def koansCollection: JSONCollection = db.collection[JSONCollection]("koans")
  private def suiteCollection: JSONCollection = db.collection[JSONCollection]("suite")

  /**
   * Administration action, removes current koan collections
   * and than loads and update
   */
  def koansParse = Action {
    import models.JsonFormats.koanFormat
    import models.JsonFormats.suiteFormat

    Logger.info("Loading and parsing koans")

    koansCollection.drop()
    suiteCollection.drop()
    Logger.info("Dropped collections [suite, koans]...")

    val suites: Map[KoanSuite, Seq[Koan]] = KoansParser.load()
    Logger.info(s"Loaded ${suites.size} suites")

    // filter suites with koans and update collections
    suites.filter(p => p._2.size != 0).foreach {
      case (suite, koans) => {
        suiteCollection.insert(suite)
        koans.foreach { koan =>
          koansCollection.insert(koan)
        }
        Logger.info(s"Inserted suite ${suite.name} koans ${koans.size}")
      }
    }
    Logger.info(s"Loaded ${suites.values.toList.size} koans, finished...")

    Ok(s"Loaded ${suites.values.toList.size} koans, finished...")
  }

  def koansList = Action.async {
    import models.JsonFormats.koanFormat

    val cursor: Cursor[Koan] = koansCollection.find(Json.obj()).cursor[Koan]

    val result: Future[List[Koan]] = cursor.collect[List]()

    result.map { result =>
      Ok(result.toString)
    }
  }

  def editor = Action.async {
    import models.JsonFormats.suiteFormat

    val cursor: Cursor[KoanSuite] = suiteCollection.find(Json.obj()).cursor[KoanSuite]

    val result: Future[List[KoanSuite]] = cursor.collect[List]()

    result.map { list =>
      Logger.info(s"Found ${list.size} suites")
        Ok(views.html.editor(0, list.head, list))
    }
  }

  def koan(suite:String, order: Long) = Action.async {
    import models.JsonFormats.koanFormat

    val filter = Json.obj(
      "suite" -> suite,
      "order" -> order)

    val cursor: Cursor[Koan] = koansCollection.find(filter).cursor[Koan]

    val result: Future[List[Koan]] = cursor.collect[List]()

    result.map { list =>
      Ok(Json.toJson(list.head))
    }
  }



}
