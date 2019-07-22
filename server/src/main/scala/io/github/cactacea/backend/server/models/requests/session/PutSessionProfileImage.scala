package io.github.cactacea.backend.server.models.requests.session

import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.swagger.annotations.ApiModelProperty

case class PutSessionProfileImage(
                                   @ApiModelProperty(value = "Medium identifier.", required = true)
                                   id: MediumId
                                 )
