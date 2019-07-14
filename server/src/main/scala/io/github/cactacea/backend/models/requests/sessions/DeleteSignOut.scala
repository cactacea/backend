package io.github.cactacea.backend.models.requests.sessions

import com.twitter.finatra.validation._
import io.swagger.annotations.ApiModelProperty

case class DeleteSignOut(
                       @ApiModelProperty(value = "Unique Device Identifier.", required = true)
                       @UUID udid: String
                     )
