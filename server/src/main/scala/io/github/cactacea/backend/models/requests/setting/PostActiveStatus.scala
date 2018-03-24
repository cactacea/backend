package io.github.cactacea.backend.models.requests.setting

import io.github.cactacea.backend.core.domain.enums.ActiveStatus
import io.swagger.annotations.ApiModelProperty

case class PostActiveStatus(
                            @ApiModelProperty(value = "Device status.")
                            status: ActiveStatus
                          )
