package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class UserMessagesDAO @Inject()(db: DatabaseService) {

  import db._

  def create(channelId: ChannelId, messageId: MessageId, sessionId: SessionId): Future[Unit] = {
    val postedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      infix"""
         insert into user_messages (user_id, channel_id, message_id, `by`, unread, notified, posted_at)
         select user_id, channel_id, ${lift(messageId)} as message_id, ${lift(by)} as `by`, true as unread, false as notified, ${lift(postedAt)} posted_at
         from user_channels where channel_id = ${lift(channelId)}
          """.as[Action[Long]]
    }
    run(q).map(_ => ())
  }

  def delete(userId: UserId, channelId: ChannelId): Future[Unit] = {
    val q = quote {
      query[UserMessages]
        .filter(_.channelId == lift(channelId))
        .filter(_.userId == lift(userId))
        .delete
    }
    run(q).map(_ => ())
  }

  def find(channelId: ChannelId,
           since: Option[Long],
           offset: Int,
           count: Int,
           ascending: Boolean,
           sessionId: SessionId): Future[Seq[Message]] = {

    if (ascending) {
      findOlder(channelId, since, offset, count, sessionId)
    } else {
      findEarlier(channelId, since, offset, count, sessionId)
    }

  }

  private def findEarlier(channelId: ChannelId,
                          since: Option[Long],
                          offset: Int,
                          count: Int,
                          sessionId: SessionId): Future[Seq[Message]] = {

    val by = sessionId.userId
    val q = quote {
      (for {
        am <- query[UserMessages]
          .filter(_.userId == lift(by))
          .filter(_.channelId == lift(channelId) )
          .filter(am => lift(since).forall(am.messageId < _))
        m <- query[Messages]
          .join(_.id == am.messageId)
        a <- query[Users]
          .join(_.id == m.by)
        i <- query[Mediums]
          .leftJoin(i => m.mediumId.exists(_ == i.id))
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (m, am, i, a, r))
        .sortBy({ case (_, am, _, _, _) => am.messageId})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (m, am, i, a, r) => Message(m, am, i, a, r, am.messageId.value) }))

  }

  private def findOlder(channelId: ChannelId,
                        since: Option[Long],
                        offset: Int,
                        count: Int,
                        sessionId: SessionId): Future[Seq[Message]] = {
    val by = sessionId.userId
    val q = quote {
      (for {
        am <- query[UserMessages]
          .filter(_.userId == lift(by))
          .filter(_.channelId == lift(channelId) )
          .filter(am => lift(since).forall(am.messageId > _))
        m <- query[Messages]
          .join(_.id == am.messageId)
        a <- query[Users]
          .join(_.id == m.by)
        i <- query[Mediums]
          .leftJoin(_.id == m.mediumId)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (m, am, i, a, r))
        .sortBy({ case (_, am, _, _, _) => am.messageId})(Ord.asc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (m, am, i, a, r) => Message(m, am, i, a, r, am.messageId.value) }))
  }

  def find(id: MessageId, sessionId: SessionId): Future[Option[Message]] = {
    val by = sessionId.userId
    val q = quote {
      (for {
        am <- query[UserMessages]
          .filter(_.userId == lift(by))
          .filter(_.messageId == lift(id) )
        m <- query[Messages]
          .join(_.id == am.messageId)
        a <- query[Users]
          .join(_.id == m.by)
        i <- query[Mediums]
          .leftJoin(_.id == m.mediumId)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (m, am, i, a, r))
    }
    run(q).map(_.map({ case (m, am, i, a, r) => Message(m, am, i, a, r, am.messageId.value) }).headOption)
  }

  def updateUnread(messageIds: Seq[MessageId], sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    val q = quote {
      query[UserMessages]
        .filter(_.userId == lift(userId))
        .filter(m => liftQuery(messageIds).contains(m.messageId))
        .update(_.unread -> false)
    }
    run(q).map(_ => ())
  }




}
