package io.github.cactacea.core.application.requests.comment

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class PostComment(
                        feedId: FeedId,
                        @Size(min = 1, max = 1000) commentMessage: String,
                        session: Request
                      )
