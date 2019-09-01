package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, CommentId, CommentLikeId}

case class CommentLikes(
                         id: CommentLikeId,
                         commentId: CommentId,
                         by: UserId,
                         likedAt: Long
                       )
