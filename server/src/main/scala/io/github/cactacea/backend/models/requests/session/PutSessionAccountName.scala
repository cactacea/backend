package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.validation.{MethodValidation, Size, ValidationResult}
import io.github.cactacea.backend.utils.validaters.CactaceaValidations
import io.swagger.annotations.ApiModelProperty

case class PutSessionAccountName(
                                  @ApiModelProperty(value = "Account name.", required = true)
                                  @Size(min = 2, max = 30) name: String
                                ) {

  @MethodValidation
  def accountNameCheck: ValidationResult = CactaceaValidations.validateAccountName(name)

}
