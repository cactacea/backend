package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, MediumId, SessionId}

class FeedsService @Inject()(
                              db: DatabaseService,
                              feedsRepository: FeedsRepository,
                              reportsRepository: ReportsRepository,
                              queueService: QueueService
                            ) {

  def create(message: String,
             mediumIds: Option[List[MediumId]],
             tags: Option[List[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[FeedId] = {

    for {
      id <- db.transaction(feedsRepository.create(message, mediumIds, tags, privacyType, contentWarning, expiration, sessionId))
      _ <- queueService.enqueueFeed(id)
    } yield (id)

  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(feedsRepository.delete(feedId, sessionId))
    } yield (())
  }

  def edit(feedId: FeedId,
           message: String,
           mediumIds: Option[List[MediumId]],
           tags: Option[List[String]],
           privacyType: FeedPrivacyType,
           contentWarning: Boolean,
           expiration: Option[Long],
           sessionId: SessionId): Future[Unit] = {

    db.transaction {
      feedsRepository.update(
        feedId,
        message,
        mediumIds,
        tags,
        privacyType,
        contentWarning,
        expiration,
        sessionId
      )
    }
  }

  def find(since: Option[Long], offset: Int, count: Int, privacyType: Option[FeedPrivacyType], sessionId: SessionId): Future[List[Feed]] = {
    feedsRepository.find(since, offset, count, privacyType, sessionId)
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedsRepository.find(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedsRepository.find(since, offset, count, sessionId)
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Feed] = {
    feedsRepository.find(
      feedId,
      sessionId
    )
  }

  def report(feedId: FeedId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    db.transaction {
      reportsRepository.createFeedReport(feedId, reportType, reportContent, sessionId)
    }
  }

}

