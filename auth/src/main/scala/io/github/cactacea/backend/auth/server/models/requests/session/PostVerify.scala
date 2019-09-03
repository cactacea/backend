package io.github.cactacea.backend.auth.server.models.requests.session

import io.swagger.annotations.ApiModelProperty

case class PostVerify(
                       @ApiModelProperty(value = "Received token", required = true)
                       token: String

                     )
