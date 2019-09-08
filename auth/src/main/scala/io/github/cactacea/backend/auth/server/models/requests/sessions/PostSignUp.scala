package io.github.cactacea.backend.auth.server.models.requests.sessions

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.http.Request
import com.twitter.finatra.validation._
import io.github.cactacea.backend.auth.server.utils.validations.CactaceaValidations
import io.swagger.annotations.ApiModelProperty

case class PostSignUp(
                       @ApiModelProperty(value = "User name.", required = true)
                       @Size(min = 2, max = 50) userName: String,

                       @ApiModelProperty(value = "User password.", required = true)
                       @Size(min = 8, max = 255) password: String,

                       @JsonIgnore
                       @ApiModelProperty(hidden = true)
                       request: Request

                     ) {

  @MethodValidation
  def userNameCheck: ValidationResult = CactaceaValidations.validateUserName(userName)

  @MethodValidation
  def passwordCheck: ValidationResult = CactaceaValidations.validatePassword(password)

}
