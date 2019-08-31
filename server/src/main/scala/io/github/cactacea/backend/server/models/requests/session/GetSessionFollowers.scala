package io.github.cactacea.backend.server.models.requests.session

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.{Max, Size}
import io.swagger.annotations.ApiModelProperty

case class GetSessionFollowers(
                                @ApiModelProperty(value = "Filters accounts whose account name start of.")
                                @QueryParam @Size(min = 0, max = 1000) accountName: Option[String],

                                @ApiModelProperty(value = "Filters followers which started on since or later.")
                                @QueryParam since: Option[Long],

                                @ApiModelProperty(value = "The offset of followers. By default the value is 0.")
                                @QueryParam offset: Option[Int],

                                @ApiModelProperty(value = "Maximum number of followers returned on one result page." +
                                  " By default the value is 20 entries. The page size can never be larger than 50.")
                                @QueryParam @Max(50) count: Option[Int]
                              )
