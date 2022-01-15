package authentication

import play.api.mvc.{Request, WrappedRequest}

case class AuthenticatedRequest[A](
                                    token: String,
                                    roles: Seq[String],
                                    request: Request[A]) extends WrappedRequest[A](request)

