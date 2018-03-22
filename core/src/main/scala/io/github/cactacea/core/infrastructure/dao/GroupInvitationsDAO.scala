package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.application.services.TimeService
import io.github.cactacea.core.domain.enums.{GroupInvitationStatusType, GroupPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class GroupInvitationsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _
  @Inject private var timeService: TimeService = _

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    for {
      id <- identifyService.generate().map(GroupInvitationId(_))
      _ <- insert(id, accountId, groupId, sessionId)
    } yield (id)
  }

  private def insert(id: GroupInvitationId, accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Long] = {
    val invitedAt = timeService.nanoTime()
    val by = sessionId.toAccountId
    val q = quote {
      query[GroupInvitations]
        .insert(
          _.id            -> lift(id),
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.groupId       -> lift(groupId),
          _.notified      -> false,
          _.invitationStatus  -> lift(GroupInvitationStatusType.noResponded),
          _.invitedAt     -> lift(invitedAt)
        )
    }
    run(q)
  }

  def exist(accountId: AccountId, groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.accountId == lift(accountId))
        .filter(_.groupId   == lift(groupId))
        .take(lift(1))
        .size
    }
    run(q).map(_ == 1)
  }

  def exist(id: GroupInvitationId): Future[Boolean] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id == lift(id))
        .size
    }
    run(q).map(_ == 1)
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
      query[GroupInvitations].filter({ gi => gi.accountId == lift(by) && (infix"gi.id < ${lift(s)}".as[Boolean] || lift(s) == -1L)})
        .join(query[Groups]).on((gi, g) => g.id == gi.groupId)
        .join(query[Accounts]).on({ case ((gi, g), a) => a.id == gi.by})
        .leftJoin(query[Relationships]).on({ case (((_, g), a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({case (((gi, g), a), r) => (gi, a, r, g)})
        .sortBy(_._1.id)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def deleteByGroupId(groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.groupId       == lift(groupId))
        .filter(_.invitationStatus  == lift(GroupInvitationStatusType.noResponded))
        .delete
    }
    run(q).map(_ >= 0)
  }

  def delete(id: GroupInvitationId): Future[Boolean] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id       == lift(id))
        .delete
    }
    run(q).map(_ >= 0)
  }

  def delete(accountId: AccountId, groupPrivacyType: GroupPrivacyType, sessionId: SessionId): Future[Boolean] = {

    // http://getquill.io/

    // Abouts group_invitations
    // FIX ME : Generate invalid sql statement

    val by = sessionId.toAccountId
    val q = quote {
      query[GroupInvitations]
        .filter(group_invitations => group_invitations.accountId        == lift(accountId))
        .filter(group_invitations => group_invitations.invitationStatus  == lift(GroupInvitationStatusType.noResponded))
        .filter(group_invitations => (
          query[Groups]
            .filter(_.id          == group_invitations.groupId)
            .filter(_.by          == lift(by))
            .filter(_.privacyType == lift(groupPrivacyType))
            .nonEmpty)
        )
        .delete
    }
    run(q).map(_ >= 0)

  }

  def update(accountId: AccountId, groupId: GroupId, invitationStatus: GroupInvitationStatusType): Future[Boolean] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId == lift(accountId))
        .update(_.invitationStatus -> lift(invitationStatus))
    }
    run(q).map(_ == 1)
  }

  def update(id: GroupInvitationId, invitationStatus: GroupInvitationStatusType, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[GroupInvitations]
        .filter(_.accountId == lift(by))
        .filter(_.id == lift(id))
        .update(_.invitationStatus -> lift(invitationStatus))
    }
    run(q).map(_ == 1)

  }

  def find(id: GroupInvitationId): Future[Option[GroupInvitations]] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id == lift(id))
    }
    run(q).map(_.headOption)
  }

  def updateNotified(id: GroupInvitationId, notified: Boolean = true): Future[Boolean] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id == lift(id))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ == 1)
  }

}
