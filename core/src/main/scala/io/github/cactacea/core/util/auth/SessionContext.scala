package io.github.cactacea.core.util.auth

import java.util.Locale

import com.twitter.util.Local
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError

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
    case None => throw new CactaceaException(CactaceaError.SessionNotAuthorized)
  }
  def setId(sessionId: SessionId) = localSessionId.update(sessionId)
  def clearId() = localSessionId.clear()

  //  private val idFiled = Request.Schema.newField[SessionId]()
//  implicit class IdContextSyntax(val request: Request) extends AnyVal {
//    def id: SessionId = request.ctx(idFiled)
//  }
//  def setId(request: Request, id: SessionId): Unit = {
//    request.ctx.update(idFiled, id)
//  }
//
//  private val udidField = Request.Schema.newField[String]()
//  implicit class UdidContextSyntax(val request: Request) extends AnyVal {
//    def udid: String = request.ctx(udidField)
//  }
//  def setUdid(request: Request, udid: String): Unit = {
//    request.ctx.update(udidField, udid)
//  }

}
