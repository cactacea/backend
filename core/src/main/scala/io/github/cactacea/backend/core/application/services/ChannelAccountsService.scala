package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId, SessionId}

class ChannelAccountsService @Inject()(
                                      databaseService: DatabaseService,
                                      channelAccountsRepository: ChannelAccountsRepository
                                    ) {

  import databaseService._

  def find(channelId: ChannelId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    channelAccountsRepository.find(channelId, since, offset, count, sessionId)
  }

  def create(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction{
      channelAccountsRepository.create(channelId, sessionId)
    }
  }

  def create(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction{
      channelAccountsRepository.create(accountId, channelId, sessionId)
    }
  }

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction{
      channelAccountsRepository.delete(channelId, sessionId)
    }
  }

  def delete(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    transaction{
      channelAccountsRepository.delete(accountId, channelId, sessionId)
    }
  }

}
