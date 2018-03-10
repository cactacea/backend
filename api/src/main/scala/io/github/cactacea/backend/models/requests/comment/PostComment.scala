package io.github.cactacea.backend.models.requests.comment

import com.twitter.finatra.validation.Size
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class PostComment(
                        id: FeedId,
                        @Size(min = 1, max = 1000) commentMessage: String
                      )
