package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.dao.ChannelsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{ChannelNotFound, OrganizerCanNotLeave}

@Singleton
class ChannelsValidator @Inject()(channelsDAO: ChannelsDAO) {

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

  def mustNotLastOrganizer(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    channelsDAO.isOrganizer(channelId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(OrganizerCanNotLeave))
      case false =>
        Future.Unit
    })
  }


}

