package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, CommentId, FeedId, SessionId}
import io.github.cactacea.core.infrastructure.results.{CommentBlocksCount, FeedBlocksCount, RelationshipBlocksCount}

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
                       count(a.followed = true and a.`by` = b.account_id) follow_count,
                       count(a.friend = true and  a.`by` = b.account_id) friend_count
                       from
                       relationships a,
                       blocks b
                       where (a.account_id = b.account_id or a.`by` = b.account_id)
                       and (a.follower = true or a.followed = true or a.friend = true)
                       and a.`by` in (${lift(ids)})
                       and b.`by` = ${lift(sessionId)}
                       group by
                       id
      """.as[Query[RelationshipBlocksCount]]
    }
    run(q)
  }

  def findCommentFavoriteBlocks(commentIds: List[CommentId], sessionId: SessionId): Future[List[CommentBlocksCount]] = {
    if (commentIds.size == 0) {
      return Future.value(List[CommentBlocksCount]())
    }
    val ids = commentIds.mkString(",")
    val q = quote { infix"""
                       select comment_id id,
                       count(*) count
                       from comment_favorites a
                       where comment_id in (${lift(ids)})
                       and exists ( select * from blocks b where `by` = ${lift(sessionId)} and a.`by` = b.account_id )
                       group by id
      """.as[Query[CommentBlocksCount]]
    }
    run(q)
  }

  def findFeedFavoriteBlocks(feedIds: List[FeedId], sessionId: SessionId): Future[List[FeedBlocksCount]] = {
    if (feedIds.size == 0) {
      return Future.value(List[FeedBlocksCount]())
    }
    val ids = feedIds.mkString(",")
    val q = quote { infix"""
                       select feed_id id,
                       count(*) count
                       from feed_favorites a
                       where feed_id in (${lift(ids)})
                       and exists ( select * from blocks b where `by` = ${lift(sessionId)} and a.`by` = b.account_id )
                       group by id
      """.as[Query[FeedBlocksCount]]
    }
    run(q)
  }

  def findFeedCommentBlocks(feedIds: List[FeedId], sessionId: SessionId): Future[List[FeedBlocksCount]] = {
    if (feedIds.size == 0) {
      return Future.value(List[FeedBlocksCount]())
    }
    val ids = feedIds.mkString(",")
    val q = quote { infix"""
                       select feed_id id,
                       count(*) count
                       from comments a
                       where feed_id in (${lift(ids)})
                       and exists ( select * from blocks b where `by` = ${lift(sessionId)} and a.`by` = b.account_id )
                       group by id
      """.as[Query[FeedBlocksCount]]
    }
    run(q)
  }

}
