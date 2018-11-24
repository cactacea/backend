package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.swagger.annotations.ApiModelProperty

case class GetSessionInvitations(
                                  @ApiModelProperty(value = "Filters invitations which started on since or later.")
                                  @QueryParam since: Option[Long],

                                  @ApiModelProperty(value = "The offset of group invitations. By default the value is 0.")
                                  @QueryParam offset: Option[Int],

                                  @ApiModelProperty(value = "Maximum number of invitations returned on one result page." +
                                    " By default the value is 20 entries. The page size can never be larger than 50.")
                                  @QueryParam @Max(50) count: Option[Int]
                    )
