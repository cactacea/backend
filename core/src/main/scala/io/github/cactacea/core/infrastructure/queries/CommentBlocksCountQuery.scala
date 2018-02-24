package io.github.cactacea.core.infrastructure.queries

import io.github.cactacea.core.infrastructure.identifiers.CommentId

case class CommentBlocksCountQuery(
                               id: CommentId,
                               count: Long
                             )