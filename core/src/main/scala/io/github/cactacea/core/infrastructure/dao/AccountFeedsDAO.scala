package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}
import io.github.cactacea.core.infrastructure.models._

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

//  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Timelines, Option[Feeds], Option[List[FeedTags]], Option[List[Mediums]], Option[Accounts], Option[Relationships])]] = {
//    val s = since.getOrElse(-1L)
//    val c = count.getOrElse(20)
//    val o = offset.getOrElse(0)
//    val by = sessionId.toAccountId
//    val q = quote {
//      for {
//        tl <- query[Timelines]
//          .filter(_.accountId == lift(by))
//          .filter(_ => (infix"id < ${lift(s)}".as[Boolean] || lift(s) == -1L))
//          .sortBy(_.id)(Ord.descNullsLast)
//          .take(lift(c))
//        t <- query[Feeds]
//          .leftJoin(p => tl.feedId.exists(_ == p.id))
//        u <- query[Accounts]
//          .leftJoin(u => tl.by.exists(_ == u.id))
//        r <- query[Relationships]
//          .leftJoin(r => tl.by.exists(_ == r.accountId) && r.by == lift(by))
//      } yield (tl, t, u, r)
//    }
//    run(q).flatMap(findTagsAndImages(_, sessionId))
//
//  }
//
//  private def findTagsAndImages(feeds: List[(Timelines, Option[Feeds], Option[Accounts], Option[Relationships])], sessionId: SessionId) = {
//    val feedIds = feeds.flatMap(_._2.map(_.id))
//    ((for {
//      tags <- feedTagsDAO.findAll(feedIds)
//      medium <- feedMediumDAO.findAll(feedIds)
//    } yield (tags, medium)).map {
//      case (tags, medium) =>
//        feeds.map(t => {
//          val tag = t._2 match {
//            case Some(t) => Some(tags.filter(_.feedId == t.id))
//            case None => None
//          }
//          val image = t._2 match {
//            case Some(t) => Some(medium.filter(_._1 == t.id).map(_._2))
//            case None => None
//          }
//          val r = (t._1, t._2, tag, image, t._3, t._4)
//          r
//        })
//    })
//  }

}
