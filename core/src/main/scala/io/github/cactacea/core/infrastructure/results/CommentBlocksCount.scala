package io.github.cactacea.core.infrastructure.results

import io.github.cactacea.core.infrastructure.identifiers.CommentId

case class CommentBlocksCount(
                               id: CommentId,
                               count: Long
                             )