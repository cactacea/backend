package io.github.cactacea.backend.auth.server.models.requests.sessions

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.http.Request
import io.github.cactacea.backend.auth.enums.AuthType
import io.swagger.annotations.ApiModelProperty

case class PostSignIn(
                      @ApiModelProperty(value = "Auth type.", required = true)
                      authType: AuthType,

                      @ApiModelProperty(value = "User name.", required = true)
                      identifier: String,

                      @ApiModelProperty(value = "User password.", required = true)
                      password: String,

                      @JsonIgnore
                      @ApiModelProperty(hidden = true)
                      request: Request

                    )
