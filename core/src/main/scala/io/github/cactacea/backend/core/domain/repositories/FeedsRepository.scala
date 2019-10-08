package io.github.cactacea.backend.core.domain.repositories

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class FeedsRepository @Inject()(
                                         feedsDAO: FeedsDAO
                                       ) {

  def find(since: Option[Long], offset: Int, count: Int, locales: Seq[Locale], sessionId: SessionId): Future[Seq[Feed]] = {
    feedsDAO.find(since, offset, count, locales, sessionId)
  }

  def updateReadStatus(notifications: Seq[Feed], sessionId: SessionId): Future[Unit] = {
    if (notifications.size == 0) {
      Future.Unit
    } else {
      feedsDAO.updateReadStatus(notifications.map(_.id), sessionId)
    }
  }

}
