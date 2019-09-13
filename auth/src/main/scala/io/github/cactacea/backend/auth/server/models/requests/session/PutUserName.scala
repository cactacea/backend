package io.github.cactacea.backend.auth.server.models.requests.session

import com.twitter.finatra.validation.{MethodValidation, Size, ValidationResult}
import io.github.cactacea.backend.auth.server.utils.validations.ValueValidator
import io.swagger.annotations.ApiModelProperty

case class PutUserName(
                                  @ApiModelProperty(value = "User name.", required = true)
                                  @Size(min = 2, max = 50) name: String
                                ) {

  @MethodValidation
  def userNameCheck: ValidationResult = ValueValidator.validateUserName(name)

}
