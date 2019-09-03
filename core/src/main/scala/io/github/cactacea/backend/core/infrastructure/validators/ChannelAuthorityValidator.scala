package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType}
import io.github.cactacea.backend.core.infrastructure.dao.ChannelUsersDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.models.{Channels, Relationships, UserChannels}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class ChannelAuthorityValidator @Inject()(db: DatabaseService, channelUsersDAO: ChannelUsersDAO) {

  import db._

  def canFindMembers(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      c <- findChannel(channelId)
      r <- findRelationship(sessionId.userId, c.by.sessionId)
      _ <- mustHaveJoinAuthority(c, r, sessionId)
    } yield (())
  }

  def canAddMember(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      c <- findChannel(channelId)
      u <- findUserChannel(channelId, sessionId)
      r <- findRelationship(userId, c.by.sessionId)
      _ <- mustNotDirectMessageChannel(c.directMessage)
      _ <- mustHaveJoinAuthority(c, r, userId.sessionId)
      _ <- mustHaveInviteAuthority(c, u)
    } yield (())
  }

  def canLeaveMember(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      c <- findChannel(channelId)
      u <- findUserChannel(channelId, sessionId)
      _ <- mustNotDirectMessageChannel(c.directMessage)
      _ <- mustHaveInviteAuthority(c, u)
      _ <- mustNotLastOrganizer(c, sessionId)
    } yield (())
  }

  def canUpdate(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      c <- findChannel(channelId)
      u <- findUserChannel(channelId, sessionId)
      _ <- mustNotDirectMessageChannel(c.directMessage)
      _ <- mustHaveInviteAuthority(c, u)
    } yield (())
  }

  def canJoin(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      c <- findChannel(channelId)
      r <- findRelationship(sessionId.userId, c.by.sessionId)
      _ <- mustNotDirectMessageChannel(c.directMessage)
      _ <- mustNotInvitationOnlyChannel(c.invitationOnly)
      _ <- mustHaveJoinAuthority(c, r, sessionId)
    } yield (())
  }

  def canInvite(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      c <- findChannel(channelId)
      u <- findUserChannel(channelId, sessionId)
      r <- findRelationship(userId, c.by.sessionId)
      _ <- mustNotDirectMessageChannel(c.directMessage)
      _ <- mustHaveJoinAuthority(c, r, userId.sessionId)
      _ <- mustHaveInviteAuthority(c, u)
    } yield (())
  }

  def canLeave(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      c <- findChannel(channelId)
      _ <- mustNotLastOrganizer(c, sessionId)
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

  private def findUserChannel(channelId: ChannelId, sessionId: SessionId): Future[UserChannels] = {
    val userId = sessionId.userId
    val q = quote {
      query[UserChannels]
        .filter(_.channelId == lift(channelId))
        .filter(_.userId == lift(userId))
    }
    run(q).flatMap(_.headOption match {
      case Some(g) => Future.value(g)
      case None => Future.exception(CactaceaException(ChannelNotFound))
    })
  }

  private def findRelationship(userId: UserId, sessionId: SessionId): Future[Option[Relationships]] = {
    val by = sessionId.userId
    val q = quote {
      for {
        r <- query[Relationships]
          .filter(_.userId == lift(userId))
          .filter(_.by     == lift(by))
      } yield (r)
    }
    run(q).map(_.headOption)
  }

  private def mustHaveJoinAuthority(g: Channels, r: Option[Relationships], sessionId: SessionId): Future[Unit] = {
    val follow = r.fold(false)(_.follow)
    val follower = r.fold(false)(_.isFollower)
    val friend = r.fold(false)(_.isFriend)


    if (g.by.sessionId == sessionId) {
      Future.Unit
    } else {
      g.privacyType match {
        case ChannelPrivacyType.follows if (follow || friend) =>
          Future.Unit
        case ChannelPrivacyType.followers if (follower || friend) =>
          Future.Unit
        case ChannelPrivacyType.friends if friend =>
          Future.Unit
        case ChannelPrivacyType.everyone =>
          Future.Unit
        case _ =>
          Future.exception(CactaceaException(AuthorityNotFound))
      }
    }
  }

  private def mustHaveInviteAuthority(g: Channels, u: UserChannels): Future[Unit] = {
    g.authorityType match {
      case ChannelAuthorityType.organizer =>
        u.authorityType match {
          case ChannelAuthorityType.organizer =>
            Future.Unit
          case ChannelAuthorityType.member =>
            Future.exception(CactaceaException(AuthorityNotFound))
        }
      case ChannelAuthorityType.member  =>
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

  private def mustNotLastOrganizer(g: Channels, sessionId: SessionId): Future[Unit] = {
    if (g.userCount == 1) {
      Future.Unit
    } else {
      g.authorityType match {
        case ChannelAuthorityType.organizer =>
          channelUsersDAO.isLastOrganizer(g.id, sessionId).flatMap(_ match {
            case true =>
              Future.exception(CactaceaException(OrganizerCanNotLeave))
            case false =>
              Future.Unit
          })
        case ChannelAuthorityType.member =>
          Future.Unit
      }
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
