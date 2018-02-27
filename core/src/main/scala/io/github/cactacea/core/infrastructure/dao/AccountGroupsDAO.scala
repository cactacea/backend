package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, MessageId, SessionId}
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class AccountGroupsDAO @Inject()(db: DatabaseService) {

  import db._

  def exist(groupId: GroupId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId     == lift(groupId))
        .filter(_.accountId   == lift(by))
        .size
    }
    run(q).map(_ == 1)
  }

  def create(accountId: AccountId, groupId: GroupId): Future[Boolean] = {
    create(accountId, groupId, accountId.toSessionId)
  }

  def create(accountIds: List[AccountId], groupId: GroupId): Future[Boolean] = {
    val joinedAt = System.nanoTime()
    val groupUsers = accountIds.map(accountId => AccountGroups(accountId, groupId, 0L, false, false, accountId, joinedAt))
    val q = quote {
      liftQuery(groupUsers).foreach(m => query[AccountGroups].insert(m))
    }
    run(q).map(_.sum == accountIds.size)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Boolean] = {
    val joinedAt = System.nanoTime()
    val toAccountId = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .insert(
          _.accountId           -> lift(accountId),
          _.groupId             -> lift(groupId),
          _.unreadCount         -> lift(0L),
          _.joinedAt            -> lift(joinedAt),
          _.toAccountId         -> lift(toAccountId),
          _.hidden              -> lift(false),
          _.muted               -> lift(false)
        )
    }
    run(q).map(_ == 1)
  }

  def delete(accountId: AccountId, groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId  == lift(accountId))
        .delete
    }
    run(q).map(_ == 1)
  }

  def updateUnreadCount(groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .update(
          ug => ug.hidden       -> lift(false),
          ug => ug.unreadCount  -> (ug.unreadCount + lift(1))
        )
    }
    run(q).map(_ > 0)
  }

  def updateHidden(groupId: GroupId, hidden: Boolean, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId     == lift(groupId))
        .filter(_.accountId   == lift(by))
        .update(
          _.hidden -> lift(hidden)
        )
    }
    run(q).map(_ == 1)
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Option[Groups]] = {
    val by = sessionId.toAccountId
    val q = quote {
      for {
        ug <- query[AccountGroups]
          .filter(_.accountId         == lift(accountId))
          .filter(_.toAccountId == lift(by))
        g <- query[Groups]
          .filter(_.id == ug.groupId)
      } yield (g)
    }
    run(q).map(_.headOption)
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], hidden: Boolean, sessionId: SessionId): Future[List[(Groups, Option[Messages], Option[AccountMessages], Option[Accounts], Option[Relationships])]] = {

    val s = since.getOrElse(Long.MaxValue)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)

    // TODO : Check Blocks
//    val by = sessionId.toAccountId

    val q = quote {
      query[AccountGroups].filter(ag => ag.accountId == lift(accountId) && ag.hidden == lift(hidden) && ag.joinedAt < lift(s))
        .join(query[Groups]).on({ case (ag, g) => g.id == ag.groupId})
        .leftJoin(query[Messages]).on({ case ((_, g), m) => g.messageId.exists(_ == m.id) })
        .leftJoin(query[AccountMessages]).on({ case (((_, g), _), am) => g.messageId.exists(_ == am.messageId) && am.accountId == lift(accountId) })
        .sortBy({ case (((ag, g), _), _) => ag.joinedAt })(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).map(_.map({ case (((ag, g), m), am) => (g, m, am, None, None)}))

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

