package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Invitation
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class InvitationsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[InvitationId] = {
    val invitedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[Invitations]
        .insert(
          _.userId         -> lift(userId),
          _.by                -> lift(by),
          _.channelId           -> lift(channelId),
          _.notified          -> false,
          _.invitedAt         -> lift(invitedAt)
        ).returning(_.id)
    }
    run(q)
  }

  def delete(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Invitations]
        .filter(_.userId == lift(userId))
        .filter(_.channelId   == lift(channelId))
        .filter(_.by        == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def exists(userId: UserId, channelId: ChannelId): Future[Boolean] = {
    val q = quote {
      query[Invitations]
        .filter(_.userId == lift(userId))
        .filter(_.channelId   == lift(channelId))
        .nonEmpty
    }
    run(q)
  }

  def find(userId: UserId, invitationId: InvitationId): Future[Option[(ChannelId, UserId)]] = {
    val q = quote {
      query[Invitations]
        .filter(_.id        == lift(invitationId))
        .filter(_.userId == lift(userId))
        .map(f => (f.channelId, f.userId))
    }
    run(q).map(_.headOption)
  }


  def find(since: Option[Long], offset: Int,
           count: Int, sessionId: SessionId): Future[Seq[Invitation]] = {
    val by = sessionId.userId
    val q = quote {
      query[Invitations]
        .filter(gi => gi.userId == lift(by))
        .filter(gi => lift(since).forall(gi.id < _))
        .join(query[Channels]).on((gi, g) => g.id == gi.channelId)
        .join(query[Users]).on({ case ((gi, _), a) => a.id == gi.by})
        .leftJoin(query[Relationships]).on({ case (((_, _), a), r) => r.userId == a.id && r.by == lift(by)})
        .map({case (((gi, g), a), r) => (gi, a, r, g)})
        .sortBy({ case (gi, _, _, _) => gi.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (gi, a, r, g) => Invitation(gi, a, r, g, gi.id.value)}))
  }

  def own(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Invitations]
        .filter(_.userId == lift(userId))
        .filter(_.channelId   == lift(channelId))
        .filter(_.by        == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def delete(id: InvitationId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Invitations]
        .filter(_.userId == lift(by))
        .filter(_.id == lift(id))
        .delete
    }
    run(q).map(_ => ())

  }

}
