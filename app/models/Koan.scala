package models

/**
 * Represents Koan
 */
case class Koan(description: String, content: String, suite: String, order: Long = 0L)

/**
 * Represents Koan Suite
 */
case class KoanSuite(name: String, context: String)

object JsonFormats {
  import play.api.libs.json.Json

  implicit val koanFormat = Json.format[Koan]
  implicit val suiteFormat = Json.format[KoanSuite]
}