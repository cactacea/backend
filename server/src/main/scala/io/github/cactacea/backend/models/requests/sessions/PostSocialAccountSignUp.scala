package io.github.cactacea.backend.models.requests.sessions

import com.twitter.finatra.request.{Header, QueryParam}
import com.twitter.finatra.validation._
import io.github.cactacea.backend.util.validaters.Validations
import io.swagger.annotations.ApiModelProperty

case class PostSocialAccountSignUp(
                                    @ApiModelProperty(value = "Account name.")
                                    @Size(min = 2, max = 30) name: String,

                                    @ApiModelProperty(value = "Display name.")
                                    @Size(min = 1, max = 50) displayName: Option[String],

                                    @ApiModelProperty(value = "Social account type.")
                                    @Size(min = 1, max = 255) socialAccountType: String,

                                    @ApiModelProperty(value = "Account password.")
                                    @Size(min = 8, max = 255) password: String,

                                    @ApiModelProperty(value = "OAuth2 access token or phone number or email address.")
                                    @QueryParam @Size(min = 1, max = 1000) socialAccountIdentifier: String,

                                    @ApiModelProperty(value = "OAuth2 access token secret or issued code.")
                                    @QueryParam @Size(min = 1, max = 1000) authenticationCode: String,

                                    @ApiModelProperty(value = "Unique Device Identifier.")
                                    @QueryParam @Size(min = 1, max = 1000) udid: String,

                                    @ApiModelProperty(value = "Profile URL.")
                                    @Size(min = 0, max = 2038) web: Option[String],

                                    @ApiModelProperty(value = "Account birthday.")
                                    birthday: Option[Long],

                                    @ApiModelProperty(value = "Account address.")
                                    @Size(min = 0, max = 255) location: Option[String],

                                    @ApiModelProperty(value = "Account bio.")
                                    @Size(min = 0, max = 1024) bio: Option[String],

                                    @Header("user-agent") userAgent: String
                                  ) {

  @MethodValidation
  def accountNameCheck = Validations.validateAccountName(name)

  @MethodValidation
  def passwordCheck = Validations.validatePassword(password)

}