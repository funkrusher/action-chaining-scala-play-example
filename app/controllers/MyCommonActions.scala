package controllers

import authentication.{AuthService, AuthenticatedRequest}
import play.api.Logger
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * This trait helps to write Actions that can use the data of the given controller, without the need
 * for dependency injection. This helps to write Actions that can be given parameters and to have all relevant
 * Actions available in our controllers.
 */
trait MyCommonActions {
  self: BaseController =>

  @Inject() private var as: AuthService = _
  lazy val authService: AuthService = as

  val AuthActionRoleJi = AuthAction.andThen(AuthorizedRoleStartsWithAction("ji"))
  val AuthActionRoleExternal = AuthAction.andThen(AuthorizedRoleStartsWithAction("external"))

  object AuthAction extends ActionBuilder[AuthenticatedRequest, AnyContent] {

    override def parser: BodyParser[AnyContent] = self.parse.default

    override protected def executionContext: ExecutionContext = self.defaultExecutionContext

    // Called when a request is invoked. We should validate the bearer token here
    // and allow the request to proceed if it is valid.
    override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
      // 1. do Authentication Stuff !!!
      val test = authService.getTest()

      // 2. resolve Roles of the User from the Authenticated Stuff
      // and put them into the AuthenticatedRequest we create here. Following is an example (user and manager roles)
      val foundRoles = Seq("master", "manager")
      block(AuthenticatedRequest(test, foundRoles, request))
    }
  }


  case class AuthorizedOneOfAction(roles: Seq[String]) extends ActionFilter[AuthenticatedRequest] {
    def this(param : (String), params: (String)*) = this(param +: params)

    override protected def executionContext: ExecutionContext = self.defaultExecutionContext

    override protected def filter[A](request: AuthenticatedRequest[A]): Future[Option[Status]] = Future.successful {
      // check if the given roles are fulfilled by the roles of the request.
      if (request.roles.containsSlice(roles)) {
        None
      } else {
        Some(Unauthorized)
      }
    }
  }

  object AuthorizedOneOfAction {
    def apply(param : (String), params: (String)*) = new AuthorizedOneOfAction(param +: params)
  }

  case class AuthorizedRoleStartsWithAction(indicator: String) extends ActionFilter[AuthenticatedRequest] {
    override protected def executionContext: ExecutionContext = self.defaultExecutionContext

    override protected def filter[A](request: AuthenticatedRequest[A]): Future[Option[Status]] = Future.successful {
      // check if the given roles are fulfilled by the roles of the request.
      if (request.roles.find(x => x.startsWith(indicator)).isDefined) {
        None
      } else {
        Some(Unauthorized)
      }
    }
  }


}
