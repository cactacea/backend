package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.FeedLikesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{FeedAlreadyLiked, FeedNotLiked}

@Singleton
class FeedLikesValidator @Inject()(feedLikesDAO: FeedLikesDAO) {

  def notExist(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedLikesDAO.exist(feedId, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(FeedAlreadyLiked))
    })
  }


  def exist(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedLikesDAO.exist(feedId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(FeedNotLiked))
      case true =>
        Future.Unit
    })
  }

}
