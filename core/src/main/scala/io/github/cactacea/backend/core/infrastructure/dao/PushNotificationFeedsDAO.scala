package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.PushNotificationType
import io.github.cactacea.backend.core.domain.models.{Destination, PushNotification}
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, TweetId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class PushNotificationTweetsDAO @Inject()(
                                      db: DatabaseService,
                                      deepLinkService: DeepLinkService) {

  import db._

  def find(id: TweetId): Future[Option[Seq[PushNotification]]] = {
    findTweet(id).flatMap(_ match {
      case Some(f) =>
        findDestinations(id).map({ d =>
          val url = deepLinkService.getTweet(id)
          val r = d.groupBy(_.userName).map({ case (userName, destinations) =>
            PushNotification(userName, None, f.postedAt, url, destinations, PushNotificationType.tweet)
          }).toSeq
          Some(r)
        })
      case None =>
        Future.None
    })
  }

  private def findTweet(id: TweetId): Future[Option[Tweets]] = {
    val q = quote {
      query[Tweets]
        .filter(_.id == lift(id))
        .filter(_.notified == false)
    }
    run(q).map(_.headOption)
  }


  private def findDestinations(id: TweetId): Future[Seq[Destination]] = {
    val q = quote {
      for {
        af <- query[UserTweets]
          .filter(_.tweetId == lift(id))
          .filter(_.notified == false)
        a <- query[Users]
          .join(_.id == af.by)
        d <- query[Devices]
          .join(_.userId == af.userId)
          .filter(_.pushToken.isDefined)
        _ <- query[PushNotificationSettings]
          .join(_.userId == af.userId)
          .filter(_.tweet == true)
        r <- query[Relationships]
          .leftJoin(r => r.userId == af.by && r.by == af.userId)

      } yield {
        Destination(
          af.userId,
          d.pushToken.getOrElse(""),
          r.flatMap(_.displayName).getOrElse(a.displayName),
          af.by)
      }
    }
    run(q)
  }

  def update(tweetId: TweetId, userIds: Seq[UserId], notified: Boolean = true): Future[Unit] = {
    val q = quote {
      query[UserTweets]
        .filter(_.tweetId == lift(tweetId))
        .filter(m => liftQuery(userIds).contains(m.userId))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ => ())
  }

  def update(tweetId: TweetId, notified: Boolean): Future[Unit] = {
    val q = quote {
      query[Tweets]
        .filter(_.id == lift(tweetId))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ => ())
  }


}
