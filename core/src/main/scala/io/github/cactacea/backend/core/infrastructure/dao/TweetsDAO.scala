package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, NotificationType, TweetPrivacyType}
import io.github.cactacea.backend.core.domain.models.{Destination, Notification, Tweet}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class TweetsDAO @Inject()(db: DatabaseService, deepLinkService: DeepLinkService) {

  import db._
  import db.extras._

  def create(message: String,
             mediumIds: Option[Seq[MediumId]],
             tags: Option[Seq[String]],
             privacyType: TweetPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[TweetId] = {

    for {
      r <- createTweets(message, mediumIds, tags, privacyType, contentWarning, expiration, sessionId)
      _ <- updateTweetCount(1L, sessionId)
    } yield (r)
  }

  private def createTweets(message: String,
                          mediumIds: Option[Seq[MediumId]],
                          tags: Option[Seq[String]],
                          privacyType: TweetPrivacyType,
                          contentWarning: Boolean,
                          expiration: Option[Long],
                          sessionId: SessionId): Future[TweetId] = {

    val by = sessionId.userId
    val postedAt = System.currentTimeMillis()
    val t = tags.map(_.mkString(" "))
    val i1 = mediumIds.flatMap(_.lift(0))
    val i2 = mediumIds.flatMap(_.lift(1))
    val i3 = mediumIds.flatMap(_.lift(2))
    val i4 = mediumIds.flatMap(_.lift(3))
    val i5 = mediumIds.flatMap(_.lift(4))
    val q = quote {
      query[Tweets].insert(
        _.by                  -> lift(by),
        _.message             -> lift(message),
        _.tags                -> lift(t),
        _.mediumId1           -> lift(i1),
        _.mediumId2           -> lift(i2),
        _.mediumId3           -> lift(i3),
        _.mediumId4           -> lift(i4),
        _.mediumId5           -> lift(i5),
        _.likeCount           -> 0L,
        _.commentCount        -> 0L,
        _.expiration          -> lift(expiration),
        _.privacyType         -> lift(privacyType),
        _.contentWarning      -> lift(contentWarning),
        _.contentStatus       -> lift(ContentStatusType.unchecked),
        _.notified            -> false,
        _.postedAt            -> lift(postedAt)
      ).returningGenerated(_.id)
    }
    run(q)

  }

  def update(tweetId: TweetId,
             message: String,
             mediumIds: Option[Seq[MediumId]],
             tags: Option[Seq[String]],
             privacyType: TweetPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[Unit] = {

    val by = sessionId.userId
    val privacy = privacyType
    val t = tags.map(_.mkString(" "))
    val i1 = mediumIds.flatMap(_.lift(0))
    val i2 = mediumIds.flatMap(_.lift(1))
    val i3 = mediumIds.flatMap(_.lift(2))
    val i4 = mediumIds.flatMap(_.lift(3))
    val i5 = mediumIds.flatMap(_.lift(4))
    val q = quote {
      query[Tweets]
        .filter(_.id == lift(tweetId))
        .filter(_.by == lift(by))
        .update(
          _.message         -> lift(message),
          _.tags            -> lift(t),
          _.mediumId1       -> lift(i1),
          _.mediumId2       -> lift(i2),
          _.mediumId3       -> lift(i3),
          _.mediumId4       -> lift(i4),
          _.mediumId5       -> lift(i5),
          _.contentWarning  -> lift(contentWarning),
          _.expiration      -> lift(expiration),
          _.privacyType     -> lift(privacy)
        )
    }
    run(q).map(_ => ())

  }

  def delete(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteTweets(tweetId, sessionId)
      _ <- updateTweetCount(-1L, sessionId)
    } yield (())
  }

  private def deleteTweets(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Tweets]
        .filter(_.by == lift(by))
        .filter(_.id == lift(tweetId))
        .delete
    }
    run(q).map(_ => ())
  }



  def own(tweetId: TweetId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Tweets]
        .filter(_.id == lift(tweetId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def exists(tweetId: TweetId, sessionId: SessionId): Future[Boolean] = {
    val e = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[Tweets]
        .filter(_.id == lift(tweetId))
        .filter(f => f.expiration.forall(_ > lift(e)))
        .filter(f =>
          query[Blocks].filter(b => (b.userId == lift(by) && b.by == f.by)).isEmpty)
        .filter({ f =>
          (f.by == lift(by)) ||
          (f.privacyType == lift(TweetPrivacyType.everyone)) ||
            (query[Relationships]
              .filter(_.userId == f.by)
              .filter(_.by == lift(by))
              .filter(r =>
              ((r.follow || r.isFriend) && (f.privacyType == lift(TweetPrivacyType.followers))) ||
                (r.isFriend && (f.privacyType == lift(TweetPrivacyType.friends)))
            ).nonEmpty)
            })
        .nonEmpty
    }
    run(q)
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Tweet]] = {  // scalastyle:ignore
    val e = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      (for {
        (f, flb, fcb) <- query[Tweets]
          .filter(f => f.by == lift(userId))
          .filter(f => f.expiration.forall(_ > lift(e)))
          .filter(f => lift(since).forall(f.id < _))
          .filter(f => (
              f.by == lift(by)) || (f.privacyType == lift(TweetPrivacyType.everyone)) ||
              (query[Relationships]
                .filter(_.userId == f.by)
                .filter(_.by == lift(by))
                .filter(r =>
                  ((r.follow || r.isFriend) && (f.privacyType == lift(TweetPrivacyType.followers))) ||
                    (r.isFriend && (f.privacyType == lift(TweetPrivacyType.friends)))
                ).nonEmpty))
          .map(f =>
            (f,
              query[TweetLikes]
                .filter(_.tweetId == f.id)
                .filter(fl =>
                  query[Blocks].filter(b => b.userId == lift(by) && b.by == fl.by).nonEmpty
                ).size,
              query[Comments]
                .filter(_.tweetId == f.id)
                .filter(c =>
                  query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).nonEmpty
                ).size))
        l <- query[TweetLikes].leftJoin(fl => fl.tweetId == f.id && fl.by == lift(by))
        i1 <- query[Mediums].leftJoin(_.id === f.mediumId1)
        i2 <- query[Mediums].leftJoin(_.id === f.mediumId2)
        i3 <- query[Mediums].leftJoin(_.id === f.mediumId3)
        i4 <- query[Mediums].leftJoin(_.id === f.mediumId4)
        i5 <- query[Mediums].leftJoin(_.id === f.mediumId5)
        a <- query[Users]
          .join(a => a.id == f.by)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (f, l, i1, i2, i3, i4, i5, a, r, flb, fcb))
        .sortBy({ case (f, _, _, _, _, _, _, _, _, _, _) => f.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (f, l, i1, i2, i3, i4, i5, a, r, flb, fcb) =>
      val f2 = f.copy(commentCount = f.commentCount - fcb, likeCount = f.likeCount - flb)
      Tweet(f2, l, Seq(i1, i2, i3, i4, i5).flatten, a, r, f.id.value)
    }))
  }


  def find(tweetId: TweetId, sessionId: SessionId): Future[Option[Tweet]] = {  // scalastyle:ignore
    val by = sessionId.userId
    val e = System.currentTimeMillis()
    val q = quote {
      (for {
        (f, flb, fcb) <- query[Tweets]
          .filter(f => f.id == lift(tweetId))
          .filter(f => f.expiration.forall(_ > lift(e)))
          .filter(f => query[Blocks].filter(b => (b.userId == lift(by) && b.by == f.by)).isEmpty)
          .filter(f => (
            f.by == lift(by)) || (f.privacyType == lift(TweetPrivacyType.everyone)) ||
            (query[Relationships]
              .filter(_.userId == f.by)
              .filter(_.by == lift(by))
              .filter(r =>
                ((r.follow || r.isFriend) && (f.privacyType == lift(TweetPrivacyType.followers))) ||
                  (r.isFriend && (f.privacyType == lift(TweetPrivacyType.friends)))
              ).nonEmpty))
          .map(f =>
            (f, query[TweetLikes]
              .filter(_.tweetId == f.id)
              .filter(fl =>
                query[Blocks].filter(b => b.userId == lift(by) && b.by == fl.by).nonEmpty
              ).size,
              query[Comments]
                .filter(_.tweetId == f.id)
                .filter(c =>
                  query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).nonEmpty
                ).size))
        l <- query[TweetLikes].leftJoin(fl => fl.tweetId == f.id && fl.by == lift(by))
        i1 <- query[Mediums].leftJoin(_.id === f.mediumId1)
        i2 <- query[Mediums].leftJoin(_.id === f.mediumId2)
        i3 <- query[Mediums].leftJoin(_.id === f.mediumId3)
        i4 <- query[Mediums].leftJoin(_.id === f.mediumId4)
        i5 <- query[Mediums].leftJoin(_.id === f.mediumId5)
        a <- query[Users]
          .join(_.id == f.by)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (f, l, i1, i2, i3, i4, i5, a, r, flb, fcb))
    }
    run(q).map(_.headOption.map({ case (f, l, i1, i2, i3, i4, i5, a, r, flb, fcb) =>
      val f2 = f.copy(commentCount = f.commentCount - fcb, likeCount = f.likeCount - flb)
      Tweet(f2, l, Seq(i1, i2, i3, i4, i5).flatten, a, r, f.id.value)
    }))
  }

  private def updateTweetCount(plus: Long, sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
        .update(
          a => a.tweetCount -> (a.tweetCount + lift(plus))
        )
    }
    run(q).map(_ => ())
  }

  def findOwner(tweetId: TweetId): Future[Option[UserId]] = {
    val q = quote {
      query[Tweets]
        .filter(_.id == lift(tweetId))
        .map(_.by)
    }
    run(q).map(_.headOption)
  }


  // Notifications

  def findNotifications(id: TweetId): Future[Option[Seq[Notification]]] = {
    findTweet(id).flatMap(_ match {
      case Some(f) =>
        findDestinations(id).map({ d =>
          val url = deepLinkService.getTweet(id)
          val r = d.groupBy(_.userName).map({ case (userName, destinations) =>
            Notification(userName, None, f.postedAt, url, destinations, NotificationType.tweet)
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
        .filter(!_.notified)
    }
    run(q).map(_.headOption)
  }


  private def findDestinations(id: TweetId): Future[Seq[Destination]] = {
    val q = quote {
      for {
        af <- query[UserTweets]
          .filter(_.tweetId == lift(id))
          .filter(!_.notified)
        a <- query[Users]
          .join(_.id == af.by)
        d <- query[Devices]
          .join(_.userId == af.userId)
          .filter(_.pushToken.isDefined)
        _ <- query[NotificationSettings]
          .join(_.userId == af.userId)
          .filter(_.tweet)
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

  def updateNotified(tweetId: TweetId): Future[Unit] = {
    val q = quote {
      query[Tweets]
        .filter(_.id == lift(tweetId))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }


}

