package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.MessageType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountChannelsValidator, AccountsValidator, ChannelAuthorityValidator, ChannelsValidator}


class ChannelAccountsRepository @Inject()(
                                         accountsValidator: AccountsValidator,
                                         accountChannelsDAO: AccountChannelsDAO,
                                         accountChannelsValidator: AccountChannelsValidator,
                                         accountMessagesDAO: AccountMessagesDAO,
                                         channelsValidator: ChannelsValidator,
                                         channelAccountsDAO: ChannelAccountsDAO,
                                         channelAuthorityValidator: ChannelAuthorityValidator,
                                         channelsDAO: ChannelsDAO,
                                         messagesDAO: MessagesDAO
                                       ) {

  def create(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- accountChannelsValidator.mustNotJoined(accountId, channelId)
      _ <- channelAuthorityValidator.hasJoinAuthority(channelId, sessionId)
      _ <- accountChannelsDAO.create(accountId, channelId, accountId.toSessionId)
      c <- channelsDAO.findAccountCount(channelId)
      m <- messagesDAO.create(channelId, MessageType.joined, c, sessionId)
      _ <- accountMessagesDAO.create(channelId, m, sessionId)
    } yield (())
  }

  def create(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- accountChannelsValidator.mustNotJoined(accountId, channelId)
      _ <- channelAuthorityValidator.hasAddMembersAuthority(accountId, channelId, sessionId)
      _ <- accountChannelsDAO.create(accountId, channelId, accountId.toSessionId)
      c <- channelsDAO.findAccountCount(channelId)
      m <- messagesDAO.create(channelId, MessageType.joined, c, accountId.toSessionId)
      _ <- accountMessagesDAO.create(channelId, m, accountId.toSessionId)
    } yield (())
  }

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- channelsValidator.mustExist(channelId, sessionId)
      _ <- accountChannelsValidator.mustJoined(accountId, channelId)
      _ <- channelsValidator.mustNotLastOrganizer(channelId, sessionId)
      _ <- accountChannelsDAO.delete(accountId, channelId)
      _ <- accountMessagesDAO.delete(accountId, channelId)
      c <- channelsDAO.findAccountCount(channelId)
      _ <- deleteChannel(accountId, channelId, c)
    } yield (())
  }

  def delete(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- channelsValidator.mustExist(channelId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- accountChannelsValidator.mustJoined(accountId, channelId)
      _ <- channelsValidator.mustNotLastOrganizer(channelId, accountId.toSessionId)
      _ <- channelAuthorityValidator.hasRemoveMembersAuthority(channelId, sessionId)
      _ <- accountChannelsDAO.delete(accountId, channelId)
      _ <- accountMessagesDAO.delete(accountId, channelId)
      c <- channelsDAO.findAccountCount(channelId)
      _ <- deleteChannel(accountId, channelId, c)
    } yield (())
  }

  private def deleteChannel(accountId: AccountId, channelId: ChannelId, accountCount: Long): Future[Unit] = {
    if (accountCount == 0) {
      channelsDAO.delete(channelId)
    } else {
      (for {
        m <- messagesDAO.create(channelId, MessageType.left, accountCount, accountId.toSessionId)
        _ <- accountMessagesDAO.create(channelId, m, accountId.toSessionId)
      } yield (()))
    }
  }

  def find(channelId: ChannelId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- channelAuthorityValidator.hasFindMembersAuthority(channelId, sessionId)
      r <- channelAccountsDAO.find(channelId, since, offset, count)
    } yield (r)
  }

}
