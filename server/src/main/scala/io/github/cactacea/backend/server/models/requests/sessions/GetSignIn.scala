package io.github.cactacea.backend.server.models.requests.sessions

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{Header, QueryParam}
import io.swagger.annotations.ApiModelProperty

case class GetSignIn(
                      @ApiModelProperty(value = "Account name.", required = true)
                      @QueryParam accountName: String,

                      @ApiModelProperty(value = "Account password.", required = true)
                      @QueryParam password: String,

                      @ApiModelProperty(value = "Unique Device Identifier.", required = true)
                      @QueryParam udid: String,

                      @ApiModelProperty(hidden = true)
                      @Header("user-agent") userAgent: Option[String],

                      @ApiModelProperty(hidden = true)
                      request: Request

                    )
