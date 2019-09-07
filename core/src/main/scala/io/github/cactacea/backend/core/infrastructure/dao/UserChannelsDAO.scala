package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ChannelAuthorityType
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class UserChannelsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(channelId: ChannelId, authorityType: ChannelAuthorityType, sessionId: SessionId): Future[UserChannelId] = {
    create(sessionId.userId, channelId, authorityType, sessionId)
  }

  def create(userId: UserId, channelId: ChannelId, authorityType: ChannelAuthorityType, sessionId: SessionId): Future[UserChannelId] = {
    for {
      r <- createUserChannels(userId, channelId, authorityType, sessionId)
      _ <- updateUserCount(channelId, 1L)
    } yield (r)
  }

  private def createUserChannels(userId: UserId, channelId: ChannelId, authorityType: ChannelAuthorityType, sessionId: SessionId): Future[UserChannelId] = {
    val joinedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[UserChannels]
        .insert(
          _.userId              -> lift(userId),
          _.channelId           -> lift(channelId),
          _.unreadCount         -> 0L,
          _.joinedAt            -> lift(joinedAt),
          _.by                  -> lift(by),
          _.hidden              -> false,
          _.authorityType       -> lift(authorityType),
          _.mute                -> false
        ).returning(_.id)
    }
    run(q)
  }

  def delete(userId: UserId, channelId: ChannelId): Future[Unit] = {
    for {
      _ <- deleteUserChannels(userId, channelId)
      _ <- updateUserCount(channelId, -1L)
    } yield (())
  }

  private def deleteUserChannels(userId: UserId, channelId: ChannelId): Future[Unit] = {
    val q = quote {
      query[UserChannels]
        .filter(_.channelId == lift(channelId))
        .filter(_.userId  == lift(userId))
        .delete
    }
    run(q).map(_ => ())
  }

  def exists(channelId: ChannelId, userId: UserId): Future[Boolean] = {
    val q = quote {
      query[UserChannels]
        .filter(_.channelId     == lift(channelId))
        .filter(_.userId   == lift(userId))
        .nonEmpty
    }
    run(q)
  }

  def updateUnreadCount(channelId: ChannelId): Future[Unit] = {
    val q = quote {
      query[UserChannels]
        .filter(_.channelId == lift(channelId))
        .update(
          ug => ug.hidden       -> false,
          ug => ug.unreadCount  -> (ug.unreadCount + lift(1))
        )
    }
    run(q).map(_ => ())
  }

  def updateHidden(channelId: ChannelId, hidden: Boolean, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[UserChannels]
        .filter(_.channelId     == lift(channelId))
        .filter(_.userId   == lift(by))
        .update(
          _.hidden -> lift(hidden)
        )
    }
    run(q).map(_ => ())
  }

  def updateAuthorityType(channelId: ChannelId, authorityType: ChannelAuthorityType, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[UserChannels]
        .filter(_.channelId     == lift(channelId))
        .filter(_.userId   == lift(by))
        .update(
          _.authorityType -> lift(authorityType)
        )
    }
    run(q).map(_ => ())
  }

  def findByUserId(userId: UserId, sessionId: SessionId): Future[Option[Channel]] = {
    val by = sessionId.userId
    val q = quote {
      for {
        ag <- query[UserChannels]
          .filter(_.userId == lift(userId))
          .filter(_.by        == lift(by))
        g <- query[Channels]
          .join(_.id == ag.channelId)
        am <- query[UserMessages]
          .leftJoin(_.messageId == g.messageId)
        m <- query[Messages]
          .leftJoin(_.id == g.messageId)
        i <- query[Mediums]
          .leftJoin(_.id == m.map(_.mediumId))
        a <- query[Users]
          .leftJoin(_.id == m.map(_.by))
        r <- query[Relationships]
          .leftJoin(r => a.map(_.id) == r.userId && r.by == lift(by))
      } yield (g, am, m, i, a, r, ag.id)
    }
    run(q).map(_.map({ case (g, am, m, i, a, r, id) => Channel(g, am, m, i, a, r, id.value)}).headOption)
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[Seq[Channel]] = {
    val by = sessionId.userId
    val q = quote {
      (for {
        ag <- query[UserChannels]
          .filter(_.userId == lift(userId))
          .filter(_.hidden == lift(hidden))
          .filter(ag => lift(since).forall(ag.id < _))
        g <- query[Channels]
          .join(_.id == ag.channelId)
        am <- query[UserMessages]
          .leftJoin(am => am.userId == lift(userId) && am.messageId == g.messageId)
        m <- query[Messages]
          .leftJoin(_.id == g.messageId)
        i <- query[Mediums]
          .leftJoin(_.id == m.map(_.mediumId))
        a <- query[Users]
          .leftJoin(_.id == m.map(_.by))
        r <- query[Relationships]
            .leftJoin(r => r.userId == a.map(_.id) && r.by == lift(by))
      } yield (g, am, m, i, a, r, ag.id))
        .sortBy({ case (_, _, _, _, _, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (g, am, m, i, a, r, id) => Channel(g, am, m, i, a, r, id.value)}))
  }

  def isHidden(channelId: ChannelId, sessionId: SessionId): Future[Option[Boolean]] = {
    val userId = sessionId.userId
    val q = quote {
      query[UserChannels]
        .filter(_.channelId == lift(channelId))
        .filter(_.userId == lift(userId))
        .map(_.hidden)
    }
    run(q).map(_.headOption)
  }

  private def updateUserCount(channelId: ChannelId, plus: Long): Future[Unit] = {
    val q = quote {
      query[Channels]
        .filter(_.id == lift(channelId))
        .update(g => g.userCount -> (g.userCount + lift(plus)))
    }
    run(q).map(_ => ())
  }


}

