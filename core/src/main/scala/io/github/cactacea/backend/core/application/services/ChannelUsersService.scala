package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, SessionId, UserId}

@Singleton
class ChannelUsersService @Inject()(
                                      databaseService: DatabaseService,
                                      channelUsersRepository: ChannelUsersRepository
                                    ) {

  import databaseService._

  def find(channelId: ChannelId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[User]] = {
    channelUsersRepository.find(channelId, since, offset, count, sessionId)
  }

  def create(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction{
      channelUsersRepository.create(channelId, sessionId)
    }
  }

  def create(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction{
      channelUsersRepository.create(userId, channelId, sessionId)
    }
  }

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction{
      channelUsersRepository.delete(channelId, sessionId)
    }
  }

  def delete(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction{
      channelUsersRepository.delete(userId, channelId, sessionId)
    }
  }

}
