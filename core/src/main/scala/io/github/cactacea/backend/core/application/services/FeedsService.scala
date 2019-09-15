package io.github.cactacea.backend.core.application.services

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.domain.repositories.FeedsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class FeedsService @Inject()(
                              databaseService: DatabaseService,
                              feedsRepository: FeedsRepository
                                    ) {
  import databaseService._

  def find(since: Option[Long], offset: Int, count: Int, locales: Seq[Locale], sessionId: SessionId): Future[Seq[Feed]] = {
    transaction {
      for {
        n <- feedsRepository.find(since, offset, count, locales, sessionId)
        _ <- feedsRepository.updateReadStatus(n, sessionId)
      } yield (n)
    }
  }

}
