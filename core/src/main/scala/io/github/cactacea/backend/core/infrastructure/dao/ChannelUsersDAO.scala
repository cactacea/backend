package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, ChannelId}
import io.github.cactacea.backend.core.infrastructure.models.{UserChannels, Users, Relationships}

@Singleton
class ChannelUsersDAO @Inject()(db: DatabaseService) {

  import db._

  def find(channelId: ChannelId, since: Option[Long], offset: Int, count: Int): Future[List[User]] = {
    val q = quote {
      (for {
        ag <- query[UserChannels]
          .filter(_.channelId == lift(channelId))
          .filter(ag => lift(since).forall(ag.id < _))
        a <- query[Users]
          .join(_.id == ag.userId)
        r <- query[Relationships]
          .leftJoin(_.userId == a.id)
      } yield (a, r, ag.id))
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => User(a, r, id.value)}))

  }

  def exists(channelId: ChannelId, userId: UserId): Future[Boolean] = {
    val q = quote {
      query[UserChannels]
        .filter(_.channelId   == lift(channelId))
        .filter(_.userId    == lift(userId))
        .nonEmpty
    }
    run(q)
  }


}
