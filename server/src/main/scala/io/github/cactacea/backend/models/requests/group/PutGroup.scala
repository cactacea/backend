package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.swagger.annotations.ApiModelProperty

case class PutGroup (
                      @ApiModelProperty(value = "Group identifier.", required = true)
                      @RouteParam id: GroupId,

                      @ApiModelProperty(value = "Group name.", required = true)
                      @Size(min = 1, max = 1000) name: String,

                      @ApiModelProperty(value = "Only invited accounts can join in.", required = true)
                      byInvitationOnly: Boolean,

                      @ApiModelProperty(value = "Which accounts can join in.", required = true)
                      privacyType: GroupPrivacyType,

                      @ApiModelProperty(value = "Which accounts can manage a group.", required = true)
                      authorityType: GroupAuthorityType
                     )
