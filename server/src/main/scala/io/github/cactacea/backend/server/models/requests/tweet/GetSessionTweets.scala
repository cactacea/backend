package io.github.cactacea.backend.server.models.requests.tweet

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.github.cactacea.backend.core.domain.enums.{TweetPrivacyType, TweetType}
import io.swagger.annotations.ApiModelProperty

case class GetSessionTweets(
                            @ApiModelProperty(value = "Filters tweets which started on since or later.")
                     @QueryParam since: Option[Long],

                            @ApiModelProperty(value = "The offset of tweets. By default the value is 0.")
                     @QueryParam offset: Option[Int],

                            @ApiModelProperty(value = "Tweet privacy type.")
                     @QueryParam tweetPrivacyType : Option[TweetPrivacyType],

                            @ApiModelProperty(value = "Posted tweets or received tweets. By default the value is received.")
                     @QueryParam tweetType: Option[TweetType],

                            @ApiModelProperty(value = "Maximum number of tweets returned on one result page." +
                       " By default the value is 20 entries. The page size can never be larger than 50.")
                     @QueryParam @Max(50) count: Option[Int]
                      )
