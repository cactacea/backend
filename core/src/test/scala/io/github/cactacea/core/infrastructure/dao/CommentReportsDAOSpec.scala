package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.CommentReports

class CommentReportsDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount1 = createAccount("CommentReportsDAOSpec1")
    val sessionAccount2 = createAccount("CommentReportsDAOSpec2")

    val feedId = execute(feedsDAO.create("message", None, None, FeedPrivacyType.followers, false, None, sessionAccount1.id.toSessionId))
    val commentId = execute(commentsDAO.create(feedId, "0123456789" * 100, sessionAccount1.id.toSessionId))
    val reportContent = Some("report content")
    val commentReportId = execute(commentReportsDAO.create(commentId, ReportType.spam, reportContent, sessionAccount2.id.toSessionId))

    val result = execute(db.run(query[CommentReports].filter(_.id == lift(commentReportId)))).head
    assert(result.id == commentReportId)
    assert(result.by == sessionAccount2.id)
    assert(result.commentId == commentId)
    assert(result.reportType == ReportType.spam)
  }

}

