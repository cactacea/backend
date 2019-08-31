package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.dao.{AccountChannelsDAO, AccountMessagesDAO, ChannelsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountChannelsValidator, AccountsValidator}


class AccountChannelRepository @Inject()(
                                         accountsValidator: AccountsValidator,
                                         accountChannelsValidator: AccountChannelsValidator,
                                         accountChannelsDAO: AccountChannelsDAO,
                                         accountMessagesDAO: AccountMessagesDAO,
                                         channelsDAO: ChannelsDAO
                                       ) {

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountChannelsValidator.mustJoined(sessionId.toAccountId, channelId)
      _ <- accountChannelsDAO.updateHidden(channelId, true, sessionId)
      _ <- accountMessagesDAO.delete(sessionId.toAccountId, channelId)
    } yield (())
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Channel]] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      r <- accountChannelsDAO.find(accountId, since, offset, count, false, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Channel]] = {
    accountChannelsDAO.find(sessionId.toAccountId, since, offset, count, hidden, sessionId)
  }

  def findOrCreate(accountId: AccountId, sessionId: SessionId): Future[Channel] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      g <- accountChannelsDAO.findByAccountId(accountId, sessionId)
      r <- createIfNeeded(g, accountId, sessionId)
    } yield (r)
  }

  private def createIfNeeded(channel: Option[Channel], accountId: AccountId, sessionId: SessionId): Future[Channel] = {
    channel match {
      case None =>
        for {
          i <- channelsDAO.create(sessionId)
          _ <- accountChannelsDAO.create(i, sessionId)
          _ <- accountChannelsDAO.create(accountId, i, sessionId)
          g <- accountChannelsValidator.mustFindByAccountId(accountId, sessionId)
        } yield (g)
      case Some(g) =>
        Future.value(g)
    }
  }

  def show(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountChannelsValidator.mustHidden(channelId, sessionId)
      _ <- accountChannelsDAO.updateHidden(channelId, false, sessionId)
    } yield (())
  }

  def hide(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountChannelsValidator.mustNotHidden(channelId, sessionId)
      _ <- accountChannelsDAO.updateHidden(channelId, true, sessionId)
    } yield (())
  }

}
