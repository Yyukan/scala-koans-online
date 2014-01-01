package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def koans = Action {
    // clone or sync koans from git
    // get list of the sources with koans
    // parse koans and save to storage
    Ok(views.html.koans())
  }

}