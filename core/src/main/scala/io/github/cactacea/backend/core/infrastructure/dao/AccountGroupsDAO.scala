package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AccountGroupsDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def exist(groupId: GroupId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId     == lift(groupId))
        .filter(_.accountId   == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def create(accountId: AccountId, groupId: GroupId): Future[AccountGroupId] = {
    for {
      id <- create(accountId, groupId, accountId.toSessionId)
      _ <- updateAccountCount(groupId, 1L)
    } yield (id)
  }

  private def updateAccountCount(groupId: GroupId, count: Long): Future[Unit] = {
    val r = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .update(g => g.accountCount -> (g.accountCount + lift(count)))
    }
    run(r).map(_ => Unit)
  }


  def create(accountIds: List[AccountId], groupId: GroupId): Future[List[AccountGroupId]] = {
    val r = Future.traverseSequentially(accountIds) { id =>
      create(id, groupId, id.toSessionId)
    }
    r.map(_.toList)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[AccountGroupId] = {
    for {
      id <- insert(accountId, groupId, sessionId)
    } yield (id)
  }

  private def insert(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[AccountGroupId] = {
    val joinedAt = timeService.currentTimeMillis()
    val toAccountId = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .insert(
          _.accountId           -> lift(accountId),
          _.groupId             -> lift(groupId),
          _.unreadCount         -> 0L,
          _.joinedAt            -> lift(joinedAt),
          _.toAccountId         -> lift(toAccountId),
          _.hidden              -> false,
          _.mute                -> false
        ).returning(_.id)
    }
    run(q)
  }

  def delete(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    for {
      _ <- updateAccountCount(groupId, -1L)
      _ <- delete2(accountId, groupId)
    } yield (Unit)
  }

  private def delete2(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId  == lift(accountId))
        .delete
    }
    run(q).map(_ => Unit)
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
    run(q).map(_ => Unit)
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
    run(q).map(_ => Unit)
  }

  def findByGroupId(groupId: GroupId, sessionId: SessionId): Future[Option[(AccountGroups, Groups)]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId         == lift(groupId))
        .filter(_.toAccountId == lift(by))
        .join(query[Groups]).on({ case (ag, g) => g.id == ag.groupId })
    }
    run(q).map(_.headOption)
  }

  def findByAccountId(accountId: AccountId, sessionId: SessionId): Future[Option[(AccountGroups, Groups)]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
          .filter(_.accountId         == lift(accountId))
          .filter(_.toAccountId == lift(by))
          .join(query[Groups]).on({ case (ag, g) => g.id == ag.groupId})
    }
    run(q).map(_.headOption)
  }

  def findAll(accountId: AccountId,
              since: Option[Long],
              offset: Int,
              count: Int,
              hidden: Boolean): Future[List[Group]] = {

    val q = quote {
      query[AccountGroups]
        .filter(ag => ag.accountId == lift(accountId))
        .filter(ag => ag.hidden == lift(hidden))
        .filter(ag => lift(since).forall(ag.id < _))
        .join(query[Groups]).on({ case (ag, g) => g.id == ag.groupId})
        .leftJoin(query[Messages]).on({ case ((_, g), m) => g.messageId.contains(m.id) })
        .map({ case ((ag, g), m) => (g, m, ag.id) })
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))

    }
    run(q).map(_.map({case (g, m, id) => Group(g, m, id.value)}))

  }

  def findGroupId(messageId: MessageId, sessionId: SessionId): Future[Option[GroupId]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Messages]
        .filter(_.id == lift(messageId))
        .join(query[AccountGroups]).on({ case (_, ag) => ag.accountId == lift(by)})
        .map({ case (m, _) => m.groupId })
    }
    run(q).map(_.headOption)
  }

}

