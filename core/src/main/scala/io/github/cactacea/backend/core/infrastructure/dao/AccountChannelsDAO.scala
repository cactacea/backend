package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AccountChannelsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(channelId: ChannelId, sessionId: SessionId): Future[AccountChannelId] = {
    create(sessionId.toAccountId, channelId, sessionId)
  }

  def create(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[AccountChannelId] = {
    for {
      r <- createAccountChannels(accountId, channelId, sessionId)
      _ <- updateAccountCount(channelId, 1L)
    } yield (r)
  }

  private def createAccountChannels(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[AccountChannelId] = {
    val joinedAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountChannels]
        .insert(
          _.accountId           -> lift(accountId),
          _.channelId             -> lift(channelId),
          _.unreadCount         -> 0L,
          _.joinedAt            -> lift(joinedAt),
          _.by                  -> lift(by),
          _.hidden              -> false,
          _.mute                -> false
        ).returning(_.id)
    }
    run(q)
  }

  def delete(accountId: AccountId, channelId: ChannelId): Future[Unit] = {
    for {
      _ <- deleteAccountChannels(accountId, channelId)
      _ <- updateAccountCount(channelId, -1L)
    } yield (())
  }

  private def deleteAccountChannels(accountId: AccountId, channelId: ChannelId): Future[Unit] = {
    val q = quote {
      query[AccountChannels]
        .filter(_.channelId == lift(channelId))
        .filter(_.accountId  == lift(accountId))
        .delete
    }
    run(q).map(_ => ())
  }

  def exists(channelId: ChannelId, accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[AccountChannels]
        .filter(_.channelId     == lift(channelId))
        .filter(_.accountId   == lift(accountId))
        .nonEmpty
    }
    run(q)
  }

  def updateUnreadCount(channelId: ChannelId): Future[Unit] = {
    val q = quote {
      query[AccountChannels]
        .filter(_.channelId == lift(channelId))
        .update(
          ug => ug.hidden       -> false,
          ug => ug.unreadCount  -> (ug.unreadCount + lift(1))
        )
    }
    run(q).map(_ => ())
  }

  def updateHidden(channelId: ChannelId, hidden: Boolean, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountChannels]
        .filter(_.channelId     == lift(channelId))
        .filter(_.accountId   == lift(by))
        .update(
          _.hidden -> lift(hidden)
        )
    }
    run(q).map(_ => ())
  }


  def findByAccountId(accountId: AccountId, sessionId: SessionId): Future[Option[Channel]] = {
    val by = sessionId.toAccountId
    val q = quote {
      for {
        ag <- query[AccountChannels]
          .filter(_.accountId == lift(accountId))
          .filter(_.by        == lift(by))
        g <- query[Channels]
          .join(_.id == ag.channelId)
        am <- query[AccountMessages]
          .leftJoin(_.messageId == g.messageId)
        m <- query[Messages]
          .leftJoin(_.id == g.messageId)
        i <- query[Mediums]
          .leftJoin(_.id == m.map(_.mediumId))
        a <- query[Accounts]
          .leftJoin(_.id == m.map(_.by))
        r <- query[Relationships]
          .leftJoin(r => a.map(_.id) == r.accountId && r.by == lift(by))
      } yield (g, am, m, i, a, r, ag.id)
    }
    run(q).map(_.map({ case (g, am, m, i, a, r, id) => Channel(g, am, m, i, a, r, id.value)}).headOption)
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Channel]] = {
    val by = sessionId.toAccountId
    val q = quote {
      (for {
        ag <- query[AccountChannels]
          .filter(_.accountId == lift(accountId))
          .filter(_.hidden == lift(hidden))
          .filter(ag => lift(since).forall(ag.id < _))
        g <- query[Channels]
          .join(_.id == ag.channelId)
        am <- query[AccountMessages]
          .leftJoin(am => am.accountId == lift(accountId) && am.messageId == g.messageId)
        m <- query[Messages]
          .leftJoin(_.id == g.messageId)
        i <- query[Mediums]
          .leftJoin(_.id == m.map(_.mediumId))
        a <- query[Accounts]
          .leftJoin(_.id == m.map(_.by))
        r <- query[Relationships]
            .leftJoin(r => r.accountId == a.map(_.id) && r.by == lift(by))
      } yield (g, am, m, i, a, r, ag.id))
        .sortBy({ case (_, _, _, _, _, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (g, am, m, i, a, r, id) => Channel(g, am, m, i, a, r, id.value)}))
  }

  def isHidden(channelId: ChannelId, sessionId: SessionId): Future[Option[Boolean]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[AccountChannels]
        .filter(_.channelId == lift(channelId))
        .filter(_.accountId == lift(accountId))
        .map(_.hidden)
    }
    run(q).map(_.headOption)
  }

  private def updateAccountCount(channelId: ChannelId, plus: Long): Future[Unit] = {
    val q = quote {
      query[Channels]
        .filter(_.id == lift(channelId))
        .update(g => g.accountCount -> (g.accountCount + lift(plus)))
    }
    run(q).map(_ => ())
  }


}

