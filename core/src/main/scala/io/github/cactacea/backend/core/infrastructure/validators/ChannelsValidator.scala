package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.dao.{ChannelUsersDAO, ChannelsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.ChannelNotFound

@Singleton
class ChannelsValidator @Inject()(channelsDAO: ChannelsDAO, channelUsersDAO: ChannelUsersDAO) {

  def mustExist(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    channelsDAO.exists(channelId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(ChannelNotFound))
    })
  }

  def mustFind(channelId: ChannelId, sessionId: SessionId): Future[Channel] = {
    channelsDAO.find(channelId, sessionId).flatMap(_ match {
      case Some(t) => Future.value(t)
      case None => Future.exception(CactaceaException(ChannelNotFound))
    })
  }

}

