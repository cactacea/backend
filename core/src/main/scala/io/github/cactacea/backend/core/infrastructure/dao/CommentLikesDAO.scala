package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, CommentLikeId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class CommentLikesDAO @Inject()(db: DatabaseService) {

  import db._

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertCommentLikes(commentId, sessionId)
      _ <- updateLikeCount(1L, commentId)
    } yield (())
  }

  private def insertCommentLikes(commentId: CommentId, sessionId: SessionId): Future[CommentLikeId] = {
    val by = sessionId.userId
    val likedAt = System.currentTimeMillis()
    val q = quote {
      query[CommentLikes]
        .insert(
          _.commentId   -> lift(commentId),
          _.by          -> lift(by),
          _.likedAt     -> lift(likedAt)
        ).returning(_.id)
    }
    run(q)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteCommentLikes(commentId, sessionId)
      _ <- updateLikeCount(-1L, commentId)
    } yield (())
  }

  private def deleteCommentLikes(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[CommentLikes]
        .filter(_.by        == lift(by))
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[CommentLikes]
        .filter(_.commentId == lift(commentId))
        .filter(_.by      == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def findUsers(commentId: CommentId,
                since: Option[Long],
                offset: Int,
                count: Int,
                sessionId: SessionId): Future[Seq[User]] = {

    val by = sessionId.userId

    val q = quote {
      (for {
        cl <- query[CommentLikes]
          .filter(_.commentId == lift(commentId))
          .filter(cl => lift(since).forall(cl.id  < _))
          .filter(cl => query[Blocks].filter(b => b.userId == lift(by) && b.by == cl.by).isEmpty)
        a <- query[Users]
          .join(_.id == cl.by)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (a, r, cl.id))
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => User(a, r, id.value)}))

  }

  private def updateLikeCount(plus: Long, commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(
          a => a.likeCount -> (a.likeCount + lift(plus))
        )
    }
    run(q).map(_ => ())
  }


}
