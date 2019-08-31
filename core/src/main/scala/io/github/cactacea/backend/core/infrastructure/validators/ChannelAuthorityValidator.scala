package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Channels, Relationships}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class ChannelAuthorityValidator @Inject()(db: DatabaseService) {

  import db._

  def hasFindMembersAuthority(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findChannel(channelId)
      r <- findRelationship(sessionId.toAccountId, g.by.toSessionId)
      _ <- mustHaveJoinAuthority(g, r, sessionId)
    } yield (())
  }

  def hasAddMembersAuthority(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findChannel(channelId)
      r <- findRelationship(accountId, g.by.toSessionId)
      _ <- mustNotDirectMessageChannel(g.directMessage)
      _ <- mustHaveJoinAuthority(g, r, accountId.toSessionId)
      _ <- mustHaveInviteAuthority(g, sessionId)
    } yield (())
  }

  def hasRemoveMembersAuthority(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findChannel(channelId)
      _ <- mustNotDirectMessageChannel(g.directMessage)
      _ <- mustHaveInviteAuthority(g, sessionId)
    } yield (())

  }

  def hasUpdateAuthority(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findChannel(channelId)
      _ <- mustNotDirectMessageChannel(g.directMessage)
      _ <- mustHaveInviteAuthority(g, sessionId)
    } yield (())
  }

  def hasJoinAuthority(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findChannel(channelId)
      r <- findRelationship(sessionId.toAccountId, g.by.toSessionId)
      _ <- mustNotDirectMessageChannel(g.directMessage)
      _ <- mustNotInvitationOnlyChannel(g.invitationOnly)
      _ <- mustHaveJoinAuthority(g, r, sessionId)
    } yield (())
  }

  def hasInviteAuthority(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findChannel(channelId)
      r <- findRelationship(accountId, g.by.toSessionId)
      _ <- mustNotDirectMessageChannel(g.directMessage)
      _ <- mustHaveJoinAuthority(g, r, accountId.toSessionId)
      _ <- mustHaveInviteAuthority(g, sessionId)
    } yield (())
  }


  private def findChannel(channelId: ChannelId): Future[Channels] = {
    val q = quote {
      query[Channels]
        .filter(_.id == lift(channelId))
    }
    run(q).flatMap(_.headOption match {
      case Some(g) => Future.value(g)
      case None => Future.exception(CactaceaException(ChannelNotFound))
    })
  }

  private def findRelationship(accountId: AccountId, sessionId: SessionId): Future[Option[Relationships]] = {
    val by = sessionId.toAccountId
    val q = quote {
      for {
        r <- query[Relationships]
          .filter(_.accountId == lift(accountId))
          .filter(_.by     == lift(by))
      } yield (r)
    }
    run(q).map(_.headOption)
  }

  private def mustHaveJoinAuthority(g: Channels, r: Option[Relationships], sessionId: SessionId): Future[Unit] = {
    val follow = r.fold(false)(_.follow)
    val follower = r.fold(false)(_.isFollower)
    val friend = r.fold(false)(_.isFriend)
    if (g.by.toSessionId == sessionId) {
      Future.Unit
    } else if (g.privacyType == ChannelPrivacyType.follows && (follow || friend)) {
      Future.Unit
    } else if (g.privacyType == ChannelPrivacyType.followers && (follower || friend)) {
      Future.Unit
    } else if (g.privacyType == ChannelPrivacyType.friends && friend) {
      Future.Unit
    } else if (g.privacyType == ChannelPrivacyType.everyone) {
      Future.Unit
    } else {
      Future.exception(CactaceaException(AuthorityNotFound))
    }
  }

  private def mustHaveInviteAuthority(g: Channels, sessionId: SessionId): Future[Unit] = {
    if (sessionId.toAccountId == g.by) {
      Future.Unit
    } else if (g.authorityType == ChannelAuthorityType.organizer) {
      Future.exception(CactaceaException(AuthorityNotFound))
    } else {
      Future.Unit
    }
  }

  private def mustNotInvitationOnlyChannel(invitationOnly: Boolean): Future[Unit] = {
    invitationOnly match {
      case true =>
        Future.exception(CactaceaException(InvitationChannelFound))
      case false =>
        Future.Unit
    }
  }

  private def mustNotDirectMessageChannel(directMessage: Boolean): Future[Unit] = {
    directMessage match {
      case true =>
        Future.exception(CactaceaException(AuthorityNotFound))
      case false =>
        Future.Unit
    }
  }


}
