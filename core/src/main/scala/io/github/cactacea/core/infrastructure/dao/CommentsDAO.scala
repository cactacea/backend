package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.application.services.TimeService
import io.github.cactacea.core.domain.enums.ContentStatusType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class CommentsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var blocksCountDAO: BlockCountDAO = _
  @Inject private var identifyService: IdentifyService = _
  @Inject private var timeService: TimeService = _

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      id <- identifyService.generate().map(CommentId(_))
      _ <- _insertComments(id, feedId, message, sessionId)
      _ <- _updateCommentCount(feedId)
    } yield (id)
  }

  private def _insertComments(id: CommentId, feedId: FeedId, message: String, sessionId: SessionId): Future[Long] = {
    val by = sessionId.toAccountId
    val postedAt = timeService.nanoTime()
    val replyId: Option[CommentId] = None
    val q = quote {
      query[Comments]
        .insert(
          _.id                -> lift(id),
          _.feedId            -> lift(feedId),
          _.replyId           -> lift(replyId),
          _.by                -> lift(by),
          _.message           -> lift(message),
          _.likeCount     -> 0L,
          _.contentWarning    -> false,
          _.contentStatus     -> lift(ContentStatusType.unchecked),
          _.notified          -> false,
          _.postedAt          -> lift(postedAt)
        )
    }
    run(q)
  }

  private def _updateCommentCount(feedId: FeedId): Future[Boolean] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.commentCount -> (a.commentCount + 1)
        )
    }
    run(q).map(_ == 1)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    _findFeedId(commentId).flatMap(_ match {
      case Some(feedId) =>
        for {
          _ <- _updateUnCommentCount(feedId)
          _ <- _deleteCommentReports(commentId)
          _ <- _deleteCommentLikes(commentId)
          _ <- _deleteComments(commentId, by)
        } yield (true)
      case None =>
        Future.False
    })
  }

  private def _deleteComments(commentId: CommentId, by: AccountId): Future[Boolean] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(_.by == lift(by))
        .delete
    }
    run(q).map(_ == 1)
  }

  private def _deleteCommentLikes(commentId: CommentId): Future[Long] = {
    val q = quote {
      query[CommentLikes]
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q)
  }

  private def _deleteCommentReports(commentId: CommentId): Future[Long] = {
    val q = quote {
      query[CommentReports]
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q)
  }

  private def _findFeedId(commentId: CommentId): Future[Option[FeedId]] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .map(_.feedId)
    }
    run(q).map(_.headOption)
  }

  private def _updateUnCommentCount(feedId: FeedId): Future[Boolean] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.commentCount -> (a.commentCount - 1)
        )
    }
    run(q).map(_ == 1)
  }

  def delete(feedId: FeedId) = {
    val q = quote {
      query[Comments]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => true)
  }

  def exist(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote(
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(c => query[Blocks]
          .filter(_.accountId == c.by)
          .filter(_.by     == lift(by))
          .filter(ur => (ur.blocked == true || ur.beingBlocked == true))
          .isEmpty)
        .take(1)
        .size
    )
    run(q).map(_ == 1)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Option[(Comments, Accounts, Option[Relationships])]] = {
    val by = sessionId.toAccountId

    val q = quote {
      query[Comments].filter(c => c.id == lift(commentId) &&
          query[Blocks].filter(b => b.accountId == c.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty)
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
      query[Comments].filter(c => c.feedId == lift(feedId) && (infix"c.id < ${lift(s)}".as[Boolean] || lift(s) == -1L) &&
        query[Blocks].filter(b => b.accountId == c.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty)
        .join(query[Accounts]).on((c, a) => a.id == c.by)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((c, _), _) => c.id })(Ord.descNullsLast)
        .take(lift(c))
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
        })
      })
      .map(_.sortBy(_._1.postedAt).reverse)
  }

  def find(commentId: CommentId): Future[Option[Comments]] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
    }
    run(q).map(_.headOption)
  }

  def findUnNotified(commentId: CommentId): Future[Option[Comments]] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(_.notified == false)
    }
    run(q).map(_.headOption)
  }

  def updateNotified(commentId: CommentId): Future[Boolean] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(_.notified -> true)
    }
    run(q).map(_ == 1)
  }

}

