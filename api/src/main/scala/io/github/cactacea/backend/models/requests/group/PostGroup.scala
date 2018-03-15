package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.validation.Size
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.swagger.annotations.ApiModelProperty

case class PostGroup (
                       @ApiModelProperty(value = "Group name.")
                       @Size(min = 1, max = 1000) name: String,

                       @ApiModelProperty(value = "Only invited accounts can join in.")
                       byInvitationOnly: Boolean,

                       @ApiModelProperty(value = "Which accounts can join in.")
                       privacyType: GroupPrivacyType,

                       @ApiModelProperty(value = "Which accounts can manage this group.")
                       authorityType: GroupAuthorityType
                     )
