package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.CommentReports

class CommentReportsDAOSpec extends DAOSpec {

  import db._

  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]
  val commentsDAO: CommentsDAO = injector.instance[CommentsDAO]
  val commentReportsDAO: CommentReportsDAO = injector.instance[CommentReportsDAO]

  test("create") {

    val sessionAccount1 = createAccount(0L)
    val sessionAccount2 = createAccount(1L)

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.followers, false, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "0123456789" * 100, sessionAccount1.id.toSessionId))
    val commentReportId = Await.result(commentReportsDAO.create(commentId, ReportType.spam, sessionAccount2.id.toSessionId))

    val result = Await.result(db.run(query[CommentReports].filter(_.id == lift(commentReportId)))).head
    assert(result.id == commentReportId)
    assert(result.by == sessionAccount2.id)
    assert(result.commentId == commentId)
    assert(result.reportType == ReportType.spam.toValue)
  }

}

