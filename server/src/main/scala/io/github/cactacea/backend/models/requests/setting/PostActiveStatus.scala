package io.github.cactacea.backend.models.requests.setting

import com.twitter.finatra.validation.UUID
import io.github.cactacea.backend.core.domain.enums.ActiveStatusType
import io.swagger.annotations.ApiModelProperty

case class PostActiveStatus(
                             @ApiModelProperty(value = "Device status.", required = true)
                             status: ActiveStatusType,

                             @ApiModelProperty(value = "Unique Device Identifier.", required = true)
                             @UUID udid: String,

                           )
