package io.github.cactacea.backend.models.requests.sessions

import com.twitter.finatra.request.Header
import com.twitter.finatra.validation._
import io.github.cactacea.backend.utils.validaters.Validations
import io.swagger.annotations.ApiModelProperty

case class PostSignUp(
                       @ApiModelProperty(value = "Account name.", required = true)
                       @Size(min = 2, max = 30) accountName: String,

                       @ApiModelProperty(value = "Account password.", required = true)
                       @Size(min = 8, max = 255) password: String,

                       @ApiModelProperty(value = "Unique Device Identifier.", required = true)
                       @UUID udid: String,

                       @ApiModelProperty(hidden = true)
                       @Header("user-agent") userAgent: Option[String]

                  ) {

  @MethodValidation
  def accountNameCheck: ValidationResult = Validations.validateAccountName(accountName)

  @MethodValidation
  def passwordCheck: ValidationResult = Validations.validatePassword(password)

}
