package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.application.services.TimeService
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, CommentLikeId, SessionId}
import io.github.cactacea.core.infrastructure.models._

@Singleton
class CommentLikesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _
  @Inject private var identifyService: IdentifyService = _

  def create(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    for {
      id <- identifyService.generate().map(CommentLikeId(_))
      _ <- _insertCommentLikes(id, commentId, sessionId)
      r <- _updateLikeCount(commentId)
    } yield (r)
  }

  private def _insertCommentLikes(id: CommentLikeId, commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val postedAt = timeService.nanoTime()
    val q = quote {
      query[CommentLikes]
        .insert(
          _.id          -> lift(id),
          _.commentId   -> lift(commentId),
          _.by          -> lift(by),
          _.postedAt    -> lift(postedAt)
        )
    }
    run(q).map(_ == 1)
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
        .filter(_.by      == lift(by)).size
    }
    run(q).map(_ == 1)
  }

  def findAll(commentId: CommentId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], Long)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[CommentLikes].filter(cf => cf.commentId == lift(commentId) && (infix"cf.id < ${lift(s)}".as[Boolean] || lift(s) == -1L) &&
          query[Blocks].filter(b => b.accountId == cf.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty)
        .join(query[Accounts]).on((cf, a) => a.id == cf.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((cf, _), _) => cf.id })(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }

    run(q).map(_.map({ case ((c, a), r) => (a, r, c.id.value)}))

  }

}