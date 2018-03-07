package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{InjectionService, SocialAccountsService}
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.models.{AdvertisementSetting, PushNotificationSetting, SocialAccount}
import io.github.cactacea.core.domain.repositories.{AdvertisementSettingsRepository, PushNotificationSettingsRepository, SocialAccountsRepository}
import io.github.cactacea.core.infrastructure.identifiers.SessionId

class SettingsService {

  @Inject private var notificationSettingsRepository: PushNotificationSettingsRepository = _
  @Inject private var socialAccountsRepository: SocialAccountsRepository = _
  @Inject private var injectionService: InjectionService = _
  @Inject private var socialAccountsService: SocialAccountsService = _
  @Inject private var db: DatabaseService = _

  def findPushNotificationSettings(sessionId: SessionId): Future[PushNotificationSetting] = {
    notificationSettingsRepository.find(sessionId)
  }

  def updatePushNotificationSettings(setting: PushNotificationSetting, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      notificationSettingsRepository.update(
        setting.groupInvitation,
        setting.followerFeed,
        setting.feedComment,
        setting.groupMessage,
        setting.directMessage,
        setting.showMessage,
        sessionId)
    }
  }

  @Inject private var advertisementSettingsRepository: AdvertisementSettingsRepository = _

  def findAdvertisementSettings(sessionId: SessionId): Future[AdvertisementSetting] = {
    advertisementSettingsRepository.find(sessionId)
  }

  def updateAdvertisementSettings(setting: AdvertisementSetting, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      advertisementSettingsRepository.update(setting.ad1, setting.ad2, setting.ad3, setting.ad4, setting.ad5, sessionId)
    }
  }


  def connectSocialAccount(socialAccountType: String, accessTokenKey: String, accessTokenSecret: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- socialAccountsService.get(socialAccountType, accessTokenKey, accessTokenSecret)
      r <- db.transaction(socialAccountsRepository.create(socialAccountType, accessTokenKey, sessionId))
      _ <- injectionService.socialAccountConnected(socialAccountType, sessionId)
    } yield (r)
  }

  def disconnectSocialAccount(socialAccountType: String, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(socialAccountsRepository.delete(socialAccountType, sessionId))
      _ <- injectionService.socialAccountDisconnected(socialAccountType, sessionId)
    } yield (r)
  }

  def findSocialAccounts(sessionId: SessionId): Future[List[SocialAccount]] = {
    socialAccountsRepository.findAll(sessionId)
  }

}
