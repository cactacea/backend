package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.AdvertisementSetting
import io.github.cactacea.core.infrastructure.dao.AdvertisementSettingsDAO
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.util.responses.CactaceaErrors._
import io.github.cactacea.core.util.exceptions.CactaceaException

@Singleton
class AdvertisementSettingsRepository {

  @Inject private var advertisementSettingsDAO: AdvertisementSettingsDAO = _

  def find(sessionId: SessionId): Future[AdvertisementSetting] = {
    advertisementSettingsDAO.find(
      sessionId
    ).flatMap(_ match {
      case Some(s) =>
        Future.value(AdvertisementSetting(s))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def update(ad1: Boolean, ad2: Boolean, ad3: Boolean, ad4: Boolean, ad5: Boolean, sessionId: SessionId): Future[Unit] = {
    advertisementSettingsDAO.update(ad1, ad2, ad3, ad4, ad5, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

}
