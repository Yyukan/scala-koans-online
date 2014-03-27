package controllers

import play.api.mvc._
import scala.concurrent.{ Await, Future }
import models.KoanSuite
import models.Koan
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

}