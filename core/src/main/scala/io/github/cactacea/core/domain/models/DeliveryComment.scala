package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId, CommentId}

case class DeliveryComment(
                            accountId: AccountId,
                            displayName: String,
                            commentId: CommentId,
                            message: String,
                            postedAt: Long,
                            tokens: List[(String, AccountId)]
                                   )
