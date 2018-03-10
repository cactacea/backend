package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.request.FormParam
import com.twitter.finatra.validation.{MethodValidation, Size}
import io.github.cactacea.backend.models.requests.Validations

case class PutSessionAccountName(
                           @Size(min = 2, max = 30) accountName: String
                    ) {

  @MethodValidation
  def accountNameCheck = Validations.validateAccountName(accountName)

}
