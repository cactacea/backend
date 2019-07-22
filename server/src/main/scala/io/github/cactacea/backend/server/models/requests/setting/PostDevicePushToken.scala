package io.github.cactacea.backend.server.models.requests.setting

import com.twitter.finatra.validation.{Size, UUID}
import io.swagger.annotations.ApiModelProperty

case class PostDevicePushToken(
                            @ApiModelProperty(value = "Push notification token.", required = false)
                            @Size(min = 1, max = 1000) pushToken: Option[String],

                            @ApiModelProperty(value = "Unique Device Identifier.", required = true)
                            @UUID udid: String,

                          )
