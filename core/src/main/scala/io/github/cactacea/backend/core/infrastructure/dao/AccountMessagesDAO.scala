package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AccountMessagesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(groupId: GroupId, messageId: MessageId, sessionId: SessionId): Future[Unit] = {
    val postedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      infix"""
         insert into account_messages (account_id, group_id, message_id, `by`, unread, notified, posted_at)
         select account_id, group_id, ${lift(messageId)} as message_id, ${lift(by)} as `by`, true as unread, false as notified, ${lift(postedAt)} posted_at from account_groups where group_id = ${lift(groupId)}
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

  def findEarlier(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[AccountMessages]
        .filter(am => am.accountId == lift(by) && am.groupId == lift(groupId))
        .filter(am => am.messageId > lift(s) || lift(s) == -1L)
        .join(query[Messages]).on({ case (am, m) => m.id == am.messageId })
        .join(query[Accounts]).on({ case ((_, m), a) => a.id == m.by })
        .leftJoin(query[Mediums]).on({ case (((_, m), _), i) => m.mediumId.exists(_ == i.id) })
        .leftJoin(query[Relationships]).on({ case ((((_, _), a), _), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((((am, m), a), i), r) => (m, am, i, a, r) })
        .sortBy(_._2.messageId)(Ord.asc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def findOlder(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[AccountMessages]
        .filter(am => am.accountId == lift(by) && am.groupId == lift(groupId) )
        .filter(am => am.messageId < lift(s) || lift(s) == -1L)
        .join(query[Messages]).on({ case (am, m) => m.id == am.messageId })
        .join(query[Accounts]).on({ case ((_, m), a) => a.id == m.by })
        .leftJoin(query[Mediums]).on({ case (((_, m), _), i) => m.mediumId.exists(_ == i.id) })
        .leftJoin(query[Relationships]).on({ case ((((_, _), a), _), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((((am, m), a), i), r) => (m, am, i, a, r) })
        .sortBy(_._2.messageId)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

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

}
