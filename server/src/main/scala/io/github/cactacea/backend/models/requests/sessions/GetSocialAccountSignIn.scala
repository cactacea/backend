package io.github.cactacea.backend.models.requests.sessions

import com.twitter.finatra.request.{Header, QueryParam}
import com.twitter.finatra.validation.Size
import io.swagger.annotations.ApiModelProperty

case class GetSocialAccountSignIn(
                                   @ApiModelProperty(value = "Social account type.")
                                   @Size(min = 1, max = 255) socialAccountType: String,

                                   @ApiModelProperty(value = "OAuth2 access token or phone number or email address.")
                                   @QueryParam @Size(min = 1, max = 1000) socialAccountIdentifier: String,

                                   @ApiModelProperty(value = "OAuth2 access token secret or issued code.")
                                   @QueryParam @Size(min = 1, max = 1000) authenticationCode: String,

                                   @ApiModelProperty(value = "Unique Device Identifier.")
                                   @QueryParam @Size(min = 1, max = 1000) udid: String,

                                   @Header("user-agent") userAgent: String
                                 )
