package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.ContentStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class CommentsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var blocksCountDAO: BlockCountDAO = _
  @Inject private var timeService: TimeService = _

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      id <- _insertComments(feedId, message, sessionId)
      _ <- _updateCommentCount(feedId, 1L)
    } yield (id)
  }

  private def _insertComments(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    val by = sessionId.toAccountId
    val postedAt = timeService.currentTimeMillis()
    val replyId: Option[CommentId] = None
    val q = quote {
      query[Comments]
        .insert(
          _.feedId            -> lift(feedId),
          _.replyId           -> lift(replyId),
          _.by                -> lift(by),
          _.message           -> lift(message),
          _.likeCount         -> 0L,
          _.contentWarning    -> false,
          _.contentStatus     -> lift(ContentStatusType.unchecked),
          _.notified          -> false,
          _.postedAt          -> lift(postedAt)
        ).returning(_.id)
    }
    run(q)
  }

  private def _updateCommentCount(feedId: FeedId, count: Long): Future[Unit] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.commentCount -> (a.commentCount + lift(count))
        )
    }
    run(q).map(_ => Unit)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    _findFeedId(commentId).flatMap(_ match {
      case Some(feedId) =>
        for {
          _ <- _updateCommentCount(feedId, -1L)
          _ <- _deleteCommentReports(commentId)
          _ <- _deleteCommentLikes(commentId)
          r <- _deleteComments(commentId, by)
        } yield (r)
      case None =>
        Future.Unit
    })
  }

  private def _deleteComments(commentId: CommentId, by: AccountId): Future[Unit] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(_.by == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def _deleteCommentLikes(commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[CommentLikes]
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def _deleteCommentReports(commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[CommentReports]
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def _findFeedId(commentId: CommentId): Future[Option[FeedId]] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .map(_.feedId)
    }
    run(q).map(_.headOption)
  }

  def delete(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[Comments]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def exist(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote(
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(c => query[Blocks]
          .filter(_.accountId == lift(by))
          .filter(_.by        == c.by)
          .isEmpty)
        .nonEmpty
    )
    run(q)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Option[(Comments, Accounts, Option[Relationships])]] = {
    val by = sessionId.toAccountId

    val q = quote {
      query[Comments]
        .filter(c => c.id == lift(commentId))
        .filter(c =>
          query[Blocks]
            .filter(_.accountId == lift(by))
            .filter(_.by        == c.by)
            .isEmpty)
        .join(query[Accounts]).on((c, a) => a.id == c.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
    }

    (for {
      comments <- run(q)
      ids = comments.map(_._1._1.id)
      blocksCount <- blocksCountDAO.findCommentLikeBlocks(ids, sessionId)
    } yield (comments, blocksCount))
      .map({ case (accounts, blocksCount) =>
        accounts.map({ t =>
          val c = t._1._1
          val a = t._1._2
          val r = t._2
          val b = blocksCount.filter(_.id == c.id).map(_.count).headOption
          (c.copy(likeCount = c.likeCount - b.getOrElse(0L)), a, r)
        }).headOption
      })
  }

  def findAll(feedId: FeedId, since: Option[Long], count: Option[Int], sessionId: SessionId): Future[List[(Comments, Accounts, Option[Relationships])]] = {

    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[Comments]
        .filter(c => c.feedId == lift(feedId) && (c.id < lift(s) || lift(s) == -1L))
        .filter(c =>
          query[Blocks]
            .filter(_.accountId == lift(by))
            .filter(_.by        == c.by)
            .isEmpty)
        .join(query[Accounts]).on((c, a) => a.id == c.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((c, _), _) => c.id })(Ord.desc)
        .take(lift(c))
    }

    (for {
      c <- run(q)
      ids = c.map(_._1._1.id)
      b <- blocksCountDAO.findCommentLikeBlocks(ids, sessionId)
    } yield (c, b))
      .map({ case (accounts, blocksCount) =>
        accounts.map({ case ((comment, account), relationship) =>
          val b = blocksCount.filter(_.id == comment.id).map(_.count).headOption
          (comment.copy(likeCount = comment.likeCount - b.getOrElse(0L)), account, relationship)
        })
      })
  }

  def find(commentId: CommentId): Future[Option[Comments]] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
    }
    run(q).map(_.headOption)
  }

  // TODO
  def findUnNotified(commentId: CommentId): Future[Option[Comments]] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(_.notified == false)
    }
    run(q).map(_.headOption)
  }

  def updateNotified(commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(_.notified -> true)
    }
    run(q).map(_ => Unit)
  }

}

