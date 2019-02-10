package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, CommentsValidator, FeedsValidator, GroupsValidator}

@Singleton
class ReportsRepository @Inject()(
                                   accountsValidator: AccountsValidator,
                                   accountReportsDAO: AccountReportsDAO,
                                   commentsValidator: CommentsValidator,
                                   feedsValidator: FeedsValidator,
                                   groupsValidator: GroupsValidator,
                                   commentReportsDAO: CommentReportsDAO,
                                   feedReportsDAO: FeedReportsDAO,
                                   groupReportsDAO: GroupReportsDAO
                                 ) {

  def createAccountReport(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountReportsDAO.create(accountId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

  def createFeedReport(feedId: FeedId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsValidator.exist(feedId, sessionId)
      _ <- feedReportsDAO.create(feedId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

  def createCommentReport(commentId: CommentId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsValidator.exist(commentId, sessionId)
      _ <- commentReportsDAO.create(commentId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

  def createGroupReport(groupId: GroupId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- groupsValidator.exist(groupId, sessionId)
      _ <- groupReportsDAO.create(groupId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

}
