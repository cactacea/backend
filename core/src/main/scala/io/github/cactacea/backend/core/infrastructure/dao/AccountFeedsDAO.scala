package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AccountFeedsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var feedTagsDAO: FeedTagsDAO = _
  @Inject private var feedMediumDAO: FeedMediumDAO = _

  def create(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      infix"""
           insert into account_feeds (account_id, feed_id, `by`, notified, posted_at)
           select `by`, ${lift(feedId)}, account_id, false as notified, CURRENT_TIMESTAMP from relationships where account_id = ${lift(by)} and follower = true
          """.as[Action[Long]]
    }
    run(q).map(_ >= 0)
  }

  def update(feedId: FeedId, accountIds: List[AccountId], notified: Boolean = true): Future[Boolean] = {
    val q = quote {
      query[AccountFeeds]
        .filter(_.feedId == lift(feedId))
        .filter(m => liftQuery(accountIds).contains(m.accountId))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ == accountIds.size)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], privacyType: FeedPrivacyType, sessionId: SessionId): Future[List[(AccountFeeds, Feeds, List[FeedTags], List[Mediums], Accounts, Option[Relationships])]] = {
    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId
    val q = quote {
      for {
        af <- query[AccountFeeds]
          .filter(_.accountId == lift(by))
          .filter(_.feedId < lift(s)  || lift(s) == -1)
          .sortBy(_.feedId)(Ord.descNullsLast)
          .take(lift(c))
        f <- query[Feeds]
          .join(f => f.id == af.feedId && (f.privacyType == lift(privacyType) || lift(privacyType) == lift(FeedPrivacyType.everyone)) )
        a <- query[Accounts]
          .join(a => a.id == f.by)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == f.by && r.by == lift(by))
      } yield (af, f, a, r)
    }
    run(q).flatMap(findTagsAndImages(_, sessionId))

  }

  private def findTagsAndImages(feeds: List[(AccountFeeds, Feeds, Accounts, Option[Relationships])], sessionId: SessionId): Future[List[(AccountFeeds, Feeds, List[FeedTags], List[Mediums], Accounts, Option[Relationships])]] = {
    val feedIds = feeds.map(_._2.id)

    ((for {
      tags <- feedTagsDAO.findAll(feedIds)
      medium <- feedMediumDAO.findAll(feedIds)
    } yield (tags, medium)).map {
      case (tags, medium) =>
        feeds.map(t => {
          val tag = tags.filter(_.feedId == t._2.id)
          val image = medium.filter(_._1 == t._2.id).map(_._2)
          (t._1, t._2, tag, image, t._3, t._4)
        })
    })
  }

}
