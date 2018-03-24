package io.github.cactacea.backend.models.requests.setting

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Size
import io.swagger.annotations.ApiModelProperty


case class PostSocialAccount(
                              @ApiModelProperty(value = "Social account type.")
                              @Size(min = 1, max = 255) socialAccountType: String,

                              @ApiModelProperty(value = "OAuth2 access token.")
                              @QueryParam @Size(min = 1, max = 1000) accessTokenKey: String,

                              @ApiModelProperty(value = "OAuth2 access token secret.")
                              @QueryParam @Size(min = 1, max = 1000) accessTokenSecret: String,
                                 )
