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
import actors.KoanActor.SuitesResult

object Application extends Controller with MongoController {

  private val system = ActorSystem("ScalaKoansSystem")
  private implicit val timeout = Timeout(5 seconds) // needed for `?` below

  private val koanActor: ActorRef = system.actorOf(Props[KoanActor], "KoanActor")
  private val compileActor: ActorRef = system.actorOf(Props[CompileActor], "CompileActor")

  def index = Action {
    Ok(views.html.index())
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


}