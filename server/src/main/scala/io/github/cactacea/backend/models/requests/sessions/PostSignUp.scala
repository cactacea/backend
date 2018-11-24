package io.github.cactacea.backend.models.requests.sessions

import com.twitter.finatra.request.Header
import com.twitter.finatra.validation._
import io.github.cactacea.backend.utils.validaters.Validations
import io.swagger.annotations.ApiModelProperty

case class PostSignUp(
                       @ApiModelProperty(value = "Account name.", required = true)
                       @Size(min = 2, max = 30) accountName: String,

                       @ApiModelProperty(value = "Display name.")
                       @Size(min = 1, max = 50) displayName: Option[String],

                       @ApiModelProperty(value = "Account password.", required = true)
                       @Size(min = 8, max = 255) password: String,

                       @ApiModelProperty(value = "Unique Device Identifier.", required = true)
                       @UUID udid: String,

                       @ApiModelProperty(value = "Profile URL.")
                       @Size(min = 0, max = 2038) web: Option[String],

                       @ApiModelProperty(value = "Account birthday.")
                       birthday: Option[Long],

                       @ApiModelProperty(value = "Account address.")
                       @Size(min = 0, max = 255) location: Option[String],

                       @ApiModelProperty(value = "Account bio.")
                       @Size(min = 0, max = 1024) bio: Option[String],

                       @ApiModelProperty(hidden = true)
                       @Header("user-agent") userAgent: Option[String]

                  ) {

  @MethodValidation
  def accountNameCheck: ValidationResult = Validations.validateAccountName(accountName)

  @MethodValidation
  def passwordCheck: ValidationResult = Validations.validatePassword(password)

}
