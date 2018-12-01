package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, CommentLikeId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class CommentLikesDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertCommentLikes(commentId, sessionId)
      r <- updateLikeCount(commentId)
    } yield (r)
  }

  private def insertCommentLikes(commentId: CommentId, sessionId: SessionId): Future[CommentLikeId] = {
    val by = sessionId.toAccountId
    val likedAt = timeService.currentTimeMillis()
    val q = quote {
      query[CommentLikes]
        .insert(
          _.commentId   -> lift(commentId),
          _.by          -> lift(by),
          _.likedAt    -> lift(likedAt)
        ).returning(_.id)
    }
    run(q)
  }

  private def updateLikeCount(commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(
          a => a.likeCount -> (a.likeCount + 1)
        )
    }
    run(q).map(_ => Unit)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteCommentLikes(commentId, sessionId)
      r <- updateUnlikeCount(commentId)
    } yield (r)
  }

  private def deleteCommentLikes(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[CommentLikes]
        .filter(_.by        == lift(by))
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def updateUnlikeCount(commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(
          a => a.likeCount -> (a.likeCount - 1)
        )
    }
    run(q).map(_ => Unit)
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

  def findAll(commentId: CommentId,
              since: Option[Long],
              offset: Int,
              count: Int,
              sessionId: SessionId): Future[List[(Accounts, Option[Relationships], CommentLikes)]] = {

    val s = since.getOrElse(-1L)


    val by = sessionId.toAccountId

    val q = quote {
      query[CommentLikes]
        .filter(c => c.commentId == lift(commentId) && (c.likedAt < lift(s) || lift(s) == -1L))
        .filter(cf => query[Blocks].filter(b => b.accountId == lift(by) && b.by == cf.by).isEmpty)
        .filter(cf => query[Blocks].filter(b => b.accountId == cf.by && b.by == lift(by)).isEmpty)
        .join(query[Accounts]).on((cf, a) => a.id == cf.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((c, a), r) => (a, r, c)})
        .sortBy({ case (_, _, c) => c.likedAt })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q)

  }

}
