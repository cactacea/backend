package io.github.cactacea.core.util.auth

import com.twitter.util.Local

object AuthenticationContext {
  private[this] val local = new Local[Boolean]

  def authenticated = local() match {
    case Some(authenticated) => authenticated
    case None => false
  }

  def setAuthenticated(authenticated: Boolean) = local.update(authenticated)

  def clear() = local.clear()
}