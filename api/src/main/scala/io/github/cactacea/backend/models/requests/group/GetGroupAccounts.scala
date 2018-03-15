package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Max
import io.github.cactacea.core.infrastructure.identifiers.GroupId
import io.swagger.annotations.ApiModelProperty

case class GetGroupAccounts(
                             @ApiModelProperty(value = "Group identifier.")
                             @RouteParam id: GroupId,

                             @QueryParam since: Option[Long],
                             @ApiModelProperty(value = "Filters accounts which started on since or later.")

                             @ApiModelProperty(value = "The offset of accounts. By default the value is 0.")
                             @QueryParam offset: Option[Int],

                             @ApiModelProperty(value = "Maximum number of accounts returned on one result page. By default the value is 20 entries. The page size can never be larger than 50.")
                             @QueryParam @Max(50) count: Option[Int]
                      )
