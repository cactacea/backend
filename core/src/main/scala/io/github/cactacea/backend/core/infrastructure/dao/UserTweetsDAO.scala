package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.core.domain.models.Tweet
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, TweetId, UserId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class UserTweetsDAO @Inject()(db: DatabaseService) {

  import db._
  import db.extras._

  def create(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      infix"""
        insert into user_tweets (user_id, tweet_id, `by`, notified, posted_at)
        select r.`by`, ${lift(tweetId)}, r.user_id, false as notified, CURRENT_TIMESTAMP
        from relationships r, tweets f
        where f.id = ${lift(tweetId)}
        and r.user_id = ${lift(by)}
        and (
           (r.follow = true and (f.privacy_type in (0, 1)))
        or (r.is_friend = true and (f.privacy_type in (0, 1, 2)))
            )
        and r.muting = 0
        """.as[Action[Long]]
    }
    run(q).map(_ => ())
  }

  def delete(tweetId: TweetId): Future[Unit] = {
    val q = quote {
      query[UserTweets]
        .filter(_.tweetId == lift(tweetId))
    }
    run(q).map(_ => ())
  }

  def find(since: Option[Long], offset: Int, count: Int, privacyType: Option[TweetPrivacyType], sessionId: SessionId): Future[Seq[Tweet]] = { // scalastyle:ignore
    val e = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      (for {
        af <- query[UserTweets]
          .filter(af => af.userId == lift(by))
          .filter(af => lift(since).forall(af.tweetId < _ ))
        (f, flb, fcb) <- query[Tweets]
          .join(_.id == af.tweetId)
          .filter(f => f.expiration.forall(_ > lift(e)))
          .filter(f => lift(privacyType).forall(_ == f.privacyType))
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
                ).size
            )
          )
        l <- query[TweetLikes].leftJoin(fl => fl.tweetId == f.id && fl.by == lift(by))
        i1 <- query[Mediums].leftJoin(_.id === f.mediumId1)
        i2 <- query[Mediums].leftJoin(_.id === f.mediumId2)
        i3 <- query[Mediums].leftJoin(_.id === f.mediumId3)
        i4 <- query[Mediums].leftJoin(_.id === f.mediumId4)
        i5 <- query[Mediums].leftJoin(_.id === f.mediumId5)
        a <- query[Users]
          .join(_.id == f.by)
          .filter(a => query[Blocks].filter(b => b.userId == lift(by) && b.by == a.id).isEmpty)
        r <- query[Relationships]
            .leftJoin(r => r.userId == f.by && r.by == lift(by))
      } yield (af, f, l, i1, i2, i3, i4, i5, a, r, flb, fcb))
        .sortBy({ case (af, _, _, _, _, _, _, _, _, _, _, _) => af.tweetId })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (af, f, l, i1, i2, i3, i4, i5, a, r, flb, fcb) =>
      val f2 = f.copy(commentCount = f.commentCount - fcb, likeCount = f.likeCount - flb)
      Tweet(f2, l, Seq(i1, i2, i3, i4, i5).flatten, a, r, af.tweetId.value)
    }))
  }


  // Notifications

  def updateNotified(tweetId: TweetId, userIds: Seq[UserId]): Future[Unit] = {
    val q = quote {
      query[UserTweets]
        .filter(_.tweetId == lift(tweetId))
        .filter(m => liftQuery(userIds).contains(m.userId))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }

}
