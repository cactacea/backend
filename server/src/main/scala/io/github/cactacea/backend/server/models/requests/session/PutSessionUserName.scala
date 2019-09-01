package io.github.cactacea.backend.server.models.requests.session

import com.twitter.finatra.validation.{MethodValidation, Size, ValidationResult}
import io.github.cactacea.backend.server.utils.validators.CactaceaValidations
import io.swagger.annotations.ApiModelProperty

case class PutSessionUserName(
                                  @ApiModelProperty(value = "User name.", required = true)
                                  @Size(min = 2, max = 30) name: String
                                ) {

  @MethodValidation
  def userNameCheck: ValidationResult = CactaceaValidations.validateUserName(name)

}
