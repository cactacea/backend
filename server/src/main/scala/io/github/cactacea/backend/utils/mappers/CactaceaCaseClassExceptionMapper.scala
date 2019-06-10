package io.github.cactacea.backend.utils.mappers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.json.internal.caseclass.exceptions.CaseClassMappingException
import com.twitter.finatra.validation.ErrorCode._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors

@Singleton
class CactaceaCaseClassExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[CaseClassMappingException] {

  override def toResponse(request: Request, e: CaseClassMappingException): Response =
    response.badRequest(errorsResponse(e))

  private def errorsResponse(e: CaseClassMappingException): CactaceaErrors = {
    val errors = e.errors.map({ error =>
      (error.reason.code) match {
        case InvalidCountryCodes(_)         => CactaceaErrors.invalidCountryCodesValidationError(s"${error.getMessage()}")
        case InvalidTimeGranularity(_, _)   => CactaceaErrors.invalidTimeGranularityValidationError(s"${error.getMessage()}")
        case InvalidUUID(_)                 => CactaceaErrors.invalidUUIDValidationError(s"${error.getMessage()}")
        case InvalidValues(_, _)            => CactaceaErrors.invalidValuesValidationError(s"${error.getMessage()}")
        case JsonProcessingError(_)         => CactaceaErrors.jsonProcessingError(s"${error.getMessage()}")
        case RequiredFieldMissing           => CactaceaErrors.requiredFieldMissingValidationError(s"${error.getMessage()}")
        case SizeOutOfRange(_, _, _)        => CactaceaErrors.sizeOutOfRangeValidation(s"${error.getMessage()}")
        case TimeNotFuture(_)               => CactaceaErrors.timeNotFutureValidation(s"${error.getMessage()}")
        case TimeNotPast(_)                 => CactaceaErrors.timeNotPastValidation(s"${error.getMessage()}")
        case ValueCannotBeEmpty             => CactaceaErrors.valueCannotBeEmptyValidation(s"${error.getMessage()}")
        case ValueOutOfRange(_, _, _)       => CactaceaErrors.valueOutOfRangeValidation(s"${error.getMessage()}")
        case ValueTooLarge(_, _)            => CactaceaErrors.valueTooLargeValidation(s"${error.getMessage()}")
        case ValueTooSmall(_, _)            => CactaceaErrors.valueTooSmallValidation(s"${error.getMessage()}")
        case Unknown                        => CactaceaErrors.unknown(s"${error.getMessage()}")
      }
    })
    CactaceaErrors(errors)
  }
}

