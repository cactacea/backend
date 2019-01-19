package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{GroupInvitationStatusType, GroupPrivacyType}
import io.github.cactacea.backend.core.domain.models.GroupInvitation
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.infrastructure.results.PushNotifications
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyInvited, GroupInvitationNotFound}

@Singleton
class GroupInvitationsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    for {
      id <- insert(accountId, groupId, sessionId)
    } yield (id)
  }

  private def insert(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    val invitedAt = System.currentTimeMillis()
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

  def findExist(accountId: AccountId, groupId: GroupId): Future[Boolean] = {
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

  def find(since: Option[Long],
           offset: Int,
           count: Int, sessionId: SessionId): Future[List[GroupInvitation]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[GroupInvitations]
        .filter(gi => gi.accountId == lift(by))
        .filter(gi => lift(since).forall(gi.id < _))
        .join(query[Groups]).on((gi, g) => g.id == gi.groupId)
        .join(query[Accounts]).on({ case ((gi, _), a) => a.id == gi.by})
        .leftJoin(query[Relationships]).on({ case (((_, _), a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({case (((gi, g), a), r) => (gi, a, r, g)})
        .sortBy({ case (gi, _, _, _) => gi.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (gi, a, r, g) => GroupInvitation(gi, a, r, g, gi.id.value)}))

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

  def update(groupId: GroupId, accountId: AccountId, invitationStatus: GroupInvitationStatusType): Future[Unit] = {
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

  // Mobile Push

  def updatePushNotifications(id: GroupInvitationId, notified: Boolean = true): Future[Unit] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id == lift(id))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ => Unit)
  }

  def findPushNotifications(id: GroupInvitationId): Future[List[PushNotifications]] = {
    val q = quote {
      query[GroupInvitations].filter(g => g.id == lift(id) && g.notified == false
        && query[PushNotificationSettings].filter(p => p.accountId == g.accountId && p.groupInvitation == true).nonEmpty)
        .leftJoin(query[Relationships]).on((g, r) => r.accountId == g.by && r.by == g.accountId)
        .join(query[Accounts]).on({ case ((g, _), a) => a.id == g.by})
        .join(query[Devices]).on({ case (((g, _), _), d) => d.accountId == g.accountId && d.pushToken.isDefined})
        .map({ case (((g, r), a), d) => (a.displayName, r.flatMap(_.displayName), g.accountId, d.pushToken) })
        .distinct
    }
    run(q).map(_.map({ case (displayName, editedDisplayName, accountId, pushToken) => {
      val name = editedDisplayName.getOrElse(displayName)
      val token = pushToken.get
      PushNotifications(accountId, name, token, showContent = false)
    }}))
  }


  // Validators

  def validateNotExist(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    findExist(accountId, groupId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyInvited))
      case false =>
        Future.Unit
    })
  }

  def validateExist(groupInvitationId: GroupInvitationId, sessionId: SessionId): Future[GroupInvitations] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[GroupInvitations]
        .filter(_.id        == lift(groupInvitationId))
        .filter(_.accountId == lift(accountId))
    }
    run(q).flatMap(_.headOption match {
      case None =>
        Future.exception(CactaceaException(GroupInvitationNotFound))
      case Some(i) =>
        Future.value(i)
    })
  }


}
