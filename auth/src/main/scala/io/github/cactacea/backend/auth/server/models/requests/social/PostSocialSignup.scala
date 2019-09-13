package io.github.cactacea.backend.auth.server.models.requests.social

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.swagger.annotations.ApiModelProperty

case class PostSocialSignup(
                                     @ApiModelProperty(value = "Provider type.", required = true)
                                     @RouteParam provider: String,

                                     @ApiModelProperty(value = "token.", required = true)
                                     token: String,

                                     @ApiModelProperty(value = "expiresIn.", required = true)
                                     expiresIn: Option[Int],

                                     @ApiModelProperty(value = "secret.", required = false)
                                     secret: Option[String],

                                     @JsonIgnore
                                     @ApiModelProperty(hidden = true)
                                     request: Request

                                   )