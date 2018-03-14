package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.core.infrastructure.models.{AccountGroups, Accounts, Relationships}

@Singleton
class GroupAccountsDAO @Inject()(db: DatabaseService) {

  import db._

  def findAll(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], AccountGroups)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
//    val by = sessionId.toAccountId

    val q = quote {
      query[AccountGroups].filter(ag => ag.groupId == lift(groupId) && (infix"ag.id < ${lift(s)}".as[Boolean] || lift(s) == -1L))
        .join(query[Accounts]).on((ag, a) => a.id == ag.accountId)
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id})
        .sortBy({ case ((ag, _), _) => ag.id})(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).map(_.sortBy(_._1._1.id.value).reverse.map({ case ((ag, a), r) => (a, r, ag)}))

  }

  def exist(accountId: AccountId, groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId   == lift(groupId))
        .filter(_.accountId    == lift(accountId))
        .size
    }
    run(q).map(_ == 1)
  }

  def findCount(groupId: GroupId): Future[Long] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .size
    }
    run(q)
  }

}
