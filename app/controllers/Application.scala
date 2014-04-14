package controllers

import play.api.mvc._

/**
 * Entry point, just returns index page
 */
object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

}