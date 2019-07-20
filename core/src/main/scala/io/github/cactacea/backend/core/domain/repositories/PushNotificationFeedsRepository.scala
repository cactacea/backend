package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationFeedsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId}


class PushNotificationFeedsRepository @Inject()(pushNotificationFeedsDAO: PushNotificationFeedsDAO) {

  def find(feedId: FeedId): Future[Option[List[PushNotification]]] = {
    pushNotificationFeedsDAO.find(feedId)
  }

  def update(feedId: FeedId): Future[Unit] = {
    pushNotificationFeedsDAO.update(feedId, true)
  }

  def update(feedId: FeedId, accountIds: List[AccountId]): Future[Unit] = {
    pushNotificationFeedsDAO.update(feedId, accountIds, true)
  }

}
