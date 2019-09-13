package io.github.cactacea.backend.server.utils.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.server.utils.mappers.ResponseBuilderExtension._
import io.github.cactacea.filhouette.api.exceptions.ProviderException

@Singleton
class ProviderExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[ProviderException] {

  override def toResponse(request: Request, e: ProviderException): Response = {
    response.create(CactaceaErrors.UserNameOrPasswordNotMatched)
  }

}