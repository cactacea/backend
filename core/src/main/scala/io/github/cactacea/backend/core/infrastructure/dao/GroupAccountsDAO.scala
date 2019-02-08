package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId}
import io.github.cactacea.backend.core.infrastructure.models.{AccountGroups, Accounts, Relationships}

@Singleton
class GroupAccountsDAO @Inject()(db: DatabaseService) {

  import db._

  def find(groupId: GroupId,
           since: Option[Long],
           offset: Int,
           count: Int): Future[List[Account]] = {

    val q = quote {
      (for {
        ag <- query[AccountGroups]
          .filter(_.groupId == lift(groupId))
          .filter(ag => lift(since).forall(ag.id < _))
        a <- query[Accounts]
          .join(_.id == ag.accountId)
        r <- query[Relationships]
          .leftJoin(_.accountId == a.id)
      } yield (a, r, ag.id))
        .sortBy(_._3)(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }

  def exist(groupId: GroupId, accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId   == lift(groupId))
        .filter(_.accountId    == lift(accountId))
        .nonEmpty
    }
    run(q)
  }


}
