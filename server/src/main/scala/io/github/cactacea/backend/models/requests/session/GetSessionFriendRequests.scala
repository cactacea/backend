package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.swagger.annotations.ApiModelProperty

case class GetSessionFriendRequests(
                                     @ApiModelProperty(value = "Filters friend requests which started on since or later.")
                                     @QueryParam since: Option[Long],

                                     @ApiModelProperty(value = "The offset of friend request. By default the value is 0.")
                                     @QueryParam offset: Option[Int],

                                     @ApiModelProperty(value = "Maximum number of friend request returned on one result page. By default the value is 20 entries. The page size can never be larger than 50.")
                                     @QueryParam @Max(50) count: Option[Int],

                                     @ApiModelProperty(value = "Filters friend requests which you have received or sent.", required = true)
                                     @QueryParam received: Boolean
                            )
