package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AccountGroupsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(groupId: GroupId, sessionId: SessionId): Future[AccountGroupId] = {
    create(sessionId.toAccountId, groupId, sessionId)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[AccountGroupId] = {
    for {
      r <- createAccountGroups(accountId, groupId, sessionId)
      _ <- updateAccountCount(groupId, 1L)
    } yield (r)
  }

  private def createAccountGroups(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[AccountGroupId] = {
    val joinedAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .insert(
          _.accountId           -> lift(accountId),
          _.groupId             -> lift(groupId),
          _.unreadCount         -> 0L,
          _.joinedAt            -> lift(joinedAt),
          _.by                  -> lift(by),
          _.hidden              -> false,
          _.mute                -> false
        ).returning(_.id)
    }
    run(q)
  }

  def delete(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    for {
      _ <- deleteAccountGroups(accountId, groupId)
      _ <- updateAccountCount(groupId, -1L)
    } yield (())
  }

  private def deleteAccountGroups(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId  == lift(accountId))
        .delete
    }
    run(q).map(_ => ())
  }

  def exists(groupId: GroupId, accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId     == lift(groupId))
        .filter(_.accountId   == lift(accountId))
        .nonEmpty
    }
    run(q)
  }

  def updateUnreadCount(groupId: GroupId): Future[Unit] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .update(
          ug => ug.hidden       -> false,
          ug => ug.unreadCount  -> (ug.unreadCount + lift(1))
        )
    }
    run(q).map(_ => ())
  }

  def updateHidden(groupId: GroupId, hidden: Boolean, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId     == lift(groupId))
        .filter(_.accountId   == lift(by))
        .update(
          _.hidden -> lift(hidden)
        )
    }
    run(q).map(_ => ())
  }


  def findByAccountId(accountId: AccountId, sessionId: SessionId): Future[Option[Group]] = {
    val by = sessionId.toAccountId
    val q = quote {
      for {
        ag <- query[AccountGroups]
          .filter(_.accountId == lift(accountId))
          .filter(_.by        == lift(by))
        g <- query[Groups]
          .join(_.id == ag.groupId)
        am <- query[AccountMessages]
          .leftJoin(_.messageId == g.messageId)
        m <- query[Messages]
          .leftJoin(_.id == g.messageId)
        i <- query[Mediums]
          .leftJoin(_.id == m.map(_.mediumId))
        a <- query[Accounts]
          .leftJoin(_.id == m.map(_.by))
        r <- query[Relationships]
          .leftJoin(r => a.map(_.id) == r.accountId && r.by == lift(by))
      } yield (g, am, m, i, a, r, ag.id)
    }
    run(q).map(_.map({ case (g, am, m, i, a, r, id) => Group(g, am, m, i, a, r, id.value)}).headOption)
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    val by = sessionId.toAccountId
    val q = quote {
      (for {
        ag <- query[AccountGroups]
          .filter(_.accountId == lift(accountId))
          .filter(_.hidden == lift(hidden))
          .filter(ag => lift(since).forall(ag.id < _))
        g <- query[Groups]
          .join(_.id == ag.groupId)
        am <- query[AccountMessages]
          .leftJoin(am => am.accountId == lift(accountId) && am.messageId == g.messageId)
        m <- query[Messages]
          .leftJoin(_.id == g.messageId)
        i <- query[Mediums]
          .leftJoin(_.id == m.map(_.mediumId))
        a <- query[Accounts]
          .leftJoin(_.id == m.map(_.by))
        r <- query[Relationships]
            .leftJoin(r => r.accountId == a.map(_.id) && r.by == lift(by))
      } yield (g, am, m, i, a, r, ag.id))
        .sortBy({ case (_, _, _, _, _, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (g, am, m, i, a, r, id) => Group(g, am, m, i, a, r, id.value)}))
  }

  def isHidden(groupId: GroupId, sessionId: SessionId): Future[Option[Boolean]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId == lift(accountId))
        .map(_.hidden)
    }
    run(q).map(_.headOption)
  }

  private def updateAccountCount(groupId: GroupId, plus: Long): Future[Unit] = {
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .update(g => g.accountCount -> (g.accountCount + lift(plus)))
    }
    run(q).map(_ => ())
  }


}

