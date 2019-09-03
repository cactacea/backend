package io.github.cactacea.backend.auth.server.models.requests.sessions

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.QueryParam
import io.swagger.annotations.ApiModelProperty

case class GetSignIn(
                      @ApiModelProperty(value = "User name.", required = true)
                      @QueryParam userName: String,

                      @ApiModelProperty(value = "User password.", required = true)
                      @QueryParam password: String,

                      @ApiModelProperty(hidden = true)
                      request: Request

                    )
