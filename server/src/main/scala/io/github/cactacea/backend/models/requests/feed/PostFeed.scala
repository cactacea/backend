package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.swagger.annotations.ApiModelProperty

case class PostFeed(
                     @ApiModelProperty(value = "A feed message will be posted.", required = true)
                     @Size(min = 1, max = 1000) message : String,

                     @ApiModelProperty(value = "Medium identifiers of attached.")
                     mediumIds : Option[Array[MediumId]],

                     @ApiModelProperty(value = "Tags of feed.")
                     @Size(min = 0, max = 1000) tags : Option[Array[String]],

                     @ApiModelProperty(value = "Feed privacy type.", required = true)
                     privacyType : FeedPrivacyType,

                     @ApiModelProperty(value = "Content warning.", required = true)
                     contentWarning : Boolean,

                     @ApiModelProperty(value = "Expiration of a feed.")
                     expiration: Option[Long]
                       )