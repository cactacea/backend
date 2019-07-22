package io.github.cactacea.backend.server.models.requests.feed

import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, MediumId}
import io.swagger.annotations.ApiModelProperty

case class PutFeed(
                    @ApiModelProperty(value = "Feed identifier.", required = true)
                    @RouteParam id: FeedId,

                    @ApiModelProperty(value = "A feed message will be posted.", required = true)
                    @Size(min = 1, max = 1000) message : String,

                    @ApiModelProperty(value = "Medium identifiers of attached.")
                    mediumIds : Option[Array[MediumId]],

                    @ApiModelProperty(value = "Tags of feed.")
                    @Size(min = 0, max = 1000) tags : Option[Array[String]],

                    @ApiModelProperty(value = "Group privacy type.", required = true)
                    privacyType : FeedPrivacyType,

                    @ApiModelProperty(value = "Content warning.", required = true)
                    contentWarning : Boolean,

                    @ApiModelProperty(value = "Expiration of a feed.")
                    expiration: Option[Long]
                    )
