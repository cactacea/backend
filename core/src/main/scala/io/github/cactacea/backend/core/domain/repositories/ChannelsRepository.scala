package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators._


class ChannelsRepository @Inject()(
                                  accountChannelsValidator: AccountChannelsValidator,
                                  channelsValidator: ChannelsValidator,
                                  channelAuthorityValidator: ChannelAuthorityValidator,
                                  accountChannelsDAO: AccountChannelsDAO,
                                  channelsDAO: ChannelsDAO,
                                  channelReportsDAO: ChannelReportsDAO
                                ) {

  def create(name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: ChannelPrivacyType,
             authority: ChannelAuthorityType,
             sessionId: SessionId): Future[ChannelId] = {
    for {
      id <- channelsDAO.create(name, byInvitationOnly, privacyType, authority, sessionId)
      _ <- accountChannelsDAO.create(sessionId.toAccountId, id, sessionId)
    } yield (id)
  }

  def update(channelId: ChannelId,
             name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: ChannelPrivacyType,
             authority: ChannelAuthorityType,
             sessionId: SessionId): Future[Unit] = {

    for {
      _ <- accountChannelsValidator.mustJoined(sessionId.toAccountId, channelId)
      _ <- channelAuthorityValidator.hasUpdateAuthority(channelId, sessionId)
      _ <- channelsDAO.update(channelId, name, byInvitationOnly, privacyType, authority, sessionId)
    } yield (())
  }

  def find(channelId: ChannelId, sessionId: SessionId): Future[Channel] = {
    channelsValidator.mustFind(channelId, sessionId)
  }

  def report(channelId: ChannelId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- channelsValidator.mustExist(channelId, sessionId)
      _ <- channelReportsDAO.create(channelId, reportType, reportContent, sessionId)
    } yield (())
  }

}