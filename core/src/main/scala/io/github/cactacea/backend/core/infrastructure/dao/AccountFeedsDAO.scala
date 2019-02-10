package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AccountFeedsDAO @Inject()(db: DatabaseService, feedTagsDAO: FeedTagsDAO, feedMediumDAO: FeedMediumDAO) {

  import db._

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      infix"""
        insert into account_feeds (account_id, feed_id, `by`, notified, posted_at)
        select r.`by`, ${lift(feedId)}, r.account_id, false as notified, CURRENT_TIMESTAMP
        from relationships r, feeds f
        where f.id = ${lift(feedId)}
        and r.account_id = ${lift(by)}
        and (
           (r.is_follower = true and (f.privacy_type in (0, 1)))
        or (r.is_friend = true and (f.privacy_type in (0, 1, 2)))
            )
        and r.muting = 0
        """.as[Action[Long]]
    }
    run(q).map(_ => Unit)
  }

  def find(since: Option[Long],
           offset: Int,
           count: Int,
           privacyType: Option[FeedPrivacyType],
           sessionId: SessionId): Future[List[Feed]] = {

    val by = sessionId.toAccountId

    val q = quote {
      (for {
        af <- query[AccountFeeds]
          .filter(af => af.accountId == lift(by))
          .filter(af => lift(since).forall(af.feedId < _ ))
        f <- query[Feeds]
          .join(_.id == af.feedId)
          .filter(f => lift(privacyType).forall(_ == f.privacyType))
          .filter(f => f.by == lift(by) || (f.privacyType == lift(FeedPrivacyType.everyone) ||
            (query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(r =>
              (r.following == true && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                (r.isFriend == true && (f.privacyType == lift(FeedPrivacyType.friends)))
            ).nonEmpty)))
        a <- query[Accounts]
          .join(_.id == f.by)
        r <- query[Relationships]
            .leftJoin(r => r.accountId == f.by && r.by == lift(by))
      } yield (af, f, a, r))
        .sortBy({ case (af, _, _, _) => af.feedId})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }

    run(q).flatMap(
      findTagsAndImages(_)
    )
  }

  private def findTagsAndImages(feeds: List[(AccountFeeds, Feeds, Accounts, Option[Relationships])])
                : Future[List[Feed]] = {
    val feedIds = feeds.map({ case (_, f, _, _) => f.id})

    (for {
      t <- feedTagsDAO.find(feedIds)
      m <- feedMediumDAO.find(feedIds)
    } yield (t, m)).map {
      case (t, m) =>
        feeds.map({ case (af, f, a, r) =>
          val tags = t.filter(_.feedId == f.id)
          val images = m.filter({ case (id, _) => id == f.id }).map({ case (_, m) => m })
          Feed(f, tags, images, a, r, af.feedId.value)
        })
    }
  }

}
