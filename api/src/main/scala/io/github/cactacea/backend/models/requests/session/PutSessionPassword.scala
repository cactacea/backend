package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.validation.{MethodValidation, Size}
import io.github.cactacea.backend.models.requests.Validations

case class PutSessionPassword(
                               @Size(min = 8, max = 255) oldPassword: String,
                               @Size(min = 8, max = 255) newPassword: String
                             ) {

  @MethodValidation
  def oldPasswordCheck = Validations.validatePassword(oldPassword)

  @MethodValidation
  def newPasswordCheck = Validations.validatePassword(newPassword)

}
