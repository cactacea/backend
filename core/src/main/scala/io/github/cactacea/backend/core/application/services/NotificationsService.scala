package io.github.cactacea.backend.core.application.services

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.domain.repositories.NotificationsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class NotificationsService @Inject()(
                                      db: DatabaseService,
                                      notificationsRepository: NotificationsRepository
                                    ) {

  def find(since: Option[Long], offset: Int, count: Int, locales: Seq[Locale], sessionId: SessionId): Future[List[Notification]] = {
    db.transaction {
      for {
        n <- notificationsRepository.find(since, offset, count, locales, sessionId)
        _ <- notificationsRepository.updateReadStatus(n, sessionId)
      } yield (n)
    }
  }

}
