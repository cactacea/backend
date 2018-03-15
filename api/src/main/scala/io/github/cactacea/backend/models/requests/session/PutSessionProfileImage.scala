package io.github.cactacea.backend.models.requests.session

import io.github.cactacea.core.infrastructure.identifiers.MediumId
import io.swagger.annotations.ApiModelProperty

case class PutSessionProfileImage(
                                   @ApiModelProperty(value = "Medium identifier.")
                                   id: MediumId
                                 )
