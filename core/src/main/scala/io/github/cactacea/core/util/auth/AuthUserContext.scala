package io.github.cactacea.core.util.auth

import java.util.Locale

import com.twitter.finagle.http.Request
import io.github.cactacea.core.infrastructure.identifiers.SessionId

object AuthUserContext {

  private val idFiled = Request.Schema.newField[SessionId]()
  implicit class IdContextSyntax(val request: Request) extends AnyVal {
    def id: SessionId = request.ctx(idFiled)
  }
  def setId(request: Request, id: SessionId): Unit = {
    request.ctx.update(idFiled, id)
  }

  private val udidField = Request.Schema.newField[String]()
  implicit class UdidContextSyntax(val request: Request) extends AnyVal {
    def udid: String = request.ctx(udidField)
  }
  def setUdid(request: Request, udid: String): Unit = {
    request.ctx.update(udidField, udid)
  }

  private val localeField = Request.Schema.newField[Seq[Locale]]()
  implicit class LocalesContextSyntax(val request: Request) extends AnyVal {
    def locales: Seq[Locale] = request.ctx(localeField)
  }
  def setLocales(request: Request, locales: Seq[Locale]): Unit = {
    request.ctx.update(localeField, locales)
  }

}
