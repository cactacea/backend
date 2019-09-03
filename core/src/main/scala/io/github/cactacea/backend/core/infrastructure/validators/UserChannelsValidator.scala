package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.dao.UserChannelsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserAlreadyJoined, UserNotJoined, ChannelAlreadyHidden, ChannelNotHidden}

@Singleton
class UserChannelsValidator @Inject()(userChannelsDAO: UserChannelsDAO) {

  def mustFindByUserId(userId: UserId, sessionId: SessionId): Future[Channel] = {
    userChannelsDAO.findByUserId(userId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(t)
      case _ =>
        Future.exception(CactaceaException(UserNotJoined))
    })
  }

  def mustJoined(userId: UserId, channelId: ChannelId): Future[Unit] = {
    userChannelsDAO.exists(channelId, userId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(UserNotJoined))
    })
  }

  def mustNotJoined(userId: UserId, channelId: ChannelId): Future[Unit] = {
    userChannelsDAO.exists(channelId, userId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(UserAlreadyJoined))
      case false =>
        Future.Unit
    })
  }

  def mustHidden(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    userChannelsDAO.isHidden(channelId, sessionId).flatMap(_ match {
      case Some(true) =>
        Future.Unit
      case Some(false) =>
        Future.exception(CactaceaException(ChannelNotHidden))
      case None =>
        Future.exception(CactaceaException(UserNotJoined))
    })
  }

  def mustNotHidden(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    userChannelsDAO.isHidden(channelId, sessionId).flatMap(_ match {
      case Some(true) =>
        Future.exception(CactaceaException(ChannelAlreadyHidden))
      case Some(false) =>
        Future.Unit
      case None =>
        Future.exception(CactaceaException(UserNotJoined))
    })
  }

}

