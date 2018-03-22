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


}
