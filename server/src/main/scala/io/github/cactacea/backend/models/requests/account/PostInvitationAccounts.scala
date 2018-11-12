package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId}
import io.swagger.annotations.ApiModelProperty

case class PostInvitationAccounts(
                                   @ApiModelProperty(value = "Group Identifier.", required = true)
                                   @RouteParam id: GroupId,

                                   @ApiModelProperty(value = "Account Identifies.")
                                   accountIds: Array[AccountId]
                                 )
