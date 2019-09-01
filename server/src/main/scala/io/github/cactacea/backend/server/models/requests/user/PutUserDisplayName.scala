package io.github.cactacea.backend.server.models.requests.user

import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.swagger.annotations.ApiModelProperty

case class PutUserDisplayName(
                                  @ApiModelProperty(value = "User Identifier.", required = true)
                                  @RouteParam id: UserId,

                                  @ApiModelProperty(value = "Display name that each user shown.")
                                  @Size(min = 1, max = 1000) displayName: Option[String]
                  )
