package controllers

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import authorization.Authorized

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
     * Returns the example page as HTML ('/example').
     *
     * @return Example page.
     */
    def example = AuthenticatedAction.andThen(AuthorizedAction(Seq("manager"))) { request =>
        Authorized(request, Seq("manager")) {
            Ok(views.html.example())
        } otherwise {
            Unauthorized
        }
    }

}
