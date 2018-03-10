package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.request.Header
import com.twitter.finatra.validation._
import io.github.cactacea.backend.models.requests.Validations
import io.swagger.annotations.ApiParam

case class PostSignUp(
                   @ApiParam(value = "unique name in Cactacea that need to be signed up", required = true)
                   @Size(min = 2, max = 30)
                   accountName: String,

                   @Size(min = 1, max = 50) displayName: String,
                   @Size(min = 8, max = 255) password: String,
                   @UUID udid: String,
                   @Size(min = 0, max = 2038) web: Option[String],
                   birthday: Option[Long],
                   @Size(min = 0, max = 255) location: Option[String],
                   @Size(min = 0, max = 1024) bio: Option[String],
                   @Header("user-agent") userAgent: String
                  ) {

  @MethodValidation
  def accountNameCheck = Validations.validateAccountName(accountName)

  @MethodValidation
  def passwordCheck = Validations.validatePassword(password)

}
