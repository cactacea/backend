package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class ReportsRepository {

  @Inject private var accountReportsDAO: AccountReportsDAO = _
  @Inject private var feedReportsDAO: FeedReportsDAO = _
  @Inject private var commentReportsDAO: CommentReportsDAO = _
  @Inject private var groupReportsDAO: GroupReportsDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def createAccountReport(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      id <- accountReportsDAO.create(accountId, reportType, reportContent, sessionId)
    } yield (Future.value(Unit))
  }

  def createFeedReport(feedId: FeedId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existFeed(feedId, sessionId)
      _ <- feedReportsDAO.create(feedId, reportType, reportContent, sessionId)
    } yield (Future.value(Unit))
  }

  def createCommentReport(commentId: CommentId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existComment(commentId, sessionId)
      _ <- commentReportsDAO.create(commentId, reportType, reportContent, sessionId)
    } yield (Future.value(Unit))
  }

  def createGroupReport(groupId: GroupId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existGroup(groupId, sessionId)
      _ <- groupReportsDAO.create(groupId, reportType, reportContent, sessionId)
    } yield (Future.value(Unit))
  }

}
