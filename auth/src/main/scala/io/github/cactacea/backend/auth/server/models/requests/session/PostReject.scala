package io.github.cactacea.backend.auth.server.models.requests.session

import io.swagger.annotations.ApiModelProperty

case class PostReject(
                       @ApiModelProperty(value = "Received token", required = true)
                       token: String

                     )
