package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.validation.{MethodValidation, Size, ValidationResult}
import io.github.cactacea.backend.utils.validaters.Validations
import io.swagger.annotations.ApiModelProperty

case class PutSessionPassword(
                               @ApiModelProperty(value = "Account old password.", required = true)
                               @Size(min = 8, max = 255) oldPassword: String,

                               @ApiModelProperty(value = "Account new password.", required = true)
                               @Size(min = 8, max = 255) newPassword: String
                             ) {

  @MethodValidation
  def oldPasswordCheck: ValidationResult = Validations.validatePassword(oldPassword)

  @MethodValidation
  def newPasswordCheck: ValidationResult = Validations.validatePassword(newPassword)

}
