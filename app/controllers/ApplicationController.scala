package controllers

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import authentication.AuthenticatedRequest
import authorization.{Authorized, AuthorizedOneOf, OneOfRole}

import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class ApplicationController @Inject()(
                                       cc: ControllerComponents) extends AbstractController(cc) with I18nSupport with MyCommonActions {

  def index: Action[AnyContent] = Action { implicit request =>
    Ok("Slick with Scala and Play-Framework Example")
  }

  /**
   * Should return success, as the user has the "manager" role.
   *
   * @return Example page.
   */
  def exampleSuccess = AuthActionRoleJi { request =>
    Authorized(request).oneOf("manager") {
      Authorized(request).oneOf("tester") {
        Ok("success")
      }
      Ok("success")
    } otherwise {
      Unauthorized
    }
  }

  /**
   * Should return Unauthorized, as the user does not have the "collaborator" role.
   *
   * @return Example page.
   */
  def exampleError = AuthAction.andThen(AuthorizedOneOfAction("ji_admin")) { request =>
    Authorized(request).oneOf("collaborator", "admin") {
      Ok("success")
    } otherwise {
      Unauthorized
    }
  }

}
