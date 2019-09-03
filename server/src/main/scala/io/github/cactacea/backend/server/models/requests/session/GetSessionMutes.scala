package io.github.cactacea.backend.server.models.requests.session

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.{Max, Size}
import io.swagger.annotations.ApiModelProperty

case class GetSessionMutes(
                            @ApiModelProperty(value = "Filters users whose user name start of.")
                            @QueryParam @Size(min = 0, max = 1000) userName: Option[String],

                            @ApiModelProperty(value = "Filters users which started on since or later.")
                            @QueryParam since: Option[Long],

                            @ApiModelProperty(value = "The offset of users. By default the value is 0.")
                            @QueryParam offset: Option[Int],

                            @ApiModelProperty(value = "Maximum number of users returned on one result page." +
                              " By default the value is 20 entries. The page size can never be larger than 50.")
                            @QueryParam @Max(50) count: Option[Int]
                   )
