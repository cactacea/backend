package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class GroupsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(sessionId: SessionId): Future[GroupId] = {
    val organizedAt = System.currentTimeMillis()
    val name: Option[String] = None
    val by = sessionId.toAccountId
    val q = quote {
      query[Groups].insert(
        _.name              -> lift(name),
        _.invitationOnly    -> true,
        _.authorityType     -> lift(GroupAuthorityType.member),
        _.privacyType       -> lift(GroupPrivacyType.everyone),
        _.directMessage     -> true,
        _.accountCount      -> 0L,
        _.by                -> lift(by),
        _.organizedAt       -> lift(organizedAt)
      ).returning(_.id)
    }
    run(q)
  }


  def create(name: Option[String],
             invitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[GroupId] = {

    val organizedAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Groups].insert(
        _.name                -> lift(name),
        _.invitationOnly      -> lift(invitationOnly),
        _.authorityType       -> lift(authority),
        _.privacyType         -> lift(privacyType),
        _.directMessage       -> false,
        _.accountCount        -> 0L,
        _.by                  -> lift(by),
        _.organizedAt         -> lift(organizedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def delete(groupId: GroupId): Future[Unit] = {
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .delete
    }
    run(q).map(_ => ())
  }

  def exists(groupId: GroupId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .filter(g => query[Blocks].filter(b => b.accountId == lift(by) && b.by == g.by).isEmpty)
        .nonEmpty
    }
    run(q)
  }

  def isOrganizer(groupId: GroupId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .filter(_.by == lift(by))
        .filter(_.accountCount > 1)
        .nonEmpty
    }
    run(q)
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Option[Group]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .filter(g => query[Blocks].filter(b => b.accountId == lift(by) && b.by == g.by).isEmpty)
    }
    run(q).map(_.headOption.map(Group(_)))
  }

  def findAccountCount(id: GroupId): Future[Long] = {
    val q = quote {
      query[Groups]
        .filter(_.id == lift(id))
        .map(_.accountCount)
    }
    run(q).map(_.headOption.getOrElse(0L))
  }

  def update(groupId: GroupId,
             name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[Unit] = {

    val by = sessionId.toAccountId
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
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

