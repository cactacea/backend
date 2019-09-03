package io.github.cactacea.backend.server.models.requests.session

import com.twitter.finatra.validation.Size
import io.swagger.annotations.ApiModelProperty

case class PutSession (
                        @ApiModelProperty(value = "User name.", required = true)
                        @Size(min = 2, max = 50) userName: String,

                        @ApiModelProperty(value = "Display name.", required = false)
                        @Size(min = 1, max = 50) displayName: Option[String]
                      )
