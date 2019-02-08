package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.GroupNotFound

@Singleton
class AccountGroupsDAO @Inject()(db: DatabaseService) {

  import db._

  def exist(groupId: GroupId, accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId     == lift(groupId))
        .filter(_.accountId   == lift(accountId))
        .nonEmpty
    }
    run(q)
  }

  def create(accountId: AccountId, groupId: GroupId): Future[AccountGroupId] = {
    for {
      id <- insert(accountId, groupId, accountId.toSessionId)
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


  def findByGroupId(groupId: GroupId, sessionId: SessionId): Future[Option[Group]] = {

    val by = sessionId.toAccountId

    val q = quote {
      for {
        ag <- query[AccountGroups]
          .filter(_.groupId         == lift(groupId))
          .filter(_.by              == lift(by))
        g <- query[Groups]
          .join(_.id == ag.groupId)
        am <- query[AccountMessages]
          .leftJoin(am => g.messageId.exists(_ == am.messageId))
        m <- query[Messages]
          .leftJoin(m => g.messageId.exists(_ == m.id))
        i <- query[Mediums]
          .leftJoin(i => m.exists(_.mediumId.exists(_ == i.id)))
        a <- query[Accounts]
          .leftJoin(a => m.exists(_.by == a.id))
        r <- query[Relationships]
          .leftJoin(r => a.exists(_.id == r.accountId) && r.by == lift(by))
      } yield (g, am, m, i, a, r, ag.id)

    }
    run(q).map(_.map({ case (g, am, m, i, a, r, id) =>
      (am, m, a) match {
        case (Some(am), Some(m), Some(a)) => Group(g, am, m, i, a, r, id.value)
        case _ => Group(g, id.value)
      }
    }).headOption)


  }


  def findByAccountId(accountId: AccountId, sessionId: SessionId): Future[Option[Group]] = {

    val by = sessionId.toAccountId

    val q = quote {
      for {
        ag <- query[AccountGroups]
          .filter(_.accountId       == lift(accountId))
          .filter(_.by              == lift(by))
        g <- query[Groups]
          .join(_.id == ag.groupId)
        am <- query[AccountMessages]
          .leftJoin(am => g.messageId.exists(_ == am.messageId))
        m <- query[Messages]
          .leftJoin(m => g.messageId.exists(_ == m.id))
        i <- query[Mediums]
          .leftJoin(i => m.exists(_.mediumId.exists(_ == i.id)))
        a <- query[Accounts]
          .leftJoin(a => m.exists(_.by == a.id))
        r <- query[Relationships]
          .leftJoin(r => a.exists(_.id == r.accountId) && r.by == lift(by))
      } yield (g, am, m, i, a, r, ag.id)

    }
    run(q).map(_.map({ case (g, am, m, i, a, r, id) =>
      (am, m, a) match {
        case (Some(am), Some(m), Some(a)) => Group(g, am, m, i, a, r, id.value)
        case _ => Group(g, id.value)
      }
    }).headOption)

  }


  def find(accountId: AccountId,
           since: Option[Long],
           offset: Int,
           count: Int,
           hidden: Boolean,
           sessionId: SessionId): Future[List[Group]] = {

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
          .leftJoin(am => g.messageId.exists(_ == am.messageId))
        m <- query[Messages]
          .leftJoin(m => g.messageId.exists(_ == m.id))
        i <- query[Mediums]
          .leftJoin(i => m.exists(_.mediumId.exists(_ == i.id)))
        a <- query[Accounts]
          .leftJoin(a => m.exists(_.by == a.id))
        r <- query[Relationships]
            .leftJoin(r => a.exists(_.id == r.accountId) && r.by == lift(by))
      } yield (g, am, m, i, a, r, ag.id))
        .sortBy(_._7)(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))

    }
    run(q).map(_.map({ case (g, am, m, i, a, r, id) =>
      (am, m, a) match {
          case (Some(am), Some(m), Some(a)) => Group(g, am, m, i, a, r, id.value)
          case _ => Group(g, id.value)
        }
    }))

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

  def findAccountCount(groupId: GroupId): Future[Long] = {
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .map(_.accountCount)
    }
    run(q).flatMap(_.headOption match {
      case Some(c) =>
        Future.value(c)
      case None =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }

  def findHidden(groupId: GroupId, sessionId: SessionId): Future[Option[Boolean]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .filter(_.by == lift(by))
        .map(_.hidden)
    }
    run(q).map(_.headOption)
  }

}

