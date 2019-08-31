package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, FeedsValidator, MediumsValidator}


class FeedsRepository @Inject()(
                                 accountsValidator: AccountsValidator,
                                 accountFeedsDAO: AccountFeedsDAO,
                                 feedsDAO: FeedsDAO,
                                 feedTagsDAO: FeedTagsDAO,
                                 feedMediumsDAO: FeedMediumsDAO,
                                 feedReportsDAO: FeedReportsDAO,
                                 feedsValidator: FeedsValidator,
                                 mediumsValidator: MediumsValidator,
                                 notificationsDAO: NotificationsDAO
                               ) {

  def create(message: String,
             mediumIds: Option[List[MediumId]],
             tags: Option[List[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[FeedId] = {

    val ids = mediumIds.map(_.distinct)
    for {
      _ <- mediumsValidator.mustExist(ids, sessionId)
      i <- feedsDAO.create(message, ids, tags, privacyType, contentWarning, expiration, sessionId)
      _ <- feedTagsDAO.create(i, tags)
      _ <- feedMediumsDAO.create(i, mediumIds)
      _ <- accountFeedsDAO.create(i, sessionId)
      _ <- notificationsDAO.create(i, sessionId)
    } yield (i)
  }

  def update(feedId: FeedId,
             message: String,
             mediumIds: Option[List[MediumId]],
             tags: Option[List[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[Unit] = {

    val ids = mediumIds.map(_.distinct)
    for {
      _ <- feedsValidator.mustOwn(feedId, sessionId)
      _ <- mediumsValidator.mustExist(ids, sessionId)
      _ <- feedTagsDAO.delete(feedId)
      _ <- feedMediumsDAO.delete(feedId)
      _ <- feedsDAO.update(feedId, message, ids, tags, privacyType, contentWarning, expiration, sessionId)
      _ <- feedTagsDAO.create(feedId, tags)
      _ <- feedMediumsDAO.create(feedId, mediumIds)
    } yield (())
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsValidator.mustOwn(feedId, sessionId)
      _ <- feedsDAO.delete(feedId, sessionId)
    } yield (())
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- accountsValidator.mustExist(accountId, sessionId)
      r <- feedsDAO.find(accountId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, privacyType: Option[FeedPrivacyType], sessionId: SessionId): Future[List[Feed]] = {
    accountFeedsDAO.find(since, offset, count, privacyType, sessionId)
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Feed] = {
    feedsValidator.mustFind(feedId, sessionId)
  }

  def report(feedId: FeedId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsValidator.mustExist(feedId, sessionId)
      _ <- feedReportsDAO.create(feedId, reportType, reportContent, sessionId)
    } yield (())
  }

}
