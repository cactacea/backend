package io.github.cactacea.backend.auth.server.models.requests.sessions

import io.swagger.annotations.ApiModelProperty

case class PostRejectToken(
                       @ApiModelProperty(value = "Received token", required = true)
                       token: String

                     )
