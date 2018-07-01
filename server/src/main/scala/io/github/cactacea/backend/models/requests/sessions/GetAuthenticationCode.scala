package io.github.cactacea.backend.models.requests.sessions

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Size
import io.swagger.annotations.ApiModelProperty

case class GetAuthenticationCode(
                                         @ApiModelProperty(value = "Social account type.")
                                         @Size(min = 1, max = 255) providerId: String,

                                         @ApiModelProperty(value = "OAuth2 access token or phone number or email address.")
                                         @QueryParam @Size(min = 1, max = 1000) providerKey: String

                                       )
