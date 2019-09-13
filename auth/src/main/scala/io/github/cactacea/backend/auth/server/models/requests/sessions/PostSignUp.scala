package io.github.cactacea.backend.auth.server.models.requests.sessions

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.{MethodValidation, Size, ValidationResult}
import io.github.cactacea.backend.auth.enums.AuthType
import io.github.cactacea.backend.auth.server.utils.validations.ValueValidator
import io.swagger.annotations.ApiModelProperty

case class PostSignUp(
                       @ApiModelProperty(value = "Auth type.", required = true)
                       authType: AuthType,

                       @ApiModelProperty(value = "User name or email address.", required = true)
                       @Size(min = 2, max = 50) identifier: String,

                       @ApiModelProperty(value = "User password.", required = true)
                       @Size(min = 8, max = 255) password: String,

                       @JsonIgnore
                       @ApiModelProperty(hidden = true)
                       request: Request

                     ) {

  @MethodValidation
  def userNameCheck: ValidationResult = {
    authType match {
      case AuthType.username =>
        ValueValidator.validateUserName(identifier)
      case AuthType.email =>
        ValueValidator.validateEmail(identifier)
    }
  }

  @MethodValidation
  def passwordCheck: ValidationResult = ValueValidator.validatePassword(password)

}
