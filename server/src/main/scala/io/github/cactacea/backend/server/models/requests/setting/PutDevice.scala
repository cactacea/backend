package io.github.cactacea.backend.server.models.requests.setting

import com.twitter.finatra.request.Header
import com.twitter.finatra.validation.{Size, UUID}
import io.github.cactacea.backend.core.domain.enums.ActiveStatusType
import io.swagger.annotations.ApiModelProperty

case class PutDevice(
                             @ApiModelProperty(value = "Device status.", required = true)
                             status: ActiveStatusType,

                             @ApiModelProperty(value = "Unique Device Identifier.", required = true)
                             @UUID udid: String,

                             @ApiModelProperty(value = "Push notification token.", required = false)
                             @Size(min = 1, max = 1000) pushToken: Option[String],

                             @ApiModelProperty(hidden = true)
                             @Header("user-agent") userAgent: Option[String]

                           )
