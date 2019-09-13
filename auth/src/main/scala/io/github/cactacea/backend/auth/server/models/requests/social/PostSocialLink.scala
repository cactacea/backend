package io.github.cactacea.backend.auth.server.models.requests.social

import com.twitter.finatra.request.RouteParam
import io.swagger.annotations.ApiModelProperty

case class PostSocialLink(
                          @ApiModelProperty(value = "Provider type.", required = true)
                          @RouteParam provider: String,

                          @ApiModelProperty(value = "token.", required = true)
                          token: String,

                          @ApiModelProperty(value = "expiresIn.", required = true)
                          expiresIn: Option[Int],

                          @ApiModelProperty(value = "secret.", required = true)
                          secret: Option[String])