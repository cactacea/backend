package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.TimelineFeed
import io.github.cactacea.core.domain.repositories.TimelineRepository
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.SessionId

@Singleton
class TimelineService @Inject()(db: DatabaseService) {

  @Inject var timelineRepository: TimelineRepository = _

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[TimelineFeed]] = {
    timelineRepository.findAll(since, offset, count, sessionId)
  }


}
