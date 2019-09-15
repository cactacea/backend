package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.infrastructure.dao.NotificationTweetsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{TweetId, UserId}

@Singleton
class NotificationTweetsRepository @Inject()(notificationTweetsDAO: NotificationTweetsDAO) {

  def find(tweetId: TweetId): Future[Option[Seq[Notification]]] = {
    notificationTweetsDAO.find(tweetId)
  }

  def update(tweetId: TweetId): Future[Unit] = {
    notificationTweetsDAO.update(tweetId, true)
  }

  def update(tweetId: TweetId, userIds: Seq[UserId]): Future[Unit] = {
    notificationTweetsDAO.update(tweetId, userIds, true)
  }

}
