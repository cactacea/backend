package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId}
import io.swagger.annotations.ApiModelProperty

case class PostInvitationAccount(
                                  @ApiModelProperty(name = "account_id")
                                  @RouteParam accountId: AccountId,
                                  @ApiModelProperty(name = "group_id")
                                  @RouteParam groupId: GroupId
                            )
