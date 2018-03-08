package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.request.{Header, RouteParam}
import com.twitter.finatra.validation._
import io.github.cactacea.backend.models.requests.Validations

case class PostSocialAccountSignUp(
                               @RouteParam socialAccountType: String,
                               @Size(min = 2, max = 30) accountName: String,
                               @Size(min = 1, max = 50) displayName: String,
                               @Size(min = 8, max = 255) password: String,
                               @Size(min = 1, max = 1024) accessTokenKey: String,
                               @Size(min = 1, max = 1024) accessTokenSecret: String,
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
