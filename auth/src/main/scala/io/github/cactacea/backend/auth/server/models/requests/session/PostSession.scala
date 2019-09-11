package io.github.cactacea.backend.auth.server.models.requests.session

import com.twitter.finatra.validation.Size
import io.swagger.annotations.ApiModelProperty

case class PostSession(
                        @ApiModelProperty(value = "User name.", required = true)
                        @Size(min = 2, max = 50) userName: String,

                        @ApiModelProperty(value = "Display name.", required = false)
                        @Size(min = 1, max = 50) displayName: Option[String]
                      )
