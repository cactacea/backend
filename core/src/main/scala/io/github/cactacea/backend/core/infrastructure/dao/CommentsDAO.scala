package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ContentStatusType
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class CommentsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(feedId: FeedId, message: String, replyId: Option[CommentId], sessionId: SessionId): Future[CommentId] = {
    for {
      i <- createComments(feedId, message, replyId, sessionId)
      _ <- updateCommentCount(feedId, 1L)
    } yield (i)
  }

  private def createComments(feedId: FeedId, message: String, replyId: Option[CommentId], sessionId: SessionId): Future[CommentId] = {
    val by = sessionId.userId
    val postedAt = System.currentTimeMillis()
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

  def delete(feedId: FeedId, commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- updateCommentCount(feedId, -1L)
      _ <- deleteComments(commentId, sessionId)
    } yield (())
  }

  private def deleteComments(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(_.by == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def exists(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(c => query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).isEmpty)
        .nonEmpty
    }
    run(q)
  }

  def exists(feedId: FeedId, commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(_.feedId == lift(feedId))
        .filter(c => query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).isEmpty)
        .nonEmpty
    }
    run(q)
  }

  def own(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Option[Comment]] = {
    val by = sessionId.userId
    val q = quote {
      for {
        (c, b) <- query[Comments]
          .filter(c => c.id == lift(commentId))
          .filter(c => query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).isEmpty)
          .map(c =>
            (c,
              query[CommentLikes]
                .filter(_.commentId == c.id)
                .filter(c => query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).nonEmpty).size
            )
          )
        a <- query[Users]
          .join(_.id == c.by)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (c, a, r, b)
    }
    run(q).map(_.map({ case (c, a, r, b) =>
      Comment(c.copy(likeCount = c.likeCount - b), a, r)
    }).headOption)
  }

  def find(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Comment]] = {
    val by = sessionId.userId
    val q = quote {
      (for {
        (c, b) <- query[Comments]
          .filter(c => c.feedId == lift(feedId))
          .filter(c => lift(since).forall(c.id  < _))
          .filter(c => query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).isEmpty)
            .map(c =>
              (c,
                query[CommentLikes]
                  .filter(_.commentId == c.id)
                  .filter(c =>
                    query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).nonEmpty
                  ).size
              )
            )
        a <- query[Users]
          .join(_.id == c.by)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (c, a, r, b))
        .sortBy({ case (c, _, _, _) => c.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (c, a, r, b) =>
      Comment(c.copy(likeCount = c.likeCount - b), a, r)
    }))
  }

  private def updateCommentCount(feedId: FeedId, count: Long): Future[Unit] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.commentCount -> (a.commentCount + lift(count))
        )
    }
    run(q).map(_ => ())
  }

  def findOwner(commentId: CommentId): Future[Option[UserId]] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .map(_.by)
    }
    run(q).map(_.headOption)
  }


}

