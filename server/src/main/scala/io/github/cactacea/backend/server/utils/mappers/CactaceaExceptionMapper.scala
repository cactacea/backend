package io.github.cactacea.backend.server.utils.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.server.utils.mappers.ResponseBuilderExtension._

@Singleton
class CactaceaExceptionMapper @Inject()(response: ResponseBuilder) extends ExceptionMapper[CactaceaException] {

  override def toResponse(request: Request, exception: CactaceaException): Response = {
    response.create(exception.error)
  }

}
