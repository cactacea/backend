package io.github.cactacea.backend.server.models.requests.notification

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.swagger.annotations.ApiModelProperty

case class GetNotifications(
                             @ApiModelProperty(value = "Filters notifications which started on since or later.")
                             @QueryParam since: Option[Long],

                             @ApiModelProperty(value = "The offset of notifications. By default the value is 0.")
                             @QueryParam offset: Option[Int],

                             @ApiModelProperty(value = "Maximum number of notifications returned on one result page." +
                               " By default the value is 20 entries. The page size can never be larger than 50.")
                             @QueryParam @Max(50) count: Option[Int],

                             @ApiModelProperty(hidden = true)
                             request: Request

                           )
