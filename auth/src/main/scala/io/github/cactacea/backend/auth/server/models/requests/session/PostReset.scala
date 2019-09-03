package io.github.cactacea.backend.auth.server.models.requests.session

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}
import io.github.cactacea.backend.auth.server.utils.validations.CactaceaValidations
import io.swagger.annotations.ApiModelProperty

case class PostReset(
                      @ApiModelProperty(value = "Received token", required = true)
                      token: String,

                      @ApiModelProperty(value = " New password", required = true)
                      newPassword: String,

                      @ApiModelProperty(hidden = true)
                      request: Request
                      ) {

  @MethodValidation
  def passwordCheck: ValidationResult = CactaceaValidations.validatePassword(newPassword)

}
