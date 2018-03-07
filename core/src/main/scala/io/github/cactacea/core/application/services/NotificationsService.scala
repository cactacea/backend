package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.models.Notification
import io.github.cactacea.core.domain.repositories.NotificationsRepository
import io.github.cactacea.core.infrastructure.identifiers.SessionId

class NotificationsService {

  @Inject private var db: DatabaseService = _
  @Inject private var notificationsRepository: NotificationsRepository = _

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Notification]] = {
    db.transaction {
      for {
        n <- notificationsRepository.findAll(since, offset, count, sessionId)
        _ <- notificationsRepository.updateReadStatus(n, sessionId)
      } yield (n)
    }
  }

}
