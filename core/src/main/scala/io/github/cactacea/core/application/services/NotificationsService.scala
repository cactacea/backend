package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Notification
import io.github.cactacea.core.domain.repositories.NotificationsRepository
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.services.DatabaseService

class NotificationsService @Inject()(db: DatabaseService, notificationsRepository: NotificationsRepository) {

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Notification]] = {
    db.transaction {
      for {
        n <- notificationsRepository.findAll(since, offset, count, sessionId)
        _ <- notificationsRepository.updateReadStatus(n, sessionId)
      } yield (n)
    }
  }

}
