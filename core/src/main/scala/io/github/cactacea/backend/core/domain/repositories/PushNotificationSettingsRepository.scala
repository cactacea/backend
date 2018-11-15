package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotificationSetting
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationSettingsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

@Singleton
class PushNotificationSettingsRepository {

  @Inject private var notificationSettingsDAO: PushNotificationSettingsDAO = _

  def find(sessionId: SessionId): Future[PushNotificationSetting] = {
    notificationSettingsDAO.find(sessionId).flatMap(_ match {
      case Some(s) =>
        Future.value(PushNotificationSetting(s))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def update(groupInvitation: Boolean, followerFeed: Boolean, feedComment: Boolean, groupMessage: Boolean, directMessage: Boolean, showMessage: Boolean, sessionId: SessionId): Future[Unit] = {
    notificationSettingsDAO.update(
      groupInvitation,
      followerFeed,
      feedComment,
      groupMessage,
      directMessage,
      showMessage,
      sessionId
    )
  }


}
