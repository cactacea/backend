package io.github.cactacea.backend.server.utils.context

import com.twitter.util.Local
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors

object CactaceaContext {

  private[this] val localScope = new Local[Option[String]]
  def scope: Option[String] = localScope() match {
    case Some(scope) => scope
    case None => None
  }
  def setScope(scope: Option[String]): Unit = localScope.update(scope)
  def clearScope(): Unit = localScope.clear()

  private[this] val localUser = new Local[User]
  def user: User = localUser() match {
    case Some(user) => user
    case None => throw new CactaceaException(CactaceaErrors.UserNotRegistered)
  }
  def setUser(user: User): Unit = localUser.update(user)
  def clearId(): Unit = localUser.clear()

  def sessionId: SessionId = localUser() match {
    case Some(a) => a.id.sessionId
    case None => throw new CactaceaException(CactaceaErrors.UserNotRegistered)
  }

  def userName: String = localUser() match {
    case Some(a) => a.userName
    case None => throw new CactaceaException(CactaceaErrors.UserNotRegistered)
  }

  private[this] val localDeviceType = new Local[DeviceType]
  def deviceType: DeviceType = localDeviceType() match {
    case Some(deviceType) => deviceType
    case None => throw new CactaceaException(CactaceaErrors.APIKeyIsInValid)
  }
  def setDeviceType(deviceType: DeviceType): Unit = localDeviceType.update(deviceType)
  def clearDeviceType(): Unit = localDeviceType.clear()

}
