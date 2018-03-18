package io.github.cactacea.backend.models.requests.message

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.github.cactacea.core.infrastructure.identifiers.GroupId
import io.swagger.annotations.ApiModelProperty

case class GetMessages(
                        @ApiModelProperty(value = "Group identifier.")
                        @QueryParam id: GroupId,

                        @ApiModelProperty(value = "Filters messages which started on since or later.")
                        @QueryParam since: Option[Long],

                        @ApiModelProperty(value = "The offset of messages. By default the value is 0.")
                        @QueryParam offset: Option[Int],

                        @ApiModelProperty(value = "Maximum number of entries returned on one result page. By default the value is 20 entries. The page size can never be larger than 50.")
                        @QueryParam @Max(50) count: Option[Int],

                        @QueryParam ascending: Boolean
                       )
