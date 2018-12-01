package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
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
           select `by`, ${lift(feedId)}, account_id, false as notified, CURRENT_TIMESTAMP from relationships where account_id = ${lift(by)} and follower = true
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
              sessionId: SessionId): Future[List[(AccountFeeds, Feeds, List[FeedTags], List[Mediums], Accounts, Option[Relationships])]] = {

    val by = sessionId.toAccountId
    val q = quote {
      for {
        af <- query[AccountFeeds]
          .filter(f => f.accountId == lift(by))
          .filter(f => lift(since).forall(f.feedId < _ ))
          .sortBy(_.feedId)(Ord.desc)
          .drop(lift(offset))
          .take(lift(count))
        f <- query[Feeds]
          .join(f => f.id == af.feedId)
          .filter(f => lift(privacyType).forall(_ == f.privacyType))
        a <- query[Accounts]
          .join(a => a.id == f.by)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == f.by && r.by == lift(by))
      } yield (af, f, a, r)
    }
    run(q).flatMap(findTagsAndImages(_))

  }

  private def findTagsAndImages(feeds: List[(AccountFeeds, Feeds, Accounts, Option[Relationships])])
                : Future[List[(AccountFeeds, Feeds, List[FeedTags], List[Mediums], Accounts, Option[Relationships])]] = {
    val feedIds = feeds.map(_._2.id)

    (for {
      tags <- feedTagsDAO.findAll(feedIds)
      mediums <- feedMediumDAO.findAll(feedIds)
    } yield (tags, mediums)).map {
      case (tags, mediums) =>
        feeds.map({ case (af, f, a, r) =>
          val tag = tags.filter(_.feedId == f.id)
          val image = mediums.filter({ case (id, _) => id == f.id }).map(_._2)
          (af, f, tag, image, a, r)
        })
    }
  }

}
