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
              offset: Option[Int],
              count: Option[Int],
              privacyType: Option[FeedPrivacyType],
              sessionId: SessionId): Future[List[(AccountFeeds, Feeds, List[FeedTags], List[Mediums], Accounts, Option[Relationships])]] = {

    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val p = privacyType.getOrElse(FeedPrivacyType.everyone)
    val by = sessionId.toAccountId
    val q = quote {
      for {
        af <- query[AccountFeeds]
          .filter(_.accountId == lift(by))
          .filter(_.feedId < lift(s)  || lift(s) == -1)
          .sortBy(_.feedId)(Ord.desc)
          .drop(lift(o))
          .take(lift(c))
        f <- query[Feeds]
          .join(f => f.id == af.feedId && (f.privacyType == lift(p) || lift(p) == lift(FeedPrivacyType.everyone)) )
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
      medium <- feedMediumDAO.findAll(feedIds)
    } yield (tags, medium)).map {
      case (tags, medium) =>
        feeds.map(t => {
          val tag = tags.filter(_.feedId == t._2.id)
          val image = medium.filter(_._1 == t._2.id).map(_._2)
          (t._1, t._2, tag, image, t._3, t._4)
        })
    }
  }

}
