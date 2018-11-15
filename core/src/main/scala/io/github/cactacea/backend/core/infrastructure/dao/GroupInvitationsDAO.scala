package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.{GroupInvitationStatusType, GroupPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class GroupInvitationsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    for {
      id <- insert(accountId, groupId, sessionId)
    } yield (id)
  }

  private def insert(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    val invitedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[GroupInvitations]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.groupId           -> lift(groupId),
          _.notified          -> false,
          _.invitationStatus  -> lift(GroupInvitationStatusType.noResponded),
          _.invitedAt         -> lift(invitedAt)
        ).returning(_.id)
    }
    run(q)
  }

  def exist(accountId: AccountId, groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.accountId == lift(accountId))
        .filter(_.groupId   == lift(groupId))
        .nonEmpty
    }
    run(q)
  }

  def exist(id: GroupInvitationId): Future[Boolean] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id == lift(id))
        .nonEmpty
    }
    run(q)
  }

  def find(id: GroupInvitationId, sessionId: SessionId): Future[Option[GroupInvitations]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[GroupInvitations]
        .filter(_.id        == lift(id))
        .filter(_.accountId == lift(accountId))
    }
    run(q).map(_.headOption)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(GroupInvitations, Accounts, Option[Relationships], Groups)]] = {
    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[GroupInvitations].filter({ gi => gi.accountId == lift(by) && (gi.id < lift(s) || lift(s) == -1L)})
        .join(query[Groups]).on((gi, g) => g.id == gi.groupId)
        .join(query[Accounts]).on({ case ((gi, g), a) => a.id == gi.by})
        .leftJoin(query[Relationships]).on({ case (((_, g), a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({case (((gi, g), a), r) => (gi, a, r, g)})
        .sortBy(_._1.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def deleteByGroupId(groupId: GroupId): Future[Unit] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.groupId       == lift(groupId))
        .filter(_.invitationStatus  == lift(GroupInvitationStatusType.noResponded))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def delete(id: GroupInvitationId): Future[Unit] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id       == lift(id))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def delete(accountId: AccountId, groupPrivacyType: GroupPrivacyType, sessionId: SessionId): Future[Unit] = {

    // http://getquill.io/

    // Abouts group_invitations
    // FIX ME : Generate invalid sql statement

    val by = sessionId.toAccountId
    val q = quote {
      query[GroupInvitations]
        .filter(group_invitations => group_invitations.accountId        == lift(accountId))
        .filter(group_invitations => group_invitations.invitationStatus  == lift(GroupInvitationStatusType.noResponded))
        .filter(group_invitations =>
          query[Groups]
            .filter(_.id          == group_invitations.groupId)
            .filter(_.by          == lift(by))
            .filter(_.privacyType == lift(groupPrivacyType))
            .nonEmpty
        )
        .delete
    }
    run(q).map(_ => Unit)

  }

  def update(accountId: AccountId, groupId: GroupId, invitationStatus: GroupInvitationStatusType): Future[Unit] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId == lift(accountId))
        .update(_.invitationStatus -> lift(invitationStatus))
    }
    run(q).map(_ => Unit)
  }

  def update(id: GroupInvitationId, invitationStatus: GroupInvitationStatusType, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[GroupInvitations]
        .filter(_.accountId == lift(by))
        .filter(_.id == lift(id))
        .update(_.invitationStatus -> lift(invitationStatus))
    }
    run(q).map(_ => Unit)

  }

  def find(id: GroupInvitationId): Future[Option[GroupInvitations]] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id == lift(id))
    }
    run(q).map(_.headOption)
  }

  def updateNotified(id: GroupInvitationId, notified: Boolean = true): Future[Unit] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id == lift(id))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ => Unit)
  }

}
