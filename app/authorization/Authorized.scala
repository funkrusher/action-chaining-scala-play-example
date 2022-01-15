package authorization

import authentication.AuthenticatedRequest
import play.api.mvc.{Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object Authorized {
  def async[T](request: AuthenticatedRequest[T], roles: Seq[String]) = {
    (block: Future[Result]) => new Authorized[T](request, roles, block)
  }
  def apply[T](request: AuthenticatedRequest[T], roles: Seq[String]) = {
    (block: Result) => new Authorized[T](request, roles, Future { block })
  }
}

class Authorized[T](request: AuthenticatedRequest[T], roles: Seq[String], success: Future[Result]) {
  def authorized = {
    if(request.roles.containsSlice(roles)) true else false
  }
  def otherwise(block: Future[Result]) : Future[Result] = if (authorized) success else block
  def otherwise(block: Result) : Result = if (authorized) Await.result(success, 1 second) else block
}