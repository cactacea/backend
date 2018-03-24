package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, CommentId, CommentLikeId}

case class CommentLikes(
                       id: CommentLikeId,
                       commentId: CommentId,
                       by: AccountId,
                       postedAt: Long
                       )
