package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.validation.{MethodValidation, Size}
import io.github.cactacea.backend.util.validaters.Validations
import io.swagger.annotations.ApiModelProperty

case class PutSessionPassword(
                               @ApiModelProperty(value = "Account old password.")
                               @Size(min = 8, max = 255) oldPassword: String,

                               @ApiModelProperty(value = "Account new password.")
                               @Size(min = 8, max = 255) newPassword: String
                             ) {

  @MethodValidation
  def oldPasswordCheck = Validations.validatePassword(oldPassword)

  @MethodValidation
  def newPasswordCheck = Validations.validatePassword(newPassword)

}
