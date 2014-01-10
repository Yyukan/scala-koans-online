package models

/**
 * Represents Koan
 */
case class Koan(description: String, content: String)

/**
 * Represents Koan Suite
 */
case class KoanSuite(id: Long, name: String, koanIds: Seq[Long])

object JsonFormats {
  import play.api.libs.json.Json

  implicit val koanFormat = Json.format[Koan]
  implicit val suiteFormat = Json.format[KoanSuite]
}