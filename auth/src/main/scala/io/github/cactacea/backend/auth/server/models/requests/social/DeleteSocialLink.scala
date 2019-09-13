package io.github.cactacea.backend.auth.server.models.requests.social

import com.twitter.finatra.request.RouteParam
import io.swagger.annotations.ApiModelProperty

case class DeleteSocialLink(
                          @ApiModelProperty(value = "Provider type.", required = true)
                          @RouteParam provider: String)