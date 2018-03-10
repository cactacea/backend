package io.github.cactacea.backend.models.requests.setting

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Size
import io.swagger.annotations.ApiModelProperty


case class PostSocialAccount(
                              @RouteParam @ApiModelProperty(name = "social_account_type") socialAccountType: String,
                              @QueryParam @Size(min = 1, max = 1000) accessTokenKey: String,
                              @QueryParam @Size(min = 1, max = 1000) accessTokenSecret: String
                                 )
