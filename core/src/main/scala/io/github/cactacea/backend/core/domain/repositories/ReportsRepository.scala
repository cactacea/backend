package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class ReportsRepository @Inject()(
                                   accountsDAO: AccountsDAO,
                                   accountReportsDAO: AccountReportsDAO,
                                   commentsDAO: CommentsDAO,
                                   commentReportsDAO: CommentReportsDAO,
                                   feedsDAO: FeedsDAO,
                                   feedReportsDAO: FeedReportsDAO,
                                   groupsDAO: GroupsDAO,
                                   groupReportsDAO: GroupReportsDAO
                                 ) {

  def createAccountReport(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountReportsDAO.create(accountId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

  def createFeedReport(feedId: FeedId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsDAO.validateExist(feedId, sessionId)
      _ <- feedReportsDAO.create(feedId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

  def createCommentReport(commentId: CommentId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsDAO.validateExist(commentId, sessionId)
      _ <- commentReportsDAO.create(commentId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

  def createGroupReport(groupId: GroupId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- groupsDAO.validateExist(groupId, sessionId)
      _ <- groupReportsDAO.create(groupId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

}
