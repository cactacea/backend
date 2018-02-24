package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers._

@Singleton
class ReportsRepository {

  @Inject var accountReportsDAO: AccountReportsDAO = _
  @Inject var feedReportsDAO: FeedReportsDAO = _
  @Inject var commentReportsDAO: CommentReportsDAO = _
  @Inject var groupReportsDAO: GroupReportsDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def createAccountReport(accountId: AccountId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existAccounts(accountId, sessionId)
      _ <- accountReportsDAO.create(accountId, reportType, sessionId)
    } yield (Future.value(Unit))
  }

  def createFeedReport(feedId: FeedId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existFeeds(feedId, sessionId)
      _ <- feedReportsDAO.create(feedId, reportType, sessionId)
    } yield (Future.value(Unit))
  }

  def createCommentReport(commentId: CommentId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existComments(commentId, sessionId)
      _ <- commentReportsDAO.create(commentId, reportType, sessionId)
    } yield (Future.value(Unit))
  }

  def createGroupReport(groupId: GroupId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existGroups(groupId)
      _ <- groupReportsDAO.create(groupId, reportType, sessionId)
    } yield (Future.value(Unit))
  }

}
