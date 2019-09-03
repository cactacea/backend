package io.github.cactacea.backend.auth.server.models.requests.session

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}
import io.github.cactacea.backend.auth.server.utils.validations.CactaceaValidations
import io.swagger.annotations.ApiModelProperty

case class PostLogin(
                      @ApiModelProperty(value = " Email", required = true)
                      email: String,

                      @ApiModelProperty(value = " Password", required = true)
                      password: String,

                      @ApiModelProperty(hidden = true)
                      request: Request
                     ) {

  @MethodValidation
  def emailCheck: ValidationResult = CactaceaValidations.validateEmail(Some(email))

}
