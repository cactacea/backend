package io.github.cactacea.backend.utils.auth

import java.util.Locale

import com.twitter.util.Local
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors

object SessionContext {

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


  private[this] val localUdid = new Local[String]
  def udid: String = localUdid() match {
    case Some(udid) => udid
    case None => ""
  }
  def setUdid(udid: String): Unit = localUdid.update(udid)
  def clearUdid(): Unit = localUdid.clear()


  private[this] val localSessionId = new Local[SessionId]
  def id: SessionId = localSessionId() match {
    case Some(sessionId) => sessionId
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }
  def setId(sessionId: SessionId): Unit = localSessionId.update(sessionId)
  def clearId(): Unit = localSessionId.clear()

  private[this] val localDeviceType = new Local[DeviceType]
  def deviceType: DeviceType = localDeviceType() match {
    case Some(deviceType) => deviceType
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }
  def setDeviceType(deviceType: DeviceType): Unit = localDeviceType.update(deviceType)
  def clearDeviceType(): Unit = localDeviceType.clear()

  private[this] val localPermissions = new Local[Int]
  def permissions: Int = localPermissions() match {
    case Some(permissions) => permissions
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }
  def setPermissions(permissions: Int): Unit = localPermissions.update(permissions)
  def clearPermissions(): Unit = localPermissions.clear()

}
