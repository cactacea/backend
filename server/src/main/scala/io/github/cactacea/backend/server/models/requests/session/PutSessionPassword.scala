package io.github.cactacea.backend.server.models.requests.session

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.{MethodValidation, Size, ValidationResult}
import io.github.cactacea.backend.server.utils.validators.CactaceaValidations
import io.swagger.annotations.ApiModelProperty

case class PutSessionPassword(
                               @ApiModelProperty(value = "User new password.", required = true)
                               @Size(min = 8, max = 255) password: String,

                               @ApiModelProperty(hidden = true)
                               request: Request

                             ) {

  @MethodValidation
  def passwordCheck: ValidationResult = CactaceaValidations.validatePassword(password)

}
