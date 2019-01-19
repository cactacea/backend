package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyJoined, AccountNotJoined, GroupNotFound}

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
      query[AccountGroups]
        .filter(_.groupId         == lift(groupId))
        .filter(_.by              == lift(by))
        .join(query[Groups]).on({ case (ag, g) => g.id == ag.groupId })
        .map({ case (ag, g) => (g, ag.id) })
    }

    for {
      r <- run(q)
      i = r.map({ case (g, _) => g.messageId}).flatten
      m <- findMessages(i, sessionId)
      g = joinMessages(r, m).headOption
    } yield (g)

  }


  def findByAccountId(accountId: AccountId, sessionId: SessionId): Future[Option[Group]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountGroups]
        .filter(_.accountId         == lift(accountId))
        .filter(_.by                == lift(by))
        .join(query[Groups]).on({ case (ag, g) => g.id == ag.groupId})
        .map({ case (ag, g) => (g, ag.id) })
    }

    for {
      r <- run(q)
      i = r.map({ case (g, _) => g.messageId}).flatten
      m <- findMessages(i, sessionId)
      g = joinMessages(r, m).headOption
    } yield (g)

  }


  def find(accountId: AccountId,
           since: Option[Long],
           offset: Int,
           count: Int,
           hidden: Boolean,
           sessionId: SessionId): Future[List[Group]] = {

    val q = quote {
      query[AccountGroups]
        .filter(ag => ag.accountId == lift(accountId))
        .filter(ag => ag.hidden == lift(hidden))
        .filter(ag => lift(since).forall(ag.id < _))
        .join(query[Groups]).on({ case (ag, g) => g.id == ag.groupId})
        .map({ case (ag, g) => (g, ag.id) })
        .sortBy({ case (_, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }

    for {
      r <- run(q)
      i = r.map({ case (g, _) => g.messageId}).flatten
      m <- findMessages(i, sessionId)
      g = joinMessages(r, m)
    } yield (g)

  }

  private def findMessages(
                            ids: List[MessageId],
                            sessionId: SessionId): Future[List[(AccountMessages, Messages, Option[Mediums], Accounts, Option[Relationships])]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[AccountMessages]
        .filter(am => liftQuery(ids).contains(am.messageId))
        .join(query[Messages]).on({ case (am, m) => am.messageId == m.id })
        .leftJoin(query[Mediums]).on({ case ((_, m), i) => m.mediumId.forall(_ == i.id) })
        .join(query[Accounts]).on({ case (((_, m), _), a) => a.id == m.by })
        .leftJoin(query[Relationships]).on({ case ((((_, _), _), a), r) => r.accountId == a.id && r.by == lift(by) })
        .map({ case ((((am, m), i), a), r) => (am, m, i, a, r)})
    }

    run(q)

  }

  private def joinMessages(
                          l1: List[(Groups, AccountGroupId)],
                          l2: List[(AccountMessages, Messages, Option[Mediums], Accounts, Option[Relationships])]): List[Group] = {

    l1.map({ case (g, n) =>
      g.messageId match {
        case Some(id) =>
          l2.filter({ case (_, m, _, _, _) => m.id == id }).headOption match {
            case Some((am, m, i, a, r)) =>
              Group(g, am, m, i, a, r, n.value)
            case None =>
              Group(g, n.value)
          }

        case None =>
          Group(g, n.value)
      }
    })
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


  def validateFindByGroupId(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    findByGroupId(groupId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(t)
      case _ =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def validateExist(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    exist(groupId, accountId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def validateNotExist(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    exist(groupId, accountId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyJoined))
      case false =>
        Future.Unit
    })
  }

}

