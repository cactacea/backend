package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.factories.TimelineFactory
import io.github.cactacea.core.domain.models.TimelineFeed
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.SessionId

@Singleton
class TimelineRepository {

  @Inject var timelineDAO: TimeLineDAO = _

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[TimelineFeed]] = {
    timelineDAO.findAll(since, offset, count, sessionId).map(_.map(t =>
      TimelineFactory.create(t._1, t._2, t._3, t._4, t._5, t._6)
    ))
  }


}
