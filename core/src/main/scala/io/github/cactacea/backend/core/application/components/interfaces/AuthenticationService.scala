package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future

trait AuthenticationService {

  val providerId: String
  def issueCode(providerKey: String): Future[Unit]
  def validateCode(providerKey: String, authenticationCode: String): Future[String]
  def resetPassword(providerKey: String): Future[Unit]

}
