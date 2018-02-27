package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, SessionId}
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class CommentFavoritesDAO @Inject()(db: DatabaseService) {

  import db._

  def create(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _insertCommentFavorites(commentId, sessionId)
      r <- _updateFavoriteCount(commentId)
    } yield (r)
  }

  private def _insertCommentFavorites(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val postedAt = System.nanoTime()
    val q = quote {
      query[CommentFavorites]
        .insert(
          _.commentId   -> lift(commentId),
          _.by          -> lift(by),
          _.postedAt    -> lift(postedAt)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateFavoriteCount(commentId: CommentId): Future[Boolean] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(
          a => a.favoriteCount -> (a.favoriteCount + 1)
        )
    }
    run(q).map(_ == 1)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _deleteCommentFavorites(commentId, sessionId)
      r <- _updateUnfavoriteCount(commentId)
    } yield (r)
  }

  private def _deleteCommentFavorites(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[CommentFavorites]
        .filter(_.by        == lift(by))
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q).map(_ == 1)
  }

  private def _updateUnfavoriteCount(commentId: CommentId): Future[Boolean] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(
          a => a.favoriteCount -> (a.favoriteCount - 1)
        )
    }
    run(q).map(_ == 1)
  }

  def exist(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[CommentFavorites]
        .filter(_.commentId == lift(commentId))
        .filter(_.by      == lift(by)).size
    }
    run(q).map(_ == 1)
  }

  def findAll(commentId: CommentId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships])]] = {

    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[CommentFavorites].filter(cf => cf.commentId == lift(commentId) && cf.postedAt < lift(s) &&
          query[Blocks].filter(b => b.accountId == cf.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty)
        .join(query[Accounts]).on((cf, a) => a.id == cf.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((cf, _), _) => cf.postedAt })(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }

    run(q).map(_.map({ case ((c, a), r) => (a.copy(position = c.postedAt), r)}))

  }

}
