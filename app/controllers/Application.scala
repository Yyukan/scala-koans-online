package controllers

import play.api.mvc._
import scala.concurrent.{Await, Future}
import models.KoanSuite
import models.Koan
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def admin = Action.async {
    val suites: Future[List[KoanSuite]] = KoansController.suitesAll()
    val koans: Future[List[Koan]] = KoansController.koansAll()

    suites.flatMap { suites =>
      koans.map { koans =>
        Ok(views.html.admin(suites, koans ))
      }
    }
  }
}