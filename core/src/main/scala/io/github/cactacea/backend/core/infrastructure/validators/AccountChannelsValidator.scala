package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.dao.AccountChannelsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyJoined, AccountNotJoined, ChannelAlreadyHidden, ChannelNotHidden}

@Singleton
class AccountChannelsValidator @Inject()(accountChannelsDAO: AccountChannelsDAO) {

  def mustFindByAccountId(accountId: AccountId, sessionId: SessionId): Future[Channel] = {
    accountChannelsDAO.findByAccountId(accountId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(t)
      case _ =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def mustJoined(accountId: AccountId, channelId: ChannelId): Future[Unit] = {
    accountChannelsDAO.exists(channelId, accountId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def mustNotJoined(accountId: AccountId, channelId: ChannelId): Future[Unit] = {
    accountChannelsDAO.exists(channelId, accountId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyJoined))
      case false =>
        Future.Unit
    })
  }

  def mustHidden(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    accountChannelsDAO.isHidden(channelId, sessionId).flatMap(_ match {
      case Some(true) =>
        Future.Unit
      case Some(false) =>
        Future.exception(CactaceaException(ChannelNotHidden))
      case None =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def mustNotHidden(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    accountChannelsDAO.isHidden(channelId, sessionId).flatMap(_ match {
      case Some(true) =>
        Future.exception(CactaceaException(ChannelAlreadyHidden))
      case Some(false) =>
        Future.Unit
      case None =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

}

