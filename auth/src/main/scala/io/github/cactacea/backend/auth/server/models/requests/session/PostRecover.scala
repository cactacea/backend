package io.github.cactacea.backend.auth.server.models.requests.session

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}
import io.github.cactacea.backend.auth.server.utils.validations.CactaceaValidations
import io.swagger.annotations.ApiModelProperty

case class PostRecover(
                        @ApiModelProperty(value = " Email", required = true)
                        email: String,

                        @JsonIgnore
                        @ApiModelProperty(hidden = true)
                        request: Request
                      ) {

  @MethodValidation
  def emailValidation: ValidationResult = CactaceaValidations.validateEmail(email)

}
