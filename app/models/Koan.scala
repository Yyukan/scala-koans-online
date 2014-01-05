package models

/**
 * Represents Koan
 */
case class Koan(description: String, content: String)

object JsonFormats {
  import play.api.libs.json.Json

  implicit val koanFormat = Json.format[Koan]
}