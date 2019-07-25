package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.infrastructure.dao.CommentsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.CommentNotFound

@Singleton
class CommentsValidator @Inject()(commentsDAO: CommentsDAO) {

//  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
//    for {
//      id <- insertComments(feedId, message, sessionId)
//      _ <- updateCommentCount(feedId, 1L)
//    } yield (id)
//  }
//
//  private def insertComments(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
//    val by = sessionId.toAccountId
//    val postedAt = System.currentTimeMillis()
//    val replyId: Option[CommentId] = None
//    val q = quote {
//      query[Comments]
//        .insert(
//          _.feedId            -> lift(feedId),
//          _.replyId           -> lift(replyId),
//          _.by                -> lift(by),
//          _.message           -> lift(message),
//          _.likeCount         -> 0L,
//          _.contentWarning    -> false,
//          _.contentStatus     -> lift(ContentStatusType.unchecked),
//          _.notified          -> false,
//          _.postedAt          -> lift(postedAt)
//        ).returning(_.id)
//    }
//    run(q)
//  }
//
//  private def updateCommentCount(feedId: FeedId, count: Long): Future[Unit] = {
//    val q = quote {
//      query[Feeds]
//        .filter(_.id == lift(feedId))
//        .update(
//          a => a.commentCount -> (a.commentCount + lift(count))
//        )
//    }
//    run(q).map(_ => Unit)
//  }
//
//  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
//    val by = sessionId.toAccountId
//    findFeedId(commentId).flatMap(_ match {
//      case Some(feedId) =>
//        for {
//          _ <- updateCommentCount(feedId, -1L)
//          _ <- deleteCommentReports(commentId)
//          _ <- deleteCommentLikes(commentId)
//          _ <- deleteComments(commentId, by)
//        } yield (())
//      case None =>
//        Future.Unit
//    })
//  }
//
//  private def deleteComments(commentId: CommentId, by: AccountId): Future[Unit] = {
//    val q = quote {
//      query[Comments]
//        .filter(_.id == lift(commentId))
//        .filter(_.by == lift(by))
//        .delete
//    }
//    run(q).map(_ => Unit)
//  }
//
//  private def deleteCommentLikes(commentId: CommentId): Future[Unit] = {
//    val q = quote {
//      query[CommentLikes]
//        .filter(_.commentId == lift(commentId))
//        .delete
//    }
//    run(q).map(_ => Unit)
//  }
//
//  private def deleteCommentReports(commentId: CommentId): Future[Unit] = {
//    val q = quote {
//      query[CommentReports]
//        .filter(_.commentId == lift(commentId))
//        .delete
//    }
//    run(q).map(_ => Unit)
//  }
//
//  private def findFeedId(commentId: CommentId): Future[Option[FeedId]] = {
//    val q = quote {
//      query[Comments]
//        .filter(_.id == lift(commentId))
//        .map(_.feedId)
//    }
//    run(q).map(_.headOption)
//  }
//
//  def delete(feedId: FeedId): Future[Unit] = {
//    val q = quote {
//      query[Comments]
//        .filter(_.feedId == lift(feedId))
//        .delete
//    }
//    run(q).map(_ => Unit)
//  }
//
//  def exist(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
//    val by = sessionId.toAccountId
//    val q = quote(
//      query[Comments]
//        .filter(_.id == lift(commentId))
//        .filter(c => query[Blocks].filter(b =>
//          (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
//        ).isEmpty)
//        .nonEmpty
//    )
//    run(q)
//  }
//
//  def find(commentId: CommentId, sessionId: SessionId): Future[Option[Comment]] = {
//    val by = sessionId.toAccountId
//
//    val q = quote {
//      query[Comments]
//        .filter(c => c.id == lift(commentId))
//        .filter(c => query[Blocks].filter(b =>
//          (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
//        ).isEmpty)
//        .join(query[Accounts]).on((c, a) => a.id == c.by)
//        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
//        .map({ case ((c, a), r) => (c, a, r) })
//    }
//
//    (for {
//      comments <- run(q)
//      ids = comments.map({ case (c, _, _) => c.id})
//      blocksCount <- blocksCountDAO.findCommentLikeBlocks(ids, sessionId)
//    } yield (comments, blocksCount))
//      .map({ case (accounts, blocksCount) =>
//        accounts.map({ case (c, a, r) =>
//          val b = blocksCount.filter(_.id == c.id).map(_.count).headOption
//          Comment(c.copy(likeCount = c.likeCount - b.getOrElse(0L)), a, r)
//        }).headOption
//      })
//  }
//
//  def find(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Comment]] = {
//
//    val by = sessionId.toAccountId
//
//    val q = quote {
//      query[Comments]
//        .filter(c => c.feedId == lift(feedId))
//        .filter(c => lift(since).forall(c.id  < _))
//        .filter(c => query[Blocks].filter(b =>
//          (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
//        ).isEmpty)
//        .join(query[Accounts]).on((c, a) => a.id == c.by)
//        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
//        .map({ case ((c, a), r) => (c, a, r) })
//        .sortBy({ case (c, _, _) => c.id })(Ord.desc)
//        .drop(lift(offset))
//        .take(lift(count))
//    }
//
//    (for {
//      c <- run(q)
//      ids = c.map({ case (c, _, _) =>  c.id})
//      b <- blocksCountDAO.findCommentLikeBlocks(ids, sessionId)
//    } yield (c, b))
//      .map({ case (accounts, blocksCount) =>
//        accounts.map({ case (c, a, r) =>
//          val b = blocksCount.filter(_.id == c.id).map(_.count).headOption
//          val c2 = c.copy(likeCount = c.likeCount - b.getOrElse(0L))
//          Comment(c2, a, r)
//        })
//      })
//  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsDAO.find(commentId, sessionId).flatMap(_ match {
      case Some(c) =>
        Future.value(c)
      case None =>
        Future.exception(CactaceaException(CommentNotFound))
    })
  }

  def exist(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentsDAO.exist(commentId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(CommentNotFound))
    })
  }



}

