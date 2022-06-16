package actors

import akka.actor.Actor
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Request

case class Authenticate[A](request: Request[A])
case class AuthenticationResult(valid: Boolean, user: Option[JsObject] = None)

class Authenticator extends Actor {
  def receive = {
    case Authenticate(request) =>
      if(request.headers.get("Authorization").isDefined)
        sender ! AuthenticationResult(valid = true, user = Some(Json.obj()))
      else
        sender ! AuthenticationResult(valid = false)
  }
}