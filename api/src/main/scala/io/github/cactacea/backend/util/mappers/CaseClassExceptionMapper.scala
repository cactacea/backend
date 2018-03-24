package io.github.cactacea.backend.util.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.json.internal.caseclass.exceptions.CaseClassMappingException
import com.twitter.finatra.validation.ErrorCode._
import io.github.cactacea.core.util.responses.CactaceaErrors

@Singleton
class CaseClassExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[CaseClassMappingException] {

  override def toResponse(request: Request, e: CaseClassMappingException): Response =
    response.badRequest(errorsResponse(e))

  private def errorsResponse(e: CaseClassMappingException) = {
    val errors = e.errors.map({ error =>
      (error.reason.code) match {
        case InvalidCountryCodes(_)         => CactaceaErrors.InvalidCountryCodesValidationError(s"${error.getMessage()}")
        case InvalidTimeGranularity(_, _)   => CactaceaErrors.InvalidTimeGranularityValidationError(s"${error.getMessage()}")
        case InvalidUUID(_)                 => CactaceaErrors.InvalidUUIDValidationError(s"${error.getMessage()}")
        case InvalidValues(_, _)            => CactaceaErrors.InvalidValuesValidationError(s"${error.getMessage()}")
        case JsonProcessingError(_)         => CactaceaErrors.JsonProcessingError(s"${error.getMessage()}")
        case RequiredFieldMissing           => CactaceaErrors.RequiredFieldMissingValidationError(s"${error.getMessage()}")
        case SizeOutOfRange(_, _, _)        => CactaceaErrors.SizeOutOfRangeValidation(s"${error.getMessage()}")
        case TimeNotFuture(_)               => CactaceaErrors.TimeNotFutureValidation(s"${error.getMessage()}")
        case TimeNotPast(_)                 => CactaceaErrors.TimeNotPastValidation(s"${error.getMessage()}")
        case ValueCannotBeEmpty             => CactaceaErrors.ValueCannotBeEmptyValidation(s"${error.getMessage()}")
        case ValueOutOfRange(_, _, _)       => CactaceaErrors.ValueOutOfRangeValidation(s"${error.getMessage()}")
        case ValueTooLarge(_, _)            => CactaceaErrors.ValueTooLargeValidation(s"${error.getMessage()}")
        case ValueTooSmall(_, _)            => CactaceaErrors.ValueTooSmallValidation(s"${error.getMessage()}")
        case Unknown                        => CactaceaErrors.Unknown(s"${error.getMessage()}")
      }
    })
    Errors(errors)
  }
}

