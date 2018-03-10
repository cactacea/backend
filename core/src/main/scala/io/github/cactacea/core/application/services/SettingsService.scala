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

  def updatePushNotificationSettings(groupInvitation: Boolean, followerFeed: Boolean, feedComment: Boolean, groupMessage: Boolean, directMessage: Boolean, showMessage: Boolean, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      notificationSettingsRepository.update(
        groupInvitation,
        followerFeed,
        feedComment,
        groupMessage,
        directMessage,
        showMessage,
        sessionId)
    }
  }

  @Inject private var advertisementSettingsRepository: AdvertisementSettingsRepository = _

  def findAdvertisementSettings(sessionId: SessionId): Future[AdvertisementSetting] = {
    advertisementSettingsRepository.find(sessionId)
  }

  def updateAdvertisementSettings(ad1: Boolean, ad2: Boolean, ad3: Boolean, ad4: Boolean, ad5: Boolean, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      advertisementSettingsRepository.update(
        ad1,
        ad2,
        ad3,
        ad4,
        ad5,
        sessionId)
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
