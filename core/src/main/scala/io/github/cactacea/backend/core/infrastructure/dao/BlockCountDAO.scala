package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Blocks, CommentLikes, Comments, FeedLikes}
import io.github.cactacea.backend.core.infrastructure.results.{CommentBlocksCount, FeedBlocksCount}

@Singleton
class BlockCountDAO @Inject()(db: DatabaseService) {

  import db._

  // TODO :
  def findCommentLikeBlocks(commentIds: List[CommentId], sessionId: SessionId): Future[List[CommentBlocksCount]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[CommentLikes]
        .filter(c => liftQuery(commentIds).contains(c.commentId))
        .filter(c =>
          query[Blocks].filter(b =>
            (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
          ).nonEmpty
        )
        .groupBy(c => c.commentId).map {
        case (commentId, comments) => (commentId, comments.size)
      }.map {
        case (commentId, count) => CommentBlocksCount(commentId, count)
      }
    }
    run(q)
  }

  def findFeedLikeBlocks(feedIds: List[FeedId], sessionId: SessionId): Future[List[FeedBlocksCount]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedLikes]
        .filter(f => liftQuery(feedIds).contains(f.feedId))
        .filter(f =>
          query[Blocks].filter(b =>
            (b.accountId == lift(by) && b.by == f.by) || (b.accountId == f.by && b.by == lift(by))
          ).nonEmpty
        )
        .groupBy(f => f.feedId).map {
        case (feedId, comments) => (feedId, comments.size)
      }.map {
        case (feedId, count) => FeedBlocksCount(feedId, count)
      }
    }
    run(q)
  }

  def findFeedCommentBlocks(feedIds: List[FeedId], sessionId: SessionId): Future[List[FeedBlocksCount]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Comments]
        .filter(c => liftQuery(feedIds).contains(c.feedId))
        .filter(c =>
          query[Blocks].filter(b =>
            (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
          ).nonEmpty
        )
        .groupBy(c => c.feedId).map {
        case (feedId, comments) => (feedId, comments.size)
      }.map {
        case (feedId, count) => FeedBlocksCount(feedId, count)
      }
    }
    run(q)
  }

}
