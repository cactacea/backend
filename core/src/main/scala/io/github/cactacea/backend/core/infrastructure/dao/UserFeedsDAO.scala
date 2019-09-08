package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class UserFeedsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      infix"""
        insert into user_feeds (user_id, feed_id, `by`, notified, posted_at)
        select r.`by`, ${lift(feedId)}, r.user_id, false as notified, CURRENT_TIMESTAMP
        from relationships r, feeds f
        where f.id = ${lift(feedId)}
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

  def delete(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[UserFeeds]
        .filter(_.feedId == lift(feedId))
    }
    run(q).map(_ => ())
  }

  def find(since: Option[Long], offset: Int, count: Int, privacyType: Option[FeedPrivacyType], sessionId: SessionId): Future[Seq[Feed]] = { // scalastyle:ignore
    val e = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      (for {
        af <- query[UserFeeds]
          .filter(af => af.userId == lift(by))
          .filter(af => lift(since).forall(af.feedId < _ ))
        (f, flb, fcb) <- query[Feeds]
          .join(_.id == af.feedId)
          .filter(f => f.expiration.forall(_ > lift(e)))
          .filter(f => lift(privacyType).forall(_ == f.privacyType))
          .map(f =>
            (f,
              query[FeedLikes]
                .filter(_.feedId == f.id)
                .filter(fl =>
                  query[Blocks].filter(b => b.userId == lift(by) && b.by == fl.by).nonEmpty
                ).size,
              query[Comments]
                .filter(_.feedId == f.id)
                .filter(c =>
                  query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).nonEmpty
                ).size
            )
          )
        l <- query[FeedLikes].leftJoin(fl => fl.feedId == f.id && fl.by == lift(by))
        i1 <- query[Mediums].leftJoin(_.id == f.mediumId1)
        i2 <- query[Mediums].leftJoin(_.id == f.mediumId2)
        i3 <- query[Mediums].leftJoin(_.id == f.mediumId3)
        i4 <- query[Mediums].leftJoin(_.id == f.mediumId4)
        i5 <- query[Mediums].leftJoin(_.id == f.mediumId5)
        a <- query[Users]
          .join(_.id == f.by)
          .filter(a => query[Blocks].filter(b => b.userId == lift(by) && b.by == a.id).isEmpty)
        r <- query[Relationships]
            .leftJoin(r => r.userId == f.by && r.by == lift(by))
      } yield (af, f, l, i1, i2, i3, i4, i5, a, r, flb, fcb))
        .sortBy({ case (af, _, _, _, _, _, _, _, _, _, _, _) => af.feedId })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (af, f, l, i1, i2, i3, i4, i5, a, r, flb, fcb) =>
      val f2 = f.copy(commentCount = f.commentCount - fcb, likeCount = f.likeCount - flb)
      Feed(f2, l, Seq(i1, i2, i3, i4, i5).flatten, a, r, af.feedId.value)
    }))
  }


}
