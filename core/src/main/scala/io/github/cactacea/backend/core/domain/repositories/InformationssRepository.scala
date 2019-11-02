package io.github.cactacea.backend.core.domain.repositories

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Information
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class InformationssRepository @Inject()(
                                         informationsDAO: InformationsDAO
                                       ) {

  def find(since: Option[Long], offset: Int, count: Int, locales: Seq[Locale], sessionId: SessionId): Future[Seq[Information]] = {
    informationsDAO.find(since, offset, count, locales, sessionId)
  }

  def updateReadStatus(notifications: Seq[Information], sessionId: SessionId): Future[Unit] = {
    if (notifications.size == 0) {
      Future.Unit
    } else {
      informationsDAO.updateReadStatus(notifications.map(_.id), sessionId)
    }
  }

}
