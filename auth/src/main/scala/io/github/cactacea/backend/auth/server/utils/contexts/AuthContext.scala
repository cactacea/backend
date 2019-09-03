package io.github.cactacea.backend.auth.server.utils.contexts

import com.twitter.util.Local
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors

object AuthContext {

  private[this] val localUser = new Local[Authentication]
  def auth: Authentication = localUser() match {
    case Some(auth) => auth
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }
  def setAuth(auth: Authentication): Unit = localUser.update(auth)
  def clearId(): Unit = localUser.clear()

  private[this] val localScope = new Local[Option[String]]
  def scope: Option[String] = localScope() match {
    case Some(scope) => scope
    case None => None
  }
  def setScope(scope: Option[String]): Unit = localScope.update(scope)
  def clearScope(): Unit = localScope.clear()

}
