package io.github.cactacea.backend.server.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId}
import io.swagger.annotations.ApiModelProperty

case class DeleteInvitation(
                                  @ApiModelProperty(value = "Group Identifier.", required = true)
                                  @RouteParam groupId: GroupId,

                                  @ApiModelProperty(value = "Account Identifier.", required = true)
                                  @RouteParam accountId: AccountId

                            )