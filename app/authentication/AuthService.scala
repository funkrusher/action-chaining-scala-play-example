package authentication

import javax.inject.Inject
import play.api.Configuration

class AuthService @Inject()(config: Configuration) {

  def getTest() = {
    // this class would do all the authentication-logic, possibly using injected helper-classes
    // within the project.
    "test"
  }
}