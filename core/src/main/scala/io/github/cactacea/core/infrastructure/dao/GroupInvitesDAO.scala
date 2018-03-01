package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.{GroupInviteStatusType, GroupPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class GroupInvitesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInviteId] = {
    for {
      id <- identifiesDAO.create().map(GroupInviteId(_))
      _ <- insert(id, accountId, groupId, sessionId)
    } yield (id)
  }

  private def insert(id: GroupInviteId, accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Long] = {
    val invitedAt = System.nanoTime()
    val by = sessionId.toAccountId
    val q = quote {
      query[GroupInvites]
        .insert(
          _.id            -> lift(id),
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.groupId       -> lift(groupId),
          _.notified      -> lift(false),
          _.inviteStatus  -> lift(GroupInviteStatusType.noresponsed.toValue),
          _.invitedAt     -> lift(invitedAt)
        )
    }
    run(q)
  }

  def exist(accountId: AccountId, groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[GroupInvites]
        .filter(_.accountId == lift(accountId))
        .filter(_.groupId   == lift(groupId))
        .take(lift(1))
        .size
    }
    run(q).map(_ == 1)
  }

  def exist(groupInviteId: GroupInviteId): Future[Boolean] = {
    val q = quote {
      query[GroupInvites]
        .filter(_.id == lift(groupInviteId))
        .size
    }
    run(q).map(_ == 1)
  }

  def find(groupInviteId: GroupInviteId, sessionId: SessionId): Future[Option[GroupInvites]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[GroupInvites]
        .filter(_.id        == lift(groupInviteId))
        .filter(_.accountId == lift(accountId))
    }
    run(q).map(_.headOption)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(GroupInvites, Accounts, Option[Relationships], Groups, Option[Messages], Option[AccountMessages], Option[Accounts], Option[Relationships])]] = {
    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[GroupInvites].filter({gi => gi.accountId == lift(by) && gi.invitedAt < lift(s)})
        .sortBy(_.invitedAt)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
        .join(query[Groups]).on((gi, g) => g.id == gi.groupId)
        .join(query[Accounts]).on({ case ((gi, g), a) => a.id == gi.by})
        .leftJoin(query[Relationships]).on({ case (((_, g), a), r) => r.accountId == a.id && r.by == lift(by)})
    }
    run(q).map(_.map({case (((gi, g), a), r) => (gi, a, r, g, None, None, None, None)}))

  }

  def deleteByGroupId(groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[GroupInvites]
        .filter(_.groupId       == lift(groupId))
        .filter(_.inviteStatus  == lift(GroupInviteStatusType.noresponsed.toValue))
        .delete
    }
    run(q).map(_ >= 0)
  }

  def delete(groupInviteId: GroupInviteId): Future[Boolean] = {
    val q = quote {
      query[GroupInvites]
        .filter(_.id       == lift(groupInviteId))
        .delete
    }
    run(q).map(_ >= 0)
  }

  def delete(accountId: AccountId, groupPrivacyType: GroupPrivacyType, sessionId: SessionId): Future[Boolean] = {

    // http://getquill.io/

    // Abouts group_invites
    // FIX ME : Generate invalid sql statement

    val by = sessionId.toAccountId
    val q = quote {
      query[GroupInvites]
        .filter(group_invites => group_invites.accountId        == lift(accountId))
        .filter(group_invites => group_invites.inviteStatus  == lift(GroupInviteStatusType.noresponsed.toValue))
        .filter(group_invites => (
          query[Groups]
            .filter(_.id          == group_invites.groupId)
            .filter(_.by          == lift(by))
            .filter(_.privacyType == lift(groupPrivacyType.toValue))
            .nonEmpty)
        )
        .delete
    }
    run(q).map(_ >= 0)

  }

  def update(accountId: AccountId, groupId: GroupId, inviteStatus: GroupInviteStatusType): Future[Boolean] = {
    val q = quote {
      query[GroupInvites]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId == lift(accountId))
        .update(_.inviteStatus -> lift(inviteStatus.toValue))
    }
    run(q).map(_ == 1)
  }

  def update(inviteId: GroupInviteId, inviteStatus: GroupInviteStatusType, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[GroupInvites]
        .filter(_.accountId == lift(by))
        .filter(_.id == lift(inviteId))
        .update(_.inviteStatus -> lift(inviteStatus.toValue))
    }
    run(q).map(_ == 1)

  }

  def find(groupInviteId: GroupInviteId): Future[Option[GroupInvites]] = {
    val q = quote {
      query[GroupInvites]
        .filter(_.id == lift(groupInviteId))
    }
    run(q).map(_.headOption)
  }

  def findUnNotified(groupInviteId: GroupInviteId): Future[Option[GroupInvites]] = {
    val q = quote {
      query[GroupInvites]
        .filter(_.id == lift(groupInviteId))
        .filter(_.notified == lift(false))
    }
    run(q).map(_.headOption)
  }

  def updateNotified(groupInviteId: GroupInviteId, notified: Boolean = true): Future[Boolean] = {
    val q = quote {
      query[GroupInvites]
        .filter(_.id == lift(groupInviteId))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ == 1)
  }

}
