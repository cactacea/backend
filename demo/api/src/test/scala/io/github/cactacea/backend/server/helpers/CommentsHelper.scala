package io.github.cactacea.backend.server.helpers

import com.twitter.finagle.http.Status
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.github.cactacea.backend.server.APIServerSpec
import io.github.cactacea.backend.server.models.requests.comment.PostComment
import io.github.cactacea.backend.server.models.responses.CommentCreated

trait CommentsHelper extends CommonHelper {
  self: APIServerSpec =>

  def postComment(id: FeedId, message: String, accessToken: String): CommentCreated = {
    val request = PostComment(id, message)
    val body = mapper.writePrettyString(request)
    val path = s"/comments"
    server.httpPostJson[CommentCreated](
      path = path,
      headers = headers(accessToken),
      postBody = body,
      andExpect = Status.Created
    )
  }

}
