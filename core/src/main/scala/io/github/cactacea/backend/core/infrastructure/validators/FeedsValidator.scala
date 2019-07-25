package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.dao.FeedsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.FeedNotFound

@Singleton
class FeedsValidator @Inject()(feedsDAO: FeedsDAO) {

  def find(feedId: FeedId, sessionId: SessionId): Future[Feed] = {
    feedsDAO.find(feedId, sessionId).flatMap(_ match {
      case Some(f) =>
        Future.value(f)
      case None =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

  def exist(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedsDAO.exist(feedId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

}

