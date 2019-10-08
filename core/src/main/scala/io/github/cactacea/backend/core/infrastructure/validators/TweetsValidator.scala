package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Tweet
import io.github.cactacea.backend.core.infrastructure.dao.{CommentsDAO, TweetsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AuthorityNotFound, CommentNotFound, TweetNotFound}

@Singleton
class TweetsValidator @Inject()(tweetsDAO: TweetsDAO, commentsDAO: CommentsDAO) {

  def mustFind(tweetId: TweetId, sessionId: SessionId): Future[Tweet] = {
    tweetsDAO.find(tweetId, sessionId).flatMap(_ match {
      case Some(f) =>
        Future.value(f)
      case None =>
        Future.exception(CactaceaException(TweetNotFound))
    })
  }

  def mustExist(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    tweetsDAO.exists(tweetId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(TweetNotFound))
    })
  }

  def mustFindOwner(tweetId: TweetId, commentId: Option[CommentId]): Future[UserId] = {
    commentId match {
      case Some(id) =>
        commentsDAO.findOwner(id).flatMap(_ match {
          case Some(a) => Future.value(a)
          case None => Future.exception(CactaceaException(CommentNotFound))
        })
      case None =>
        tweetsDAO.findOwner(tweetId).flatMap(_ match {
          case Some(a) => Future.value(a)
          case None => Future.exception(CactaceaException(TweetNotFound))
        })
    }
  }

  def mustOwn(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    tweetsDAO.own(tweetId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AuthorityNotFound))
    })
  }


}

