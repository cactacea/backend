package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.request.{Header, QueryParam}
import com.twitter.finatra.validation.Size
import io.swagger.annotations.ApiModelProperty

case class GetSocialAccountSignIn(
                                   @ApiModelProperty(value = "OAuth2 access token.")
                                   @QueryParam @Size(min = 1, max = 1000) accessTokenKey: String,

                                   @ApiModelProperty(value = "OAuth2 access token secret.")
                                   @QueryParam @Size(min = 1, max = 1000) accessTokenSecret: String,

                                   @ApiModelProperty(value = "Unique Device Identifier.")
                                   @QueryParam @Size(min = 1, max = 1000) udid: String,

                                   @Header("user-agent") userAgent: String
                                 )
