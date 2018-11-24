package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.validation.{MethodValidation, Size, ValidationResult}
import io.github.cactacea.backend.utils.validaters.Validations
import io.swagger.annotations.ApiModelProperty

case class PutSessionAccountName(
                                  @ApiModelProperty(value = "Account name.", required = true)
                                  @Size(min = 2, max = 30) name: String
                                ) {

  @MethodValidation
  def accountNameCheck: ValidationResult = Validations.validateAccountName(name)

}
