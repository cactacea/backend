package io.github.cactacea.backend.auth.server.models.requests.sessions

import com.twitter.finagle.http.Request
import io.swagger.annotations.ApiModelProperty

case class GetSignIn(
                      @ApiModelProperty(value = "User name.", required = true)
                      userName: String,

                      @ApiModelProperty(value = "User password.", required = true)
                      password: String,

                      @ApiModelProperty(hidden = true)
                      request: Request

                    )
