package io.github.cactacea.backend.server.models.requests.message

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import io.swagger.annotations.ApiModelProperty

case class GetMessages(
                        @ApiModelProperty(value = "Channel identifier.", required = true)
                        @QueryParam id: ChannelId,

                        @ApiModelProperty(value = "Filters messages which started on since or later.")
                        @QueryParam since: Option[Long],

                        @ApiModelProperty(value = "The offset of messages. By default the value is 0.")
                        @QueryParam offset: Option[Int],

                        @ApiModelProperty(value = "Maximum number of entries returned on one result page." +
                          " By default the value is 20 entries. The page size can never be larger than 50.")
                        @QueryParam @Max(50) count: Option[Int],

                        @ApiModelProperty(value = "Order by posted time.", required = true)
                        @QueryParam ascending: Boolean
                       )
