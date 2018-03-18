package io.github.cactacea.backend.models.requests.setting

import com.twitter.finatra.validation.Size
import io.swagger.annotations.ApiModelProperty

case class PostDevicePushToken(
                            @ApiModelProperty(value = "Push notification token.")
                            @Size(min = 1, max = 1000) pushToken: Option[String]
                          )
