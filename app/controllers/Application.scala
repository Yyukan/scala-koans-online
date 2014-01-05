package controllers

import play.api._
import play.api.mvc._
import models.Koan

import scala.concurrent.ExecutionContext.Implicits.global
// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object Application extends Controller with MongoController {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def koans = Action.async {
    import models.JsonFormats.koanFormat

    def collection: JSONCollection = db.collection[JSONCollection]("koans")

    Logger.info("Collections is " + collection)

    val user = Koan("first koan", "{first code}")
    val futureResult = collection.insert(user)

    // when the insert is performed, send a OK 200 result
    futureResult.map(lastError => Ok(views.html.koans("Mongo LastError: %s".format(lastError))))
  }

}