package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.{Tweet, User}
import io.github.cactacea.backend.core.infrastructure.dao.TweetLikesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{TweetId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{TweetLikesValidator, TweetsValidator, UsersValidator}

@Singleton
class TweetLikesRepository @Inject()(
                                      usersValidator: UsersValidator,
                                      tweetsValidator: TweetsValidator,
                                      tweetLikesValidator: TweetLikesValidator,
                                      tweetLikesDAO: TweetLikesDAO
                                   ) {

  def create(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- tweetsValidator.mustExist(tweetId, sessionId)
      _ <- tweetLikesValidator.mustNotLiked(tweetId, sessionId)
      _ <- tweetLikesDAO.create(tweetId, sessionId)
    } yield (())
  }

  def delete(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- tweetsValidator.mustExist(tweetId, sessionId)
      _ <- tweetLikesValidator.mustLiked(tweetId, sessionId)
      _ <- tweetLikesDAO.delete(tweetId, sessionId)
    } yield (())
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Tweet]] = {
    for {
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- tweetLikesDAO.find(userId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Tweet]] = {
    tweetLikesDAO.find(sessionId.userId, since, offset, count, sessionId)
  }

  def findUsers(tweetId: TweetId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[User]] = {
    for {
      _ <- tweetsValidator.mustExist(tweetId, sessionId)
      r <- tweetLikesDAO.findUsers(tweetId, since, offset, count, sessionId)
    } yield (r)
  }

}
