package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{CommentId, AccountId}

case class CommentFavorites(
                             commentId: CommentId,
                             by: AccountId,
                             postedAt: Long
                           )
