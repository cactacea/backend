package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId, SessionId}

class AccountChannelsService @Inject()(
                                      accountChannelsRepository: AccountChannelRepository,
                                      databaseService: DatabaseService
                                    ) {

  import databaseService._

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction {
      accountChannelsRepository.delete(channelId,sessionId)
    }
  }

  def find(accountId: AccountId, sessionId: SessionId): Future[Channel] = {
    transaction {
      accountChannelsRepository.findOrCreate(accountId, sessionId)
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Channel]] = {
    accountChannelsRepository.find(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Channel]] = {
    accountChannelsRepository.find(since, offset, count, hidden, sessionId)
  }

  def hide(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction {
      accountChannelsRepository.hide(channelId, sessionId)
    }
  }

  def show(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction {
      accountChannelsRepository.show(channelId, sessionId)
    }
  }

}
