package io.github.cactacea.backend.auth.server.models.requests.session

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.{MethodValidation, Size, ValidationResult}
import io.github.cactacea.backend.auth.server.utils.validations.CactaceaValidations
import io.swagger.annotations.ApiModelProperty

case class PutPassword(

                               @ApiModelProperty(value = "User new password.", required = true)
                               @Size(min = 8, max = 255) newPassword: String,

                               @JsonIgnore
                               @ApiModelProperty(hidden = true)
                               request: Request
                              ) {

  @MethodValidation
  def passwordCheck: ValidationResult = CactaceaValidations.validatePassword(newPassword)

}
