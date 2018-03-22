package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.validation.{MethodValidation, Size}
import io.github.cactacea.backend.util.validaters.Validations
import io.swagger.annotations.ApiModelProperty

case class PutSessionAccountName(
                                  @ApiModelProperty(value = "Account name.")
                                  @Size(min = 2, max = 30) name: String
                                ) {

  @MethodValidation
  def accountNameCheck = Validations.validateAccountName(name)

}
