package io.github.cactacea.backend.server.utils.context

import com.twitter.util.Local
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.Accounts
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

  private[this] val localAccount = new Local[Accounts]
  def account: Accounts = localAccount() match {
    case Some(account) => account
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }
  def setAccount(account: Accounts): Unit = localAccount.update(account)
  def clearId(): Unit = localAccount.clear()

  def sessionId: SessionId = localAccount() match {
    case Some(a) => a.id.toSessionId
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }

  def accountName: String = localAccount() match {
    case Some(a) => a.accountName
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }

  private[this] val localDeviceType = new Local[DeviceType]
  def deviceType: DeviceType = localDeviceType() match {
    case Some(deviceType) => deviceType
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }
  def setDeviceType(deviceType: DeviceType): Unit = localDeviceType.update(deviceType)
  def clearDeviceType(): Unit = localDeviceType.clear()

}
