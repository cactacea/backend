package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
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
    create(accountId, groupId, accountId.toSessionId)
  }

  def create(accountIds: List[AccountId], groupId: GroupId): Future[List[AccountGroupId]] = {
    val r = Future.traverseSequentially(accountIds) { id =>
      create(id, groupId, id.toSessionId)
    }
    r.map(_.toList)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[AccountGroupId] = {
    for {
      id <- _insert(accountId, groupId, sessionId)
    } yield (id)
  }

  private def _insert(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[AccountGroupId] = {
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
      for {
        ag <- query[AccountGroups]
          .filter(_.groupId         == lift(groupId))
          .filter(_.toAccountId == lift(by))
        g <- query[Groups]
          .filter(_.id == ag.groupId)
      } yield ((ag, g))
    }
    run(q).map(_.headOption)
  }

  def findByAccountId(accountId: AccountId, sessionId: SessionId): Future[Option[(AccountGroups, Groups)]] = {
    val by = sessionId.toAccountId
    val q = quote {
      for {
        ag <- query[AccountGroups]
          .filter(_.accountId         == lift(accountId))
          .filter(_.toAccountId == lift(by))
        g <- query[Groups]
          .filter(_.id == ag.groupId)
      } yield ((ag, g))
    }
    run(q).map(_.headOption)
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], hidden: Boolean): Future[List[(AccountGroups, Groups, Option[Messages], Option[AccountMessages])]] = {
    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val q = quote {
      query[AccountGroups]
        .filter(ag => ag.accountId == lift(accountId) && ag.hidden == lift(hidden))
        .filter(ag => ag.id < lift(s) || lift(s) == -1L)
        .join(query[Groups]).on({ case (ag, g) => g.id == ag.groupId})
        .leftJoin(query[Messages]).on({ case ((_, g), m) => g.messageId.contains(m.id) })
        .leftJoin(query[AccountMessages]).on({ case (((_, g), _), am) => g.messageId.contains(am.messageId) && am.accountId == lift(accountId) })
        .map({ case (((ag, g), m), am) => (ag, g, m, am) })
        .sortBy(_._1.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def findGroupId(messageId: MessageId, sessionId: SessionId): Future[Option[GroupId]] = {
    val by = sessionId.toAccountId
    val q = quote {
      for {
        groupId <- query[Messages]
          .filter(_.id == lift(messageId))
          .map(_.groupId)
        _ <- query[AccountGroups]
          .filter(_.accountId == lift(by))
      } yield (groupId)
    }
    run(q).map(_.headOption)
  }

}

