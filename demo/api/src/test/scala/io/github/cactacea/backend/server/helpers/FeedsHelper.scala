package io.github.cactacea.backend.server.helpers

import com.twitter.finagle.http.Status
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.github.cactacea.backend.server.APIServerSpec
import io.github.cactacea.backend.server.models.requests.feed.PostFeed
import io.github.cactacea.backend.server.models.responses.FeedCreated

trait FeedsHelper extends CommonHelper {
  self: APIServerSpec =>

  def postFeed(message: String, mediumIds: Option[Array[MediumId]],
               tags: Option[Array[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, accessToken: String): FeedCreated = {
    val request = PostFeed(message, mediumIds, tags, privacyType, contentWarning, None)
    val body = mapper.writePrettyString(request)
    val path = s"/feeds"
    server.httpPostJson[FeedCreated](
      path = path,
      headers = headers(accessToken),
      postBody = body,
      andExpect = Status.Created
    )
  }

}
