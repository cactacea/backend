package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.application.services.TimeService
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class AccountGroupsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _
  @Inject private var identifyService: IdentifyService = _

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
    val r = Future.traverseSequentially(accountIds) { id =>
      create(id, groupId, id.toSessionId)
    }
    r.map(_.foldLeft(true)(_ && _))
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Boolean] = {
    for {
      id <- identifyService.generate().map(AccountGroupId(_))
      r <- _insert(id, accountId, groupId, sessionId)
    } yield (r)
  }

  private def _insert(id: AccountGroupId, accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Boolean] = {
    val joinedAt = timeService.nanoTime()
    val toAccountId = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .insert(
          _.id                  -> lift(id),
          _.accountId           -> lift(accountId),
          _.groupId             -> lift(groupId),
          _.unreadCount         -> 0L,
          _.joinedAt            -> lift(joinedAt),
          _.toAccountId         -> lift(toAccountId),
          _.hidden              -> false,
          _.mute                -> false
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
          ug => ug.hidden       -> false,
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

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], hidden: Boolean): Future[List[(Groups, Option[Messages], Option[AccountMessages], Option[Accounts], Option[Relationships], AccountGroupId)]] = {

    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)

    val q = quote {
      query[AccountGroups].filter(ag => ag.accountId == lift(accountId) && ag.hidden == lift(hidden))
        .filter(_ => infix"ag.id < ${lift(s)}".as[Boolean] || lift(s) == -1L)
        .join(query[Groups]).on({ case (ag, g) => g.id == ag.groupId})
        .leftJoin(query[Messages]).on({ case ((_, g), m) => g.messageId.exists(_ == m.id) })
        .leftJoin(query[AccountMessages]).on({ case (((_, g), _), am) => g.messageId.exists(_ == am.messageId) && am.accountId == lift(accountId) })
        .sortBy({ case (((ag, _), _), _) => ag.id })(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).map(_.sortBy(_._1._1._1.id.value).reverse.map({ case (((ag, g), m), am) => (g, m, am, None, None, ag.id)}))

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

