package io.github.cactacea.backend.core.application.services

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Information
import io.github.cactacea.backend.core.domain.repositories.InformationssRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class InformationsService @Inject()(
                                     databaseService: DatabaseService,
                                     informationsRepository: InformationssRepository
                                    ) {
  import databaseService._

  def find(since: Option[Long], offset: Int, count: Int, locales: Seq[Locale], sessionId: SessionId): Future[Seq[Information]] = {
    transaction {
      for {
        n <- informationsRepository.find(since, offset, count, locales, sessionId)
        _ <- informationsRepository.updateReadStatus(n, sessionId)
      } yield (n)
    }
  }

}
