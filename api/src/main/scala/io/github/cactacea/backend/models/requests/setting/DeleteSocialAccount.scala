package io.github.cactacea.backend.models.requests.setting

import com.twitter.finatra.request.RouteParam
import io.swagger.annotations.ApiModelProperty


case class DeleteSocialAccount(
                                @RouteParam @ApiModelProperty(name = "social_account_type") socialAccountType: String
                              )
