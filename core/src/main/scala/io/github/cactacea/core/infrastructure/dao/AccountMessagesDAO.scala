package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.application.services.TimeService
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class AccountMessagesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(groupId: GroupId, messageId: MessageId, sessionId: SessionId): Future[Boolean] = {
    val postedAt = timeService.nanoTime()
    val by = sessionId.toAccountId
    val q = quote {
      infix"""
         insert into account_messages (account_id, group_id, message_id, `by`, unread, notified, posted_at)
         select account_id, group_id, ${lift(messageId)} as message_id, ${lift(by)} as `by`, true as unread, false as notified, ${lift(postedAt)} posted_at from account_groups where group_id = ${lift(groupId)}
          """.as[Action[Long]]
    }
    run(q).map(_ > 0)
  }



  def delete(accountId: AccountId, groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[AccountMessages]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId == lift(accountId))
        .delete
    }
    run(q).map(_ >= 0)
  }

  def findEarlier(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val accountId = sessionId.toAccountId
    val by = sessionId.toAccountId

    val q = quote {
      query[AccountMessages].filter(am => am.accountId == lift(accountId) && am.groupId == lift(groupId))
        .filter(_ => infix"am.message_id > ${lift(s)}".as[Boolean] || lift(s) == -1L)
        .join(query[Messages]).on({ case (am, m) => m.id == am.messageId })
        .join(query[Accounts]).on({ case ((_, m), a) => a.id == m.by })
        .leftJoin(query[Mediums]).on({ case (((_, m), _), i) => m.mediumId.forall(_ == i.id) })
        .leftJoin(query[Relationships]).on({ case ((((_, _), a), _), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((((am, _), _), _), _) => am.messageId})(Ord.ascNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).map(_.map({ case ((((am, m), a), i), r) => (m, am, i, a, r) }))

  }

  def findOlder(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val accountId = sessionId.toAccountId
    val by = sessionId.toAccountId

    val q = quote {
      query[AccountMessages].filter(am => am.accountId == lift(accountId) && am.groupId == lift(groupId) )
        .filter(_ => infix"am.message_id < ${lift(s)}".as[Boolean] || lift(s) == -1L )
        .join(query[Messages]).on({ case (am, m) => m.id == am.messageId })
        .join(query[Accounts]).on({ case ((_, m), a) => a.id == m.by })
        .leftJoin(query[Mediums]).on({ case (((_, m), _), i) => m.mediumId.forall(_ == i.id) })
        .leftJoin(query[Relationships]).on({ case ((((_, _), a), _), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((((am, _), _), _), _) => am.messageId})(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).map(_.map({ case ((((am, m), a), i), r) => (m, am, i, a, r) }))

  }

  def updateUnread(messageIds: List[MessageId], sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[AccountMessages]
        .filter(_.accountId == lift(accountId))
        .filter(m => liftQuery(messageIds).contains(m.messageId))
        .update(_.unread -> false)
    }
    run(q).map(_ == messageIds.size)
  }

  def updateNotified(messageId: MessageId, accountIds: List[AccountId]): Future[Boolean] = {
    val q = quote {
      query[AccountMessages]
        .filter(_.messageId == lift(messageId))
        .filter(m => liftQuery(accountIds).contains(m.accountId))
        .update(_.notified -> true)
    }
    run(q).map(_ == accountIds.size)
  }

}
