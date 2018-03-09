package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{CommentId, AccountId}

case class CommentLikes(
                             commentId: CommentId,
                             by: AccountId,
                             postedAt: Long
                           )
