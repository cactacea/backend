package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationFeedsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, UserId}

@Singleton
class PushNotificationFeedsRepository @Inject()(pushNotificationFeedsDAO: PushNotificationFeedsDAO) {

  def find(feedId: FeedId): Future[Option[List[PushNotification]]] = {
    pushNotificationFeedsDAO.find(feedId)
  }

  def update(feedId: FeedId): Future[Unit] = {
    pushNotificationFeedsDAO.update(feedId, true)
  }

  def update(feedId: FeedId, userIds: List[UserId]): Future[Unit] = {
    pushNotificationFeedsDAO.update(feedId, userIds, true)
  }

}
