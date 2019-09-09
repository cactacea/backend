package io.github.cactacea.backend.server.utils.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.oauth2.OAuthError
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.server.utils.mappers.ResponseBuilderExtension._

@Singleton
class OAuthErrorExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[OAuthError] {

  override def toResponse(request: Request, e: OAuthError): Response = {
    response.create(CactaceaErrors.SessionNotAuthorized)
  }

}