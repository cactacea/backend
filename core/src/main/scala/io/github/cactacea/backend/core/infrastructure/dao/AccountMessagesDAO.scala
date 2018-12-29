package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.MessageNotFound

@Singleton
class AccountMessagesDAO @Inject()(db: DatabaseService) {

  import db._

  def create(groupId: GroupId, messageId: MessageId, sessionId: SessionId): Future[Unit] = {
    val postedAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      infix"""
         insert into account_messages (account_id, group_id, message_id, `by`, unread, notified, posted_at)
         select account_id, group_id, ${lift(messageId)} as message_id, ${lift(by)} as `by`, true as unread, false as notified, ${lift(postedAt)} posted_at
         from account_groups where group_id = ${lift(groupId)}
          """.as[Action[Long]]
    }
    run(q).map(_ => Unit)
  }



  def delete(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    val q = quote {
      query[AccountMessages]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId == lift(accountId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def find(groupId: GroupId,
           since: Option[Long],
           offset: Int,
           count: Int,
           ascending: Boolean,
           sessionId: SessionId): Future[List[Message]] = {

    if (ascending) {
      findOlder(groupId, since, offset, count, sessionId)
    } else {
      findEarlier(groupId, since, offset, count, sessionId)
    }

  }

  private def findEarlier(groupId: GroupId,
                  since: Option[Long],
                  offset: Int,
                  count: Int,
                  sessionId: SessionId): Future[List[Message]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[AccountMessages]
        .filter(am => am.accountId == lift(by) && am.groupId == lift(groupId))
        .filter(am => lift(since).forall(am.messageId < _))
        .join(query[Messages]).on({ case (am, m) => m.id == am.messageId })
        .join(query[Accounts]).on({ case ((_, m), a) => a.id == m.by })
        .leftJoin(query[Mediums]).on({ case (((_, m), _), i) => m.mediumId.contains(i.id) })
        .leftJoin(query[Relationships]).on({ case ((((_, _), a), _), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((((am, m), a), i), r) => (m, am, i, a, r) })
        .sortBy({ case (_, am, _, _, _) => am.messageId })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (m, am, i, a, r) => Message(m, am, i, a, r, am.messageId.value)}))

  }

  private def findOlder(groupId: GroupId,
                since: Option[Long],
                offset: Int,
                count: Int,
                sessionId: SessionId): Future[List[Message]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[AccountMessages]
        .filter(am => am.accountId == lift(by))
        .filter(am => am.groupId == lift(groupId) )
        .filter(am => lift(since).forall(am.messageId > _))
        .join(query[Messages]).on({ case (am, m) => m.id == am.messageId })
        .join(query[Accounts]).on({ case ((_, m), a) => a.id == m.by })
        .leftJoin(query[Mediums]).on({ case (((_, m), _), i) => m.mediumId.contains(i.id) })
        .leftJoin(query[Relationships]).on({ case ((((_, _), a), _), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((((am, m), a), i), r) => (m, am, i, a, r) })
        .sortBy({ case (_, am, _, _, _) => am.messageId})(Ord.asc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (m, am, i, a, r) => Message(m, am, i, a, r, am.messageId.value)}))

  }



  def updateUnread(messageIds: List[MessageId], sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[AccountMessages]
        .filter(_.accountId == lift(accountId))
        .filter(m => liftQuery(messageIds).contains(m.messageId))
        .update(_.unread -> false)
    }
    run(q).map(_ => Unit)
  }

  def updateNotified(messageId: MessageId, accountIds: List[AccountId]): Future[Unit] = {
    val q = quote {
      query[AccountMessages]
        .filter(_.messageId == lift(messageId))
        .filter(m => liftQuery(accountIds).contains(m.accountId))
        .update(_.notified -> true)
    }
    run(q).map(_ => Unit)
  }



  def validateFind(id: MessageId, sessionId: SessionId): Future[Message] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[AccountMessages]
        .filter(am => am.accountId == lift(by))
        .filter(am => am.messageId == lift(id) )
        .join(query[Messages]).on({ case (am, m) => m.id == am.messageId })
        .join(query[Accounts]).on({ case ((_, m), a) => a.id == m.by })
        .leftJoin(query[Mediums]).on({ case (((_, m), _), i) => m.mediumId.contains(i.id) })
        .leftJoin(query[Relationships]).on({ case ((((_, _), a), _), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((((am, m), a), i), r) => (m, am, i, a, r) })
        .sortBy({ case (_, am, _, _, _) => am.messageId})(Ord.asc)
    }
    run(q).flatMap(_.map({ case (m, am, i, a, r) => Message(m, am, i, a, r, am.messageId.value)}).headOption match {
      case Some(m) => Future.value(m)
      case None => Future.exception(CactaceaException(MessageNotFound))
    })

  }

}
