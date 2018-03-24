package io.github.cactacea.backend.core.infrastructure.results

import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId

case class CommentBlocksCount(
                               id: CommentId,
                               count: Long
                             )