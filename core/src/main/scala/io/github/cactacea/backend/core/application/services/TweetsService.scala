package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ReportType, TweetPrivacyType, TweetType}
import io.github.cactacea.backend.core.domain.models.Tweet
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId, TweetId, UserId}

@Singleton
class TweetsService @Inject()(
                              databaseService: DatabaseService,
                              tweetsRepository: TweetsRepository,
                              queueService: QueueService
                            ) {

  import databaseService._

  def create(message: String,
             mediumIds: Option[Seq[MediumId]],
             tags: Option[Seq[String]],
             privacyType: TweetPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[TweetId] = {

    transaction {
      for {
        i <- tweetsRepository.create(message, mediumIds, tags, privacyType, contentWarning, expiration, sessionId)
        _ <- queueService.enqueueTweet(i)
      } yield (i)
    }
  }

  def delete(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    transaction {
      tweetsRepository.delete(tweetId, sessionId)
    }
  }

  def edit(tweetId: TweetId,
           message: String,
           mediumIds: Option[Seq[MediumId]],
           tags: Option[Seq[String]],
           privacyType: TweetPrivacyType,
           contentWarning: Boolean,
           expiration: Option[Long],
           sessionId: SessionId): Future[Unit] = {

    transaction {
      tweetsRepository.update(tweetId, message, mediumIds, tags, privacyType, contentWarning, expiration, sessionId)
    }
  }

  def find(since: Option[Long], offset: Int, count: Int, privacyType: Option[TweetPrivacyType], tweetType: TweetType, sessionId: SessionId): Future[Seq[Tweet]] = {
    tweetsRepository.find(since, offset, count, privacyType, tweetType, sessionId)
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Tweet]] = {
    tweetsRepository.find(userId, since, offset, count, sessionId)
  }

  def find(tweetId: TweetId, sessionId: SessionId): Future[Tweet] = {
    tweetsRepository.find(tweetId, sessionId)
  }

  def report(tweetId: TweetId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      tweetsRepository.report(tweetId, reportType, reportContent, sessionId)
    }
  }

}

