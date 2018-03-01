package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.PushNotificationSetting
import io.github.cactacea.core.infrastructure.dao.PushNotificationSettingsDAO
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException

@Singleton
class PushNotificationSettingsRepository {

  @Inject var notificationSettingsDAO: PushNotificationSettingsDAO = _

  def find(sessionId: SessionId): Future[PushNotificationSetting] = {
    notificationSettingsDAO.find(sessionId).flatMap(_ match {
      case Some(s) =>
        Future.value(PushNotificationSetting(s))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def update(groupInvite: Boolean,
             followerFeed: Boolean,
             feedComment: Boolean,
             groupMessage: Boolean,
             directMessage: Boolean,
             showMessage: Boolean,
             sessionId: SessionId): Future[Unit] = {
    notificationSettingsDAO.update(
      groupInvite,
      followerFeed,
      feedComment,
      groupMessage,
      directMessage,
      showMessage,
      sessionId
    ).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })

  }


}
