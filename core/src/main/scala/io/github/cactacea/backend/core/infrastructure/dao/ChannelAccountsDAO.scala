package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId}
import io.github.cactacea.backend.core.infrastructure.models.{AccountChannels, Accounts, Relationships}

@Singleton
class ChannelAccountsDAO @Inject()(db: DatabaseService) {

  import db._

  def find(channelId: ChannelId, since: Option[Long], offset: Int, count: Int): Future[List[Account]] = {
    val q = quote {
      (for {
        ag <- query[AccountChannels]
          .filter(_.channelId == lift(channelId))
          .filter(ag => lift(since).forall(ag.id < _))
        a <- query[Accounts]
          .join(_.id == ag.accountId)
        r <- query[Relationships]
          .leftJoin(_.accountId == a.id)
      } yield (a, r, ag.id))
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))

  }

  def exists(channelId: ChannelId, accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[AccountChannels]
        .filter(_.channelId   == lift(channelId))
        .filter(_.accountId    == lift(accountId))
        .nonEmpty
    }
    run(q)
  }


}
