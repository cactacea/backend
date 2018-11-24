package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.swagger.annotations.ApiModelProperty

case class GetSessionFollows(
                                @ApiModelProperty(value = "Filters follower which started on since or later.")
                                @QueryParam since: Option[Long],

                                @ApiModelProperty(value = "The offset of follower. By default the value is 0.")
                                @QueryParam offset: Option[Int],

                                @ApiModelProperty(value = "Maximum number of follower returned on one result page." +
                                  " By default the value is 20 entries. The page size can never be larger than 50.")
                                @QueryParam @Max(50) count: Option[Int]
                              )
