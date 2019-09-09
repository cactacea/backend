package io.github.cactacea.backend.server.utils.mappers

import com.twitter.finagle.http.{Response, Status}
import com.twitter.finatra.http.response.ResponseBuilder
import io.github.cactacea.backend.core.util.responses.{CactaceaError, CactaceaErrors}

object ResponseBuilderExtension {

  implicit class RichResponseBuilder(val response: ResponseBuilder) extends AnyVal {
    def create(error: CactaceaError): Response = {
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

}
