package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, ChannelId, SessionId}

class UserChannelsService @Inject()(
                                     userChannelsRepository: UserChannelRepository,
                                     databaseService: DatabaseService
                                    ) {

  import databaseService._

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction {
      userChannelsRepository.delete(channelId,sessionId)
    }
  }

  def find(userId: UserId, sessionId: SessionId): Future[Channel] = {
    transaction {
      userChannelsRepository.findOrCreate(userId, sessionId)
    }
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Channel]] = {
    userChannelsRepository.find(userId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Channel]] = {
    userChannelsRepository.find(since, offset, count, hidden, sessionId)
  }

  def hide(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction {
      userChannelsRepository.hide(channelId, sessionId)
    }
  }

  def show(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction {
      userChannelsRepository.show(channelId, sessionId)
    }
  }

}
