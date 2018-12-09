package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}
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
           (r.follower = true and (f.privacy_type in (0, 1)))
        or (r.friend = true and (f.privacy_type in (0, 1, 2)))
            )
        """.as[Action[Long]]
    }
    run(q).map(_ => Unit)
  }

  def update(feedId: FeedId, accountIds: List[AccountId], notified: Boolean = true): Future[Unit] = {
    val q = quote {
      query[AccountFeeds]
        .filter(_.feedId == lift(feedId))
        .filter(m => liftQuery(accountIds).contains(m.accountId))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ => Unit)
  }

  def findAll(since: Option[Long],
              offset: Int,
              count: Int,
              privacyType: Option[FeedPrivacyType],
              sessionId: SessionId): Future[List[Feed]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[AccountFeeds]
        .filter(f => f.accountId == lift(by))
        .filter(f => lift(since).forall(f.feedId < _ ))
        .join(query[Feeds])
        .on({ case (af, f) => af.feedId == f.id && lift(privacyType).forall(_ == f.privacyType) &&
          ((f.privacyType == lift(FeedPrivacyType.everyone)) ||
            (query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(r =>
              (r.follow == true && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                (r.friend == true && (f.privacyType == lift(FeedPrivacyType.friends)))
            ).nonEmpty) ||
            (f.by == lift(by)))})
        .join(query[Accounts]).on({ case ((_, f), a) => a.id == f.by })
        .leftJoin(query[Relationships]).on({ case (((_, f), _), r) => r.accountId == f.by && r.by == lift(by) })
        .map({ case (((af, f), a), r) => (af, f, a, r) })
        .sortBy({ case (af, _, _, _) => af.feedId })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))

    }
    run(q).flatMap(
      findTagsAndImages(_)
    )
  }

  private def findTagsAndImages(feeds: List[(AccountFeeds, Feeds, Accounts, Option[Relationships])])
                : Future[List[Feed]] = {
    val feedIds = feeds.map(_._2.id)

    (for {
      t <- feedTagsDAO.findAll(feedIds)
      m <- feedMediumDAO.findAll(feedIds)
    } yield (t, m)).map {
      case (t, m) =>
        feeds.map({ case (af, f, a, r) =>
          val tags = t.filter(_.feedId == f.id)
          val images = m.filter({ case (id, _) => id == f.id }).map(_._2)
          Feed(f, tags, images, a, r, af.feedId.value)
        })
    }
  }

}
