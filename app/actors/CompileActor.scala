package actors

import akka.actor.Actor
import play.api.libs.json.Json
import play.Logger

object CompileActor {

  case class Compile(koanId: Long, koan: String)
  case class CompileResult(isError: Boolean, message: String)

  // TODO move to json formats
  implicit val compileResultFormat = Json.format[CompileResult]
  implicit val compileFormat = Json.format[Compile]
}

class CompileActor extends Actor {

  import CompileActor._

  override def receive = {
    case Compile(koanId, koan) => {
      Logger info s"compiling: ${koan}"
      sender ! CompileResult(false, "Ok")
    }
    case _ => ???
  }

}