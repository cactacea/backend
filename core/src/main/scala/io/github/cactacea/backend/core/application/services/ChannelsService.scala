package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, SessionId}

@Singleton
class ChannelsService @Inject()(
                               databaseService: DatabaseService,
                               channelsRepository: ChannelsRepository
                             ) {

  import databaseService._

  def create(name: String, byInvitationOnly: Boolean, privacyType: ChannelPrivacyType,
             authority: ChannelAuthorityType, sessionId: SessionId): Future[ChannelId] = {
    transaction {
      channelsRepository.create(Some(name), byInvitationOnly, privacyType, authority, sessionId)
    }
  }

  def update(channelId: ChannelId,
             name: String,
             invitationOnly: Boolean,
             privacyType: ChannelPrivacyType,
             authority: ChannelAuthorityType,
             sessionId: SessionId): Future[Unit] = {

    transaction {
      channelsRepository.update(channelId, Some(name), invitationOnly, privacyType, authority, sessionId)
    }
  }

  def find(channelId: ChannelId, sessionId: SessionId): Future[Channel] = {
    channelsRepository.find(channelId, sessionId)
  }

  def report(channelId: ChannelId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      channelsRepository.report(channelId, reportType, reportContent, sessionId)
    }
  }

}
