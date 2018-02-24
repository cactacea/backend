package io.github.cactacea.core.util.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.json.internal.caseclass.exceptions.CaseClassMappingException
import com.twitter.finatra.validation.ErrorCode._
import io.github.cactacea.core.util.responses.{Errors, CactaceaError}

@Singleton
class CaseClassExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[CaseClassMappingException] {

  override def toResponse(request: Request, e: CaseClassMappingException): Response =
    response.badRequest(errorsResponse(e))

  private def errorsResponse(e: CaseClassMappingException) = {
    val errors = e.errors.map({ error =>
      (error.reason.code) match {
        case InvalidCountryCodes(_)         => CactaceaError.InvalidCountryCodesValidationError(s"${error.getMessage()}")
        case InvalidTimeGranularity(_, _)   => CactaceaError.InvalidTimeGranularityValidationError(s"${error.getMessage()}")
        case InvalidUUID(_)                 => CactaceaError.InvalidUUIDValidationError(s"${error.getMessage()}")
        case InvalidValues(_, _)            => CactaceaError.InvalidValuesValidationError(s"${error.getMessage()}")
        case JsonProcessingError(_)         => CactaceaError.JsonProcessingError(s"${error.getMessage()}")
        case RequiredFieldMissing           => CactaceaError.RequiredFieldMissingValidationError(s"${error.getMessage()}")
        case SizeOutOfRange(_, _, _)        => CactaceaError.SizeOutOfRangeValidation(s"${error.getMessage()}")
        case TimeNotFuture(_)               => CactaceaError.TimeNotFutureValidation(s"${error.getMessage()}")
        case TimeNotPast(_)                 => CactaceaError.TimeNotPastValidation(s"${error.getMessage()}")
        case ValueCannotBeEmpty             => CactaceaError.ValueCannotBeEmptyValidation(s"${error.getMessage()}")
        case ValueOutOfRange(_, _, _)       => CactaceaError.ValueOutOfRangeValidation(s"${error.getMessage()}")
        case ValueTooLarge(_, _)            => CactaceaError.ValueTooLargeValidation(s"${error.getMessage()}")
        case ValueTooSmall(_, _)            => CactaceaError.ValueTooSmallValidation(s"${error.getMessage()}")
        case Unknown                        => CactaceaError.Unknown(s"${error.getMessage()}")
      }
    })
    Errors(errors)
  }
}

