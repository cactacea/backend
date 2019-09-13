package io.github.cactacea.backend.auth.server.models.requests.password

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}
import io.github.cactacea.backend.auth.server.utils.validations.ValueValidator
import io.swagger.annotations.ApiModelProperty

case class PostResetPassword(
                      @ApiModelProperty(value = "Received token", required = true)
                      token: String,

                      @ApiModelProperty(value = " New password", required = true)
                      newPassword: String,

                      @JsonIgnore
                      @ApiModelProperty(hidden = true)
                      request: Request
                      ) {

  @MethodValidation
  def passwordCheck: ValidationResult = ValueValidator.validatePassword(newPassword)

}
