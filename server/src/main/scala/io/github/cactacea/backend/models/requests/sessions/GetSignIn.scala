package io.github.cactacea.backend.models.requests.sessions

import com.twitter.finatra.request.{Header, QueryParam}
import io.swagger.annotations.ApiModelProperty

case class GetSignIn(
                      @ApiModelProperty(value = "Account name.")
                      @QueryParam name: String,

                      @ApiModelProperty(value = "Account password.")
                      @QueryParam password: String,

                      @ApiModelProperty(value = "Unique Device Identifier.")
                      @QueryParam udid: String,

                      @Header("user-agent") userAgent: Option[String]

                    )
