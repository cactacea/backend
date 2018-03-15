package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.{QueryParam, RouteParam}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId}
import io.swagger.annotations.ApiModelProperty

case class PostInvitationAccounts(
                                   @ApiModelProperty(value = "Group Identifier.")
                                   @RouteParam groupId: GroupId,

                                   @ApiModelProperty(value = "Account Identifies.")
                                   @QueryParam accountIds: Array[AccountId]
                                 )
