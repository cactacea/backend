package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationTweetsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{TweetId, UserId}

@Singleton
class PushNotificationTweetsRepository @Inject()(pushNotificationTweetsDAO: PushNotificationTweetsDAO) {

  def find(tweetId: TweetId): Future[Option[Seq[PushNotification]]] = {
    pushNotificationTweetsDAO.find(tweetId)
  }

  def update(tweetId: TweetId): Future[Unit] = {
    pushNotificationTweetsDAO.update(tweetId, true)
  }

  def update(tweetId: TweetId, userIds: Seq[UserId]): Future[Unit] = {
    pushNotificationTweetsDAO.update(tweetId, userIds, true)
  }

}
