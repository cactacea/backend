package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.TweetLikesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{TweetAlreadyLiked, TweetNotLiked}

@Singleton
class TweetLikesValidator @Inject()(tweetLikesDAO: TweetLikesDAO) {

  def mustNotLiked(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    tweetLikesDAO.own(tweetId, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(TweetAlreadyLiked))
    })
  }


  def mustLiked(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    tweetLikesDAO.own(tweetId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(TweetNotLiked))
      case true =>
        Future.Unit
    })
  }

}
