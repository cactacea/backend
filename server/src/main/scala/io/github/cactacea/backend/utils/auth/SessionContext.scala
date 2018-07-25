package io.github.cactacea.backend.utils.auth

import java.util.Locale

import com.twitter.util.Local
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors

object SessionContext {

  private[this] val localAuthenticated = new Local[Boolean]
  def authenticated = localAuthenticated() match {
    case Some(authenticated) => authenticated
    case None => false
  }
  def setAuthenticated(authenticated: Boolean) = localAuthenticated.update(authenticated)
  def clearAuthenticated() = localAuthenticated.clear()


  private[this] val localLocales = new Local[Seq[Locale]]
  def locales = localLocales() match {
    case Some(locales) => locales
    case None => Seq[Locale](Locale.US)
  }
  def setLocales(locales: Seq[Locale]) = localLocales.update(locales)
  def clearLocales() = localLocales.clear()


  private[this] val localUdid = new Local[String]
  def udid = localUdid() match {
    case Some(udid) => udid
    case None => ""
  }
  def setUdid(udid: String) = localUdid.update(udid)
  def clearUdid() = localUdid.clear()


  private[this] val localSessionId = new Local[SessionId]
  def id = localSessionId() match {
    case Some(sessionId) => sessionId
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }
  def setId(sessionId: SessionId) = localSessionId.update(sessionId)
  def clearId() = localSessionId.clear()

  private[this] val localDeviceType = new Local[DeviceType]
  def deviceType = localDeviceType() match {
    case Some(deviceType) => deviceType
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }
  def setDeviceType(deviceType: DeviceType) = localDeviceType.update(deviceType)
  def clearDeviceType() = localDeviceType.clear()

  private[this] val localPermissions = new Local[Int]
  def permissions = localPermissions() match {
    case Some(permissions) => permissions
    case None => throw new CactaceaException(CactaceaErrors.SessionNotAuthorized)
  }
  def setPermissions(permissions: Int) = localPermissions.update(permissions)
  def clearPermissions() = localPermissions.clear()

}
