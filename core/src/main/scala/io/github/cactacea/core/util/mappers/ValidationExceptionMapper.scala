package io.github.cactacea.core.util.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.Errors

@Singleton
class ValidationExceptionMapper @Inject()(response: ResponseBuilder) extends ExceptionMapper[CactaceaException] {

  override def toResponse(request: Request, exception: CactaceaException) = {
    val error = exception.error
    val errors = Errors(Seq(error))
    if (error.status == Status.NotFound) {
      response.notFound(errors)
    } else if (error.status == Status.BadRequest) {
      response.badRequest(errors)
    } else {
      response.internalServerError(errors)
    }
  }

}
