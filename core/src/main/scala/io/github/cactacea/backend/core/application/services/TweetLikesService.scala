package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.{Tweet, User}
import io.github.cactacea.backend.core.domain.repositories.TweetLikesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{TweetId, SessionId, UserId}

@Singleton
class TweetLikesService @Inject()(
                                  databaseService: DatabaseService,
                                  tweetLikesRepository: TweetLikesRepository
                                ) {

  import databaseService._

  def create(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    transaction {
      tweetLikesRepository.create(tweetId, sessionId)
    }
  }

  def delete(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    transaction {
      tweetLikesRepository.delete(tweetId, sessionId)
    }
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Tweet]] = {
    tweetLikesRepository.find(userId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Tweet]] = {
    tweetLikesRepository.find(since, offset, count, sessionId)
  }

  def findUsers(tweetId: TweetId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[User]] = {
    tweetLikesRepository.findUsers(tweetId, since, offset, count, sessionId)
  }

}
