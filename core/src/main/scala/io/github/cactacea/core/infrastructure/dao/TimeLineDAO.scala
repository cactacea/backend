package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class TimeLineDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var feedsDAO: FeedsDAO = _
  @Inject private var feedTagsDAO: FeedTagsDAO = _
  @Inject private var feedMediumDAO: FeedMediumDAO = _

  def create(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
        infix"""
           insert into timelines (id, account_id, feed_id, `by`, posted_at)
           select generateId(), `by`, ${lift(feedId)}, account_id, CURRENT_TIMESTAMP from relationships where account_id = ${lift(by)} and friend = true and muted = false
          """.as[Action[Long]]
    }
    run(q).map(_ >= 0)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Timelines]
        .filter(_.accountId == lift(accountId))
        .filter(_.by.forall(_ == lift(by)))
        .delete
    }
    run(q).map(_ >= 0)
  }

  def delete(feedId: FeedId) = {
    val q = quote {
      query[Timelines]
        .filter(_.feedId.forall(_ == lift(feedId)))
        .delete
    }
    run(q).map(_ => true)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Timelines, Option[Feeds], Option[List[FeedTags]], Option[List[Mediums]], Option[Accounts], Option[Relationships])]] = {
    val s = since.getOrElse(Long.MaxValue)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId

    val q = quote {
      for {
        tl <- query[Timelines]
          .filter(_.accountId == lift(by))
          .filter(_.postedAt < lift(s))
          .sortBy(_.postedAt)(Ord.descNullsLast)
          .take(lift(c))
        t <- query[Feeds]
          .leftJoin(p => tl.feedId.exists(_ == p.id))
        u <- query[Accounts]
          .leftJoin(u => tl.by.exists(_ == u.id))
        r <- query[Relationships]
          .leftJoin(r => tl.by.exists(_ == r.accountId) && r.by == lift(by))
      } yield (tl, t, u, r)
    }
    run(q).flatMap(findTagsAndImages(_, sessionId)).map(_.sortBy(_._1.postedAt).reverse)

  }

  private def findTagsAndImages(feeds: List[(Timelines, Option[Feeds], Option[Accounts], Option[Relationships])], sessionId: SessionId) = {
    val feedIds = feeds.flatMap(_._2.map(_.id))
    (for {
      tags <- feedTagsDAO.findAll(feedIds)
      medium <- feedMediumDAO.findAll(feedIds)
    } yield (tags, medium)).map {
      case (tags, medium) =>
        feeds.map(t => {
          val tag = t._2 match {
            case Some(t) => Some(tags.filter(_.feedId == t.id))
            case None => None
          }
          val image = t._2 match {
            case Some(t) => Some(medium.filter(_._1 == t.id).map(_._2))
            case None => None
          }
          val r = (t._1, t._2, tag, image, t._3, t._4)
          r
        })
    }
  }

}
