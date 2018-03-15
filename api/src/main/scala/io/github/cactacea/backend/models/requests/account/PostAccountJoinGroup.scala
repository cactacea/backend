package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId}
import io.swagger.annotations.ApiModelProperty

case class PostAccountJoinGroup(
                                 @ApiModelProperty(value = "Account Identifier.")
                                 @RouteParam accountId: AccountId,

                                 @ApiModelProperty(value = "Group Identifier.")
                                 @RouteParam groupId: GroupId
                         )
