package io.github.cactacea.core.util.auth

import com.twitter.util.Local

object RequestContext {
  private[this] val local = new Local[Boolean]

  def authorized = local() match {
    case Some(authorized) => authorized
    case None => false
  }

  def setAuthorized(authorized: Boolean) = local.update(authorized)

  def clear() = local.clear()
}