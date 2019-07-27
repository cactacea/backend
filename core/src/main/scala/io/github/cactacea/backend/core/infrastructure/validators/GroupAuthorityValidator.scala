package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Groups, Relationships}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class GroupAuthorityValidator @Inject()(
                                   db: DatabaseService) {

  import db._

  def hasFindMembersAuthority(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      r <- findRelationship(sessionId.toAccountId, g.by.toSessionId)
      _ <- mustHaveJoinAuthority(g, r, sessionId)
    } yield (())
  }

  def hasAddMembersAuthority(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      r <- findRelationship(accountId, g.by.toSessionId)
      _ <- mustNotDirectMessageGroup(g.directMessage)
      _ <- mustHaveJoinAuthority(g, r, accountId.toSessionId)
      _ <- mustHaveInviteAuthority(g, sessionId)
    } yield (())
  }

  def hasRemoveMembersAuthority(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      _ <- mustNotDirectMessageGroup(g.directMessage)
      _ <- mustHaveInviteAuthority(g, sessionId)
    } yield (())

  }

  def hasUpdateAuthority(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      _ <- mustNotDirectMessageGroup(g.directMessage)
      _ <- mustHaveInviteAuthority(g, sessionId)
    } yield (())
  }

  def hasJoinAuthority(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      r <- findRelationship(sessionId.toAccountId, g.by.toSessionId)
      _ <- mustNotDirectMessageGroup(g.directMessage)
      _ <- mustNotInvitationOnlyGroup(g.invitationOnly)
      _ <- mustHaveJoinAuthority(g, r, sessionId)
    } yield (())
  }

  def hasInviteAuthority(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      r <- findRelationship(accountId, g.by.toSessionId)
      _ <- mustNotDirectMessageGroup(g.directMessage)
      _ <- mustHaveJoinAuthority(g, r, accountId.toSessionId)
      _ <- mustHaveInviteAuthority(g, sessionId)
    } yield (())
  }


  private def findGroup(groupId: GroupId): Future[Groups] = {
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
    }
    run(q).flatMap(_.headOption match {
      case Some(g) => Future.value(g)
      case None => Future.exception(CactaceaException(GroupNotFound))
    })
  }

  private def findRelationship(accountId: AccountId, sessionId: SessionId): Future[Option[Relationships]] = {
    val by = sessionId.toAccountId
    val select = quote {
      for {
        r <- query[Relationships]
          .filter(_.accountId == lift(accountId))
          .filter(_.by     == lift(by))
      } yield (r)
    }
    run(select).map(_.headOption)
  }

  private def mustHaveJoinAuthority(g: Groups, r: Option[Relationships], sessionId: SessionId): Future[Unit] = {
    val follow = r.fold(false)(_.follow)
    val follower = r.fold(false)(_.isFollower)
    val friend = r.fold(false)(_.isFriend)
    if (g.by.toSessionId == sessionId) {
      Future.Unit
    } else if (g.privacyType == GroupPrivacyType.follows && (follow || friend)) {
      Future.Unit
    } else if (g.privacyType == GroupPrivacyType.followers && (follower || friend)) {
      Future.Unit
    } else if (g.privacyType == GroupPrivacyType.friends && friend) {
      Future.Unit
    } else if (g.privacyType == GroupPrivacyType.everyone) {
      Future.Unit
    } else {
      Future.exception(CactaceaException(AuthorityNotFound))
    }
  }

  private def mustHaveInviteAuthority(g: Groups, sessionId: SessionId): Future[Unit] = {
    if (sessionId.toAccountId == g.by) {
      Future.Unit
    } else if (g.authorityType == GroupAuthorityType.organizer) {
      Future.exception(CactaceaException(AuthorityNotFound))
    } else {
      Future.Unit
    }
  }

  private def mustNotInvitationOnlyGroup(invitationOnly: Boolean): Future[Unit] = {
    invitationOnly match {
      case true =>
        Future.exception(CactaceaException(InvitationGroupFound))
      case false =>
        Future.Unit
    }
  }

  private def mustNotDirectMessageGroup(directMessage: Boolean): Future[Unit] = {
    directMessage match {
      case true =>
        Future.exception(CactaceaException(AuthorityNotFound))
      case false =>
        Future.Unit
    }
  }


}
