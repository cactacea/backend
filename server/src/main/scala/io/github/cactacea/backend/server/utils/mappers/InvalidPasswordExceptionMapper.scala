package io.github.cactacea.backend.server.utils.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.server.utils.mappers.ResponseBuilderExtension._
import io.github.cactacea.filhouette.impl.exceptions.InvalidPasswordException

@Singleton
class InvalidPasswordExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[InvalidPasswordException] {

  override def toResponse(request: Request, e: InvalidPasswordException): Response = {
    response.create(CactaceaErrors.UserNameOrPasswordNotMatched)
  }

}