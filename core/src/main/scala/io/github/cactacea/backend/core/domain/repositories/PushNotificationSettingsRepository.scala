package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotificationSetting
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationSettingsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException


class PushNotificationSettingsRepository @Inject()(
                                                    notificationSettingsDAO: PushNotificationSettingsDAO) {

  def find(sessionId: SessionId): Future[PushNotificationSetting] = {
    notificationSettingsDAO.find(sessionId).flatMap(_ match {
      case Some(s) =>
        Future.value(PushNotificationSetting(s))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def update(feed: Boolean,
             comment: Boolean,
             friendRequest: Boolean,
             message: Boolean,
             groupMessage: Boolean,
             groupInvitation: Boolean,
             showMessage: Boolean,
             sessionId: SessionId): Future[Unit] = {

    notificationSettingsDAO.update(
      feed,
      comment,
      friendRequest,
      message,
      groupMessage,
      groupInvitation,
      showMessage,
      sessionId)
  }


}
