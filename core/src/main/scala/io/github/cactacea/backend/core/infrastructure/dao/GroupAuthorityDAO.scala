package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{AccountGroups, Groups, Relationships}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaError

@Singleton
class GroupAuthorityDAO @Inject()(db: DatabaseService) {

  import db._

  def hasJoinAndManagingAuthority(g: Groups, accountId: AccountId, sessionId: SessionId): Future[Either[CactaceaError, Boolean]] = {
    _hasManagingAuthority(sessionId.toAccountId, g).flatMap(_ match {
      case true =>
        _findRelationship(accountId, g.by.toSessionId).flatMap(r =>
          _hasJoinAuthority(g, r, accountId.toSessionId).flatMap(_ match {
            case false =>
              Future.value(Left(OperationNotAllowed))
            case true =>
              Future.value(Right(true))
          })
        )
      case false =>
        Future.value(Left(AuthorityNotFound))
    })
  }

  def hasManagingAuthority(g: Groups, sessionId: SessionId): Future[Either[CactaceaError, Boolean]] = {
    _hasManagingAuthority(sessionId.toAccountId, g).flatMap(_ match {
      case true =>
        Future.value(Right(true))
      case false =>
        Future.value(Left(AuthorityNotFound))
    })
  }

  def hasJoinAuthority(g: Groups, sessionId: SessionId): Future[Either[CactaceaError, Boolean]] = {
    _findRelationship(sessionId.toAccountId, g.by.toSessionId).flatMap(r =>
      _hasJoinAuthority(g, r, sessionId).flatMap(_ match {
        case false =>
          Future.value(Left(AuthorityNotFound))
        case true =>
          Future.value(Right(true))
      })
    )
  }

  private def _hasManagingAuthority(accountId: AccountId, g: Groups): Future[Boolean] = {
    if (accountId == g.by) {
      Future.True
    } else if (g.authorityType == GroupAuthorityType.owner && g.by != accountId) {
      Future.value(false)
    } else {
      _exist(g.id, accountId)
    }
  }

  private def _hasJoinAuthority(g: Groups, r: Option[Relationships], sessionId: SessionId): Future[Boolean] = {
    val follow = r.fold(false)(_.follow)
    val follower = r.fold(false)(_.follower)
    val friend = r.fold(false)(_.friend)
    if (g.by.toSessionId == sessionId) {
      Future.True
    } else if (g.privacyType == GroupPrivacyType.follows && follow) {
      Future.True
    } else if (g.privacyType == GroupPrivacyType.followers && follower) {
      Future.True
    } else if (g.privacyType == GroupPrivacyType.friends && friend) {
      Future.True
    } else if (g.privacyType == GroupPrivacyType.everyone) {
      Future.True
    } else {
      Future.False
    }
  }

  private def _exist(groupId: GroupId, accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.accountId   == lift(accountId))
        .filter(_.groupId     == lift(groupId))
        .nonEmpty
    }
    run(q)
  }

  private def _findRelationship(accountId: AccountId, sessionId: SessionId): Future[Option[Relationships]] = {
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

}
