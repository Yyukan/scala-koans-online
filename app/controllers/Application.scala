package controllers

import play.api._
import play.api.mvc._
import models.{ KoansParser, Koan }
import scala.concurrent._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import reactivemongo.api._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import actors.KoanActor
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import scala.concurrent.duration._
import models.KoanSuite
import actors.CompileActor
import scala.collection.Map

object Application extends Controller with MongoController {

  private def koansCollection: JSONCollection = db.collection[JSONCollection]("koans")
  private def suiteCollection: JSONCollection = db.collection[JSONCollection]("suite")

  private val system = ActorSystem("ScalaKoansSystem")
  private implicit val timeout = Timeout(5 seconds) // needed for `?` below

  private val koanActor: ActorRef = system.actorOf(Props[KoanActor], "KoanActor")
  private val compileActor: ActorRef = system.actorOf(Props[CompileActor], "CompileActor")

  def index = Action {
    Ok(views.html.index())
  }

  def editor = Action.async {
    val suites = (koanActor ? KoanActor.ListAllSuites).mapTo[KoanActor.SuitesResult]
    suites.map { s =>
      val suite = s.suites.head
      Ok(views.html.editor(0, suite, s.suites))
    }
  }

  def compile = Action.async(parse.json) { request =>

    import actors.CompileActor.compileResultFormat
    import actors.CompileActor.compileFormat

    request.body.validate[CompileActor.Compile].map {
      case compile: CompileActor.Compile => {
        val result = (compileActor ? compile).mapTo[CompileActor.CompileResult]
        result.map { r => Ok(Json.toJson(r)) }
      }
    }.recoverTotal {
      e => Future.successful(BadRequest("Detected error:" + JsError.toFlatJson(e)))
    }
  }

  def koan(id: Long) = Action.async {
    import models.JsonFormats.koanFormat
    val koan = (koanActor ? KoanActor.GetKoan(id)).mapTo[KoanActor.KoanResult]
    koan.map { k => Ok(Json.toJson(k.koan)) }
  }

  def suite(id: Long) = Action.async {
    import models.JsonFormats.suiteFormat
    val suite = (koanActor ? KoanActor.GetKoanSuite(id)).mapTo[KoanActor.SuiteResult]
    suite.map {
      _.suite match {
        case Some(suite) => Ok(Json.toJson(suite))
        case None => NotFound
      }
    }
  }

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
}