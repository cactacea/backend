package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType}
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class ChannelsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(sessionId: SessionId): Future[ChannelId] = {
    val organizedAt = System.currentTimeMillis()
    val name: Option[String] = None
    val by = sessionId.userId
    val q = quote {
      query[Channels].insert(
        _.name              -> lift(name),
        _.invitationOnly    -> true,
        _.authorityType     -> lift(ChannelAuthorityType.member),
        _.privacyType       -> lift(ChannelPrivacyType.everyone),
        _.directMessage     -> true,
        _.userCount      -> 0L,
        _.by                -> lift(by),
        _.organizedAt       -> lift(organizedAt)
      ).returning(_.id)
    }
    run(q)
  }


  def create(name: Option[String],
             invitationOnly: Boolean,
             privacyType: ChannelPrivacyType,
             authority: ChannelAuthorityType,
             sessionId: SessionId): Future[ChannelId] = {

    val organizedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[Channels].insert(
        _.name                -> lift(name),
        _.invitationOnly      -> lift(invitationOnly),
        _.authorityType       -> lift(authority),
        _.privacyType         -> lift(privacyType),
        _.directMessage       -> false,
        _.userCount        -> 0L,
        _.by                  -> lift(by),
        _.organizedAt         -> lift(organizedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def delete(channelId: ChannelId): Future[Unit] = {
    val q = quote {
      query[Channels]
        .filter(_.id == lift(channelId))
        .delete
    }
    run(q).map(_ => ())
  }

  def exists(channelId: ChannelId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Channels]
        .filter(_.id == lift(channelId))
        .filter(g => query[Blocks].filter(b => b.userId == lift(by) && b.by == g.by).isEmpty)
        .nonEmpty
    }
    run(q)
  }

  def find(channelId: ChannelId, sessionId: SessionId): Future[Option[Channel]] = {
    val by = sessionId.userId
    val q = quote {
      query[Channels]
        .filter(_.id == lift(channelId))
        .filter(g => query[Blocks].filter(b => b.userId == lift(by) && b.by == g.by).isEmpty)
    }
    run(q).map(_.headOption.map(Channel(_)))
  }

  def findUserCount(id: ChannelId): Future[Long] = {
    val q = quote {
      query[Channels]
        .filter(_.id == lift(id))
        .map(_.userCount)
    }
    run(q).map(_.headOption.getOrElse(0L))
  }

  def update(channelId: ChannelId,
             name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: ChannelPrivacyType,
             authority: ChannelAuthorityType,
             sessionId: SessionId): Future[Unit] = {

    val by = sessionId.userId
    val q = quote {
      query[Channels]
        .filter(_.id == lift(channelId))
        .filter(_.by == lift(by))
        .update(
        _.name                -> lift(name),
        _.invitationOnly      -> lift(byInvitationOnly),
        _.privacyType         -> lift(privacyType),
        _.authorityType       -> lift(authority)
      )
    }
    run(q).map(_ => ())
  }

}

