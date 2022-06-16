package authorization

import authentication.AuthenticatedRequest
import play.api.mvc.{Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}


case class Authorized[T](request: AuthenticatedRequest[T]) {
  def oneOf(param : (String), params: (String)*) = AuthorizedOneOf(request, param +: params)
}

object Authorized {
  def apply[T](request: AuthenticatedRequest[T])= new Authorized[T](request)
}

object AuthorizedOneOf {
  def async[T](request: AuthenticatedRequest[T], roles: Seq[String]) = {
    (block: Future[Result]) => new AuthorizedOneOf[T](request, roles, block)
  }
  def apply[T](request: AuthenticatedRequest[T], roles: Seq[String]) = {
    (block: Result) => new AuthorizedOneOf[T](request, roles, Future { block })
  }

  def async[T](request: AuthenticatedRequest[T], param : (String), params: (String)*) = {
    (block: Future[Result]) => new AuthorizedOneOf[T](request, param +: params, block)
  }
  def apply[T](request: AuthenticatedRequest[T], param : (String), params: (String)*) = {
    (block: Result) => new AuthorizedOneOf(request, param +: params, Future { block })
  }
}

class AuthorizedOneOf[T](request: AuthenticatedRequest[T], roles: Seq[String], success: Future[Result]) {
  def authorized = {
    if(request.roles.containsSlice(roles)) true else false
  }
  def otherwise(block: Future[Result]) : Future[Result] = if (authorized) success else block
  def otherwise(block: Result) : Result = if (authorized) Await.result(success, 1 second) else block
}