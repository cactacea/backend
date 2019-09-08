package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, MediumId, SessionId, UserId}

@Singleton
class FeedsService @Inject()(
                              databaseService: DatabaseService,
                              feedsRepository: FeedsRepository,
                              queueService: QueueService
                            ) {

  import databaseService._

  def create(message: String,
             mediumIds: Option[Seq[MediumId]],
             tags: Option[Seq[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[FeedId] = {

    transaction {
      for {
        i <- feedsRepository.create(message, mediumIds, tags, privacyType, contentWarning, expiration, sessionId)
        _ <- queueService.enqueueFeed(i)
      } yield (i)
    }
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    transaction {
      feedsRepository.delete(feedId, sessionId)
    }
  }

  def edit(feedId: FeedId,
           message: String,
           mediumIds: Option[Seq[MediumId]],
           tags: Option[Seq[String]],
           privacyType: FeedPrivacyType,
           contentWarning: Boolean,
           expiration: Option[Long],
           sessionId: SessionId): Future[Unit] = {

    transaction {
      feedsRepository.update(feedId, message, mediumIds, tags, privacyType, contentWarning, expiration, sessionId)
    }
  }

  def find(since: Option[Long], offset: Int, count: Int, privacyType: Option[FeedPrivacyType], sessionId: SessionId): Future[Seq[Feed]] = {
    feedsRepository.find(since, offset, count, privacyType, sessionId)
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Feed]] = {
    feedsRepository.find(userId, since, offset, count, sessionId)
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Feed] = {
    feedsRepository.find(feedId, sessionId)
  }

  def report(feedId: FeedId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      feedsRepository.report(feedId, reportType, reportContent, sessionId)
    }
  }

}

