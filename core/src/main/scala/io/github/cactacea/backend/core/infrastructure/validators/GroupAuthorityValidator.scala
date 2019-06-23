package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{AccountGroups, Groups, Relationships}
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
      _ <- canJoin(g, r, sessionId)
    } yield (())
  }

  def hasAddMembersAuthority(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      r <- findRelationship(accountId, g.by.toSessionId)
      _ <- canJoin(g, r, accountId.toSessionId)
      _ <- canManage(g, sessionId)
    } yield (())
  }

  def hasRemoveMembersAuthority(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      _ <- canManage(g, sessionId)
    } yield (())

  }

  def hasUpdateAuthority(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      _ <- notDirectMessageGroup(g)
      _ <- canManage(g, sessionId)
    } yield (())
  }

  def hasJoinAuthority(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      r <- findRelationship(sessionId.toAccountId, g.by.toSessionId)
      _ <- notInvitationOnlyGroup(g)
      _ <- canJoin(g, r, sessionId)
    } yield (())
  }

  def hasInviteMembersAuthority(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      g <- findGroup(groupId)
      r <- findRelationship(accountId, g.by.toSessionId)
      _ <- canJoin(g, r, accountId.toSessionId)
      _ <- canManage(g, sessionId)
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

  private def canJoin(g: Groups, r: Option[Relationships], sessionId: SessionId): Future[Unit] = {
    val follow = r.fold(false)(_.follow)
    val follower = r.fold(false)(_.isFollower)
    val friend = r.fold(false)(_.isFriend)
    if (g.by.toSessionId == sessionId) {
      Future.Unit
    } else if (g.privacyType == GroupPrivacyType.follows && follow) {
      Future.Unit
    } else if (g.privacyType == GroupPrivacyType.followers && follower) {
      Future.Unit
    } else if (g.privacyType == GroupPrivacyType.friends && friend) {
      Future.Unit
    } else if (g.privacyType == GroupPrivacyType.everyone) {
      Future.Unit
    } else {
      Future.exception(CactaceaException(AuthorityNotFound))
    }
  }

  private def canManage(g: Groups, sessionId: SessionId): Future[Unit] = {
    if (sessionId.toAccountId == g.by) {
      Future.Unit
    } else if (g.authorityType == GroupAuthorityType.owner) {
      Future.exception(CactaceaException(AuthorityNotFound))
    } else {
      findAccountGroupExist(g.id, sessionId.toAccountId).flatMap(_ match {
        case true =>
          Future.Unit
        case false =>
          Future.exception(CactaceaException(AuthorityNotFound))
      })
    }
  }

  private def findAccountGroupExist(groupId: GroupId, accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId     == lift(groupId))
        .filter(_.accountId   == lift(accountId))
        .nonEmpty
    }
    run(q)
  }


  private def notInvitationOnlyGroup(g: Groups): Future[Unit] = {
    g.invitationOnly match {
      case true =>
        Future.exception(CactaceaException(InnvitationOnlyGroup))
      case false =>
        Future.Unit
    }
  }

  private def notDirectMessageGroup(g: Groups): Future[Unit] = {
    g.directMessage match {
      case true =>
        Future.exception(CactaceaException(DirectMessageGroupCanNotUpdated))
      case false =>
        Future.Unit
    }
  }


}
