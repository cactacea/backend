package io.github.cactacea.backend.utils.auth

import java.util.Locale

import com.twitter.util.Local
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.Accounts
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors

object CactaceaContext {

  private[this] val localAuthenticated = new Local[Boolean]
  def authenticated: Boolean = localAuthenticated() match {
    case Some(authenticated) => authenticated
    case None => false
  }
  def setAuthenticated(authenticated: Boolean): Unit = localAuthenticated.update(authenticated)
  def clearAuthenticated(): Unit = localAuthenticated.clear()


  private[this] val localLocales = new Local[Seq[Locale]]
  def locales: Seq[Locale] = localLocales() match {
    case Some(locales) => locales
    case None => Seq[Locale](Locale.US)
  }
  def setLocales(locales: Seq[Locale]): Unit = localLocales.update(locales)
  def clearLocales(): Unit = localLocales.clear()

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
