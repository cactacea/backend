package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, CommentLikeId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class CommentLikesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    for {
      id <- _insertCommentLikes(commentId, sessionId)
      r <- _updateLikeCount(commentId)
    } yield (r)
  }

  private def _insertCommentLikes(commentId: CommentId, sessionId: SessionId): Future[CommentLikeId] = {
    val by = sessionId.toAccountId
    val postedAt = timeService.currentTimeMillis()
    val q = quote {
      query[CommentLikes]
        .insert(
          _.commentId   -> lift(commentId),
          _.by          -> lift(by),
          _.postedAt    -> lift(postedAt)
        ).returning(_.id)
    }
    run(q)
  }

  private def _updateLikeCount(commentId: CommentId): Future[Boolean] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(
          a => a.likeCount -> (a.likeCount + 1)
        )
    }
    run(q).map(_ == 1)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _deleteCommentLikes(commentId, sessionId)
      r <- _updateUnlikeCount(commentId)
    } yield (r)
  }

  private def _deleteCommentLikes(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[CommentLikes]
        .filter(_.by        == lift(by))
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q).map(_ == 1)
  }

  private def _updateUnlikeCount(commentId: CommentId): Future[Boolean] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(
          a => a.likeCount -> (a.likeCount - 1)
        )
    }
    run(q).map(_ == 1)
  }

  def exist(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[CommentLikes]
        .filter(_.commentId == lift(commentId))
        .filter(_.by      == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def findAll(commentId: CommentId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], CommentLikes)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[CommentLikes].filter(cf => cf.commentId == lift(commentId) && (cf.id < lift(s) || lift(s) == -1L) &&
          query[Blocks].filter(b => b.accountId == cf.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty)
        .join(query[Accounts]).on((cf, a) => a.id == cf.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((c, a), r) => (a, r, c)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }

    run(q)

  }

}
