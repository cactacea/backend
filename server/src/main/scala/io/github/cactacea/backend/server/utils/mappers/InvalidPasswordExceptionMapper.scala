package io.github.cactacea.backend.server.utils.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.filhouette.impl.exceptions.InvalidPasswordException

@Singleton
class InvalidPasswordExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[InvalidPasswordException] {

  override def toResponse(request: Request, e: InvalidPasswordException): Response = {
    val error = CactaceaErrors.UserNameOrPasswordNotMatched
    val errors = CactaceaErrors(Seq(error))
    error.status match {
      case Status.NotFound =>
        response.notFound(errors)
      case Status.BadRequest =>
        response.badRequest(errors)
      case Status.Unauthorized =>
        response.unauthorized(errors)
      case _ =>
        response.internalServerError(errors)
    }
  }

}