package io.github.cactacea.backend.server.models.requests.tweet

import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.swagger.annotations.ApiModelProperty

case class PostTweet(
                     @ApiModelProperty(value = "A tweet message will be posted.", required = true)
                     @Size(min = 1, max = 1000) message : String,

                     @ApiModelProperty(value = "Medium identifiers of attached.")
                     mediumIds : Option[Seq[MediumId]],

                     @ApiModelProperty(value = "Tags of tweet.")
                     @Size(min = 0, max = 1000) tags : Option[Seq[String]],

                     @ApiModelProperty(value = "Tweet privacy type.", required = true)
                     privacyType : TweetPrivacyType,

                     @ApiModelProperty(value = "Content warning.", required = true)
                     contentWarning : Boolean,

                     @ApiModelProperty(value = "Expiration of a tweet.")
                     expiration: Option[Long]
                       )
