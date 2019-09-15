package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.domain.models.{Destination, Notification}
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class NotificationCommentsDAO @Inject()(
                                             db: DatabaseService,
                                             deepLinkService: DeepLinkService) {

  import db._

  def find(id: CommentId): Future[Option[Seq[Notification]]] = {
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
        .filter(_.notified == false)
    }
    run(q).map(_.headOption)
  }

  private def findDestinations(id: CommentId): Future[Seq[Destination]] = {
    val q = quote {
      for {
        c <- query[Comments]
          .filter(_.id == lift(id))
          .filter(_.notified == false)
        f <- query[Tweets]
          .join(_.id == c.tweetId)
        _ <- query[NotificationSettings]
          .join(_.userId == f.by)
          .filter(_.comment == true)
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


  def update(commentId: CommentId): Future[Unit] = {
    val q = quote {
      query[Comments]
        .filter(_.id == lift(commentId))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }


}
