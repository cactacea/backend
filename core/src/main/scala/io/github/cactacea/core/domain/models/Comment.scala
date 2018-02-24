package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.CommentId

case class Comment(
                    id: CommentId,
                    message: String,
                    account: Account,
                    feed: Option[Feed],
                    favoriteCount: Long,
                    postedAt: Long)
