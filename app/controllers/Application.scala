package controllers

import play.api._
import play.api.mvc._
import models.{KoansParser, Koan}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object Application extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("koans")

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def koans = Action.async {
    import models.JsonFormats.koanFormat

    Logger.info("Collections is " + collection)

    import sys.process._
    import java.net.URL
    import java.io.File

    val zipFileName = "/tmp/koans.zip"
    val file = new File(zipFileName)

    if (!file.exists()) {
      // download last koans archive
      new URL("https://bitbucket.org/dmarsh/scalakoansexercises/get/tip.zip") #> new File(zipFileName) !!
    }

    val zip = new java.util.zip.ZipFile(zipFileName)

    import scala.collection.JavaConverters._
    val entries = zip.entries.asScala

    val sources = entries.filter(entry => entry.getName().contains("/src/test/scala/org/functionalkoans/forscala/"))

    val sourceMap:Map[String, String] = sources.flatMap{ entry =>
      Map(entry.getName -> scala.io.Source.fromInputStream(zip.getInputStream(entry)).getLines().mkString("\n"))
    }.toMap

    var sum = 0

    sourceMap.foreach {
      case (key, value) => {

        val koans = KoansParser.parse(value)
        println("File " + key.substring(key.lastIndexOf("/"), key.length - 1) + "koans " + koans.size)
        sum += koans.size
      }
    }

    println("Koans size " + sum)







    val user = Koan("first koan", "{first code}")
    val futureResult = collection.insert(user)

    // when the insert is performed, send a OK 200 result
    futureResult.map(lastError => Ok(views.html.koans("Mongo LastError: %s".format(lastError))))
  }


  def koansAll = Action.async {
    import models.JsonFormats.koanFormat

    val cursor: Cursor[Koan] = collection.find(Json.obj()).cursor[Koan]

    val result: Future[List[Koan]] = cursor.collect[List]()

    result.map { result =>
      Ok(result.toString)
    }
  }
}