package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, NotificationType}
import io.github.cactacea.backend.core.domain.models.{Comment, Destination, Notification}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class CommentsDAO @Inject()(db: DatabaseService, deepLinkService: DeepLinkService) {

  import db._

  def create(tweetId: TweetId, message: String, replyId: Option[CommentId], sessionId: SessionId): Future[CommentId] = {
    for {
      i <- createComments(tweetId, message, replyId, sessionId)
      _ <- updateCommentCount(tweetId, 1L)
    } yield (i)
  }

  private def createComments(tweetId: TweetId, message: String, replyId: Option[CommentId], sessionId: SessionId): Future[CommentId] = {
    val by = sessionId.userId
    val postedAt = System.currentTimeMillis()
    val q = quote {
      query[Comments]
        .insert(
          _.tweetId            -> lift(tweetId),
          _.replyId           -> lift(replyId),
          _.by                -> lift(by),
          _.message           -> lift(message),
          _.likeCount         -> 0L,
          _.contentWarning    -> false,
          _.contentStatus     -> lift(ContentStatusType.unchecked),
          _.notified          -> false,
          _.postedAt          -> lift(postedAt)
        ).returningGenerated(_.id)
    }
    run(q)
  }

  def delete(tweetId: TweetId, commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- updateCommentCount(tweetId, -1L)
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

  def exists(tweetId: TweetId, commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .filter(_.tweetId == lift(tweetId))
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

  def find(tweetId: TweetId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Comment]] = {
    val by = sessionId.userId
    val q = quote {
      (for {
        (c, b) <- query[Comments]
          .filter(c => c.tweetId == lift(tweetId))
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

  private def updateCommentCount(tweetId: TweetId, count: Long): Future[Unit] = {
    val q = quote {
      query[Tweets]
        .filter(_.id == lift(tweetId))
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



  // notifications

  def findNotifications(id: CommentId): Future[Option[Seq[Notification]]] = {
    findComment(id).flatMap(_ match {
      case Some(c) =>
        findDestinations(id).map({ d =>
          val pt = c.replyId.isDefined match {
            case true => NotificationType.commentReply
            case false => NotificationType.tweetReply
          }
          val url = c.replyId.isDefined match {
            case true => deepLinkService.getComments(c.tweetId)
            case false => deepLinkService.getComment(c.tweetId, c.id)
          }
          val r = d.groupBy(_.userName).map({ case (userName, destinations) =>
            Notification(userName, None, c.postedAt, url, destinations, pt)
          }).toSeq
          Some(r)
        })
      case None =>
        Future.None
    })
  }

  private def findComment(id: CommentId): Future[Option[Comments]] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(id))
        .filter(!_.notified)
    }
    run(q).map(_.headOption)
  }

  private def findDestinations(id: CommentId): Future[Seq[Destination]] = {
    val q = quote {
      for {
        c <- query[Comments]
          .filter(_.id == lift(id))
          .filter(!_.notified)
        f <- query[Tweets]
          .join(_.id == c.tweetId)
        _ <- query[NotificationSettings]
          .join(_.userId == f.by)
          .filter(_.comment)
        a <- query[Users]
          .join(_.id == f.by)
        d <- query[Devices]
          .join(_.userId == f.by)
          .filter(_.pushToken.isDefined)
        r <- query[Relationships]
          .leftJoin(r => r.userId == c.by && r.by == f.by)
      } yield
        {
          Destination(
            f.by,
            d.pushToken.getOrElse(""),
            r.flatMap(_.displayName).getOrElse(a.displayName),
            c.by)
        }
    }
    run(q)
  }


  def updateNotified(commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }

}

