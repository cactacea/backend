package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, CommentId, FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Blocks, CommentLikes, Comments, FeedLikes}
import io.github.cactacea.backend.core.infrastructure.results.{CommentBlocksCount, FeedBlocksCount, RelationshipBlocksCount}

@Singleton
class BlockCountDAO @Inject()(db: DatabaseService) {

  import db._

  def findRelationshipBlocks(accountIds: List[AccountId], sessionId: SessionId): Future[List[RelationshipBlocksCount]] = {
    if (accountIds.size == 0) {
      return Future.value(List[RelationshipBlocksCount]())
    }
    val ids = accountIds.mkString(",")
    val q = quote { infix"""
                       select
                       a.`by` id,
                       count(a.follower = true and a.account_id = b.account_id) follower_count,
                       count(a.follow  = true and a.`by` = b.account_id) follow_count,
                       count(a.friend = true and  a.`by` = b.account_id) friend_count
                       from
                       relationships a,
                       blocks b
                       where (a.account_id = b.account_id or a.`by` = b.account_id)
                       and (a.follower = true or a.follow = true or a.friend = true)
                       and a.`by` in (${lift(ids)})
                       and b.`by` = ${lift(sessionId)}
                       group by
                       a.`by`
      """.as[Query[RelationshipBlocksCount]]
    }
    run(q)
  }

  def findCommentLikeBlocks(commentIds: List[CommentId], sessionId: SessionId): Future[List[CommentBlocksCount]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[CommentLikes]
        .filter(c => liftQuery(commentIds).contains(c.commentId))
        .filter(c => query[Blocks].filter(b => b.by == lift(by) && c.by == b.accountId).nonEmpty)
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
        .filter(f => query[Blocks].filter(b => b.by == lift(by) && f.by == b.accountId).nonEmpty)
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
        .filter(c => query[Blocks].filter(b => b.by == lift(by) && c.by == b.accountId).nonEmpty)
        .groupBy(c => c.feedId).map {
        case (feedId, comments) => (feedId, comments.size)
      }.map {
        case (feedId, count) => FeedBlocksCount(feedId, count)
      }
    }
    run(q)
  }

}
