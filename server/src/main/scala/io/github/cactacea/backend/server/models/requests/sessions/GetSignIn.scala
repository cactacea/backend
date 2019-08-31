package io.github.cactacea.backend.server.models.requests.sessions

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.QueryParam
import io.swagger.annotations.ApiModelProperty

case class GetSignIn(
                      @ApiModelProperty(value = "Account name.", required = true)
                      @QueryParam accountName: String,

                      @ApiModelProperty(value = "Account password.", required = true)
                      @QueryParam password: String,

                      @ApiModelProperty(hidden = true)
                      request: Request

                    )
