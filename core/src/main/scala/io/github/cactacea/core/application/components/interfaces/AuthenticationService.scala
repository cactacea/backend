package io.github.cactacea.core.application.components.interfaces

import com.twitter.util.Future

trait AuthenticationService {

  val socialAccountType: String
  def issueCode(socialAccountIdentifier: String): Future[Unit]
  def validateCode(socialAccountIdentifier: String, authenticationCode: String): Future[String]
  def resetPassword(socialAccountIdentifier: String): Future[Unit]

}
