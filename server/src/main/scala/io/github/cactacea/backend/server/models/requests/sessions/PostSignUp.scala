package io.github.cactacea.backend.server.models.requests.sessions

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation._
import io.github.cactacea.backend.server.utils.validators.CactaceaValidations
import io.swagger.annotations.ApiModelProperty

case class PostSignUp(
                       @ApiModelProperty(value = "Account name.", required = true)
                       @Size(min = 2, max = 30) accountName: String,

                       @ApiModelProperty(value = "Account password.", required = true)
                       @Size(min = 8, max = 255) password: String,

                       @ApiModelProperty(hidden = true)
                       request: Request

                     ) {

  @MethodValidation
  def accountNameCheck: ValidationResult = CactaceaValidations.validateAccountName(accountName)

  @MethodValidation
  def passwordCheck: ValidationResult = CactaceaValidations.validatePassword(password)

}
