package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ContentStatusType
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class CommentsDAO @Inject()(
                             db: DatabaseService
                           ) {

  import db._

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      id <- insertComments(feedId, message, sessionId)
      _ <- updateCommentCount(feedId, 1L)
    } yield (id)
  }

  private def insertComments(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    val by = sessionId.toAccountId
    val postedAt = System.currentTimeMillis()
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

  private def updateCommentCount(feedId: FeedId, count: Long): Future[Unit] = {
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
    findFeedId(commentId).flatMap(_ match {
      case Some(feedId) =>
        for {
          _ <- updateCommentCount(feedId, -1L)
          _ <- deleteCommentReports(commentId)
          _ <- deleteCommentLikes(commentId)
          _ <- deleteComments(commentId, by)
        } yield (())
      case None =>
        Future.Unit
    })
  }

  private def deleteComments(commentId: CommentId, by: AccountId): Future[Unit] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(_.by == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def deleteCommentLikes(commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[CommentLikes]
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def deleteCommentReports(commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[CommentReports]
        .filter(_.commentId == lift(commentId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def findFeedId(commentId: CommentId): Future[Option[FeedId]] = {
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
        .filter(c => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
        ).isEmpty)
        .nonEmpty
    )
    run(q)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Option[Comment]] = {
    val by = sessionId.toAccountId

    val q = quote {
      for {
        (c, b) <- query[Comments]
          .filter(c => c.id == lift(commentId))
          .filter(c => query[Blocks].filter(b =>
            (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
          ).isEmpty)
          .map(c =>
            (c,
              query[CommentLikes]
                .filter(_.commentId == c.id)
                .filter(c =>
                  query[Blocks].filter(b =>
                    (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
                  ).nonEmpty
                ).size
            )
          )
        a <- query[Accounts]
          .join(_.id == c.by)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (c, a, r, b)
    }
    run(q).map(_.map({ case (c, a, r, b) =>
      Comment(c.copy(likeCount = c.likeCount - b), a, r)
    }).headOption)
  }

  def find(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Comment]] = {

    val by = sessionId.toAccountId

    val q = quote {
      (for {
        (c, b) <- query[Comments]
          .filter(c => c.feedId == lift(feedId))
          .filter(c => lift(since).forall(c.id  < _))
          .filter(c => query[Blocks].filter(b =>
            (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
          ).isEmpty)
            .map(c =>
              (c,
                query[CommentLikes]
                  .filter(_.commentId == c.id)
                  .filter(c =>
                    query[Blocks].filter(b =>
                      (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
                    ).nonEmpty
                  ).size
              )
            )
        a <- query[Accounts]
          .join(_.id == c.by)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (c, a, r, b))
        .sortBy({ case (c, _, _, _) => c.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (c, a, r, b) =>
      Comment(c.copy(likeCount = c.likeCount - b), a, r)
    }))
  }


}

