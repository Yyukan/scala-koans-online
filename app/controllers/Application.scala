package controllers

import play.api._
import play.api.mvc._
// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object Application extends Controller with MongoController {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def koans = Action {

    def collection: JSONCollection = db.collection[JSONCollection]("koans")

    Logger.info("Collections is " + collection)

    // clone or sync koans from git
    // get list of the sources with koans
    // parse koans and save to storage
    Ok(views.html.koans(collection.toString))
  }

}