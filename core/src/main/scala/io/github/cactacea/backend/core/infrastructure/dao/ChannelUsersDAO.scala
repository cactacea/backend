package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ChannelAuthorityType
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.models.{Relationships, UserChannels, Users}

@Singleton
class ChannelUsersDAO @Inject()(db: DatabaseService) {

  import db._

  def find(channelId: ChannelId, since: Option[Long], offset: Int, count: Int): Future[Seq[User]] = {
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

  def isLastOrganizer(channelId: ChannelId, sessionId: SessionId): Future[Boolean] = {
    val userId = sessionId.userId
    val q = quote {
      query[UserChannels]
        .filter(_.userId        != lift(userId))
        .filter(_.channelId     == lift(channelId))
        .filter(_.authorityType == lift(ChannelAuthorityType.organizer))
        .isEmpty
    }
    run(q)
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
