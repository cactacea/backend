package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId, CommentId, CommentLikeId}

case class CommentLikes(
                       id: CommentLikeId,
                       commentId: CommentId,
                       by: AccountId,
                       postedAt: Long
                       )
