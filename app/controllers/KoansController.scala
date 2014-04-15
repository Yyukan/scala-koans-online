package controllers

import play.api._
import play.api.mvc._
import models.{ KoansInterpreter, KoansParser, Koan, KoanSuite }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import reactivemongo.api._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.collection.Map
import models.JsonFormats.koanFormat
import models.JsonFormats.suiteFormat

/**
 * Controller represents koans API
 */
object KoansController extends Controller with MongoController {
  /** koans collection */
  private def koansCollection: JSONCollection = db.collection[JSONCollection]("koans")
  /** suite collection */
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

  /**
   * Returns list of koan suites as JSON
   */
  def suitesList = Action.async {
    val cursor: Cursor[KoanSuite] = suiteCollection.find(Json.obj()).cursor[KoanSuite]
    val result: Future[List[KoanSuite]] = cursor.collect[List]()
    result.map { suite =>
      Ok(Json.toJson(suite))
    }
  }

  /**
   * Returns list of koan suites based on query as JSON
   *
   * @param query - suite name as part of regexp expression
   */
  def suites(query: String) = Action.async {
    val jsonQuery = Json.obj("name" -> Json.obj("$regex" -> s"$query.*", "$options" -> "i"))
    val cursor: Cursor[KoanSuite] = suiteCollection.find(jsonQuery).cursor[KoanSuite]
    val result: Future[List[KoanSuite]] = cursor.collect[List]()
    result.map { suite =>
      Ok(Json.toJson(suite))
    }
  }

  /**
   * Returns list of all koans as JSON
   */
  def koansList = Action.async {
    val result: Future[List[Koan]] = koansAll()
    result.map { result =>
      Ok(Json.toJson(result))
    }
  }

  /**
   * Returns list of all koans as future
   * @return - future with koans list
   */
  def koansAll(): Future[List[Koan]] = {
    val cursor: Cursor[Koan] = koansCollection.find(Json.obj()).cursor[Koan]
    cursor.collect[List]()
  }

  /**
   * Returns list of koans for specified suite
   */
  def koans(suite: String) = Action.async {
    val future: Future[Option[KoanSuite]] = suiteCollection.find(Json.obj("name" -> suite)).one[KoanSuite]

    future.flatMap { koanSuite =>
      val cursor: Cursor[Koan] = koansCollection.find(Json.obj("suite" -> suite)).cursor[Koan]

      val result: Future[List[Koan]] = cursor.collect[List]()

      result.map { list =>
        Ok(Json.obj("name" -> suite, "context" -> koanSuite.get.context, "koans" -> Json.toJson(list.map(koan => koan.order).sorted)))
      }
    }
  }

  /**
   * Returns koan json for specified suite by specified order
   */
  def koan(suite: String, order: Long) = Action.async {
    import models.JsonFormats.koanFormat

    val filter = Json.obj(
      "suite" -> suite,
      "order" -> order)

    val cursor: Cursor[Koan] = koansCollection.find(filter).cursor[Koan]

    val result: Future[List[Koan]] = cursor.collect[List]()

    result.map { list =>
      // check if empty - go to the next suite
      val koan: Koan = list.head

      Ok(Json.obj(
        "id" -> koan.order,
        "suite" -> koan.suite,
        "description" -> koan.description,
        "content" -> s"""  koan(\"\"\"${koan.description}\"\"\") ${koan.content}"""))
    }
  }

  /**
   * Compiles and interprets koan, returns result as JSON
   */
  def compile = Action(parse.json) { request =>
    val json: JsValue = request.body
    val koan: String = (json \ "koan").as[String]
    val suite: String = (json \ "suite").as[String]
    Logger.debug(s"Suite [$suite] koan to execute $koan")

    val result:(String, Int) = KoansInterpreter.execute(koan, suite)
    Logger.debug("Compilation result is " + result)
    Ok(Json.obj("returnCode" -> result._2, "output" -> result._1))
  }

}
