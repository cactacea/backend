package io.github.cactacea.backend.models.requests.sessions

import com.twitter.finatra.request.{Header, QueryParam}
import io.swagger.annotations.ApiModelProperty

case class GetSignIn(
                      @ApiModelProperty(value = "Account name.", required = true)
                      @QueryParam name: String,

                      @ApiModelProperty(value = "Account password.", required = true)
                      @QueryParam password: String,

                      @ApiModelProperty(value = "Unique Device Identifier.", required = true)
                      @QueryParam udid: String,

                      @Header("user-agent") userAgent: Option[String]

                    )
