package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.{Max, Size}
import io.github.cactacea.backend.core.domain.enums.GroupPrivacyType
import io.swagger.annotations.ApiModelProperty

case class GetGroups(
                      @ApiModelProperty(value = "Filters groups which group name start of.")
                      @QueryParam @Size(min = 0, max = 1000) groupName: Option[String],

                      @ApiModelProperty(value = "Filters groups that invited accounts can join in.")
                      @QueryParam invitationOnly: Option[Boolean],

                      @ApiModelProperty(value = "Filters groups which can join in.")
                      @QueryParam privacyType: Option[GroupPrivacyType],

                      @ApiModelProperty(value = "Filters groups which started on since or later.")
                      @QueryParam since: Option[Long],

                      @ApiModelProperty(value = "The offset of messages. By default the value is 0.")
                      @QueryParam offset: Option[Int],

                      @ApiModelProperty(value = "Maximum number of groups returned on one result page. By default the value is 20 entries. The page size can never be larger than 50.")
                      @QueryParam @Max(50) count: Option[Int]
                     )
