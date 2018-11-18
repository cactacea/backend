package io.github.cactacea.backend.utils.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors

@Singleton
class CactaceaExceptionMapper @Inject()(response: ResponseBuilder) extends ExceptionMapper[CactaceaException] {

  override def toResponse(request: Request, exception: CactaceaException) = {
    val error = exception.error
    val errors = CactaceaErrors(Seq(error))
    if (error.status == Status.NotFound) {
      response.notFound(errors)
    } else if (error.status == Status.BadRequest) {
      response.badRequest(errors)
    } else if (error.status == Status.Unauthorized) {
      response.unauthorized(errors)
    } else {
      response.internalServerError(errors)
    }
  }

}
