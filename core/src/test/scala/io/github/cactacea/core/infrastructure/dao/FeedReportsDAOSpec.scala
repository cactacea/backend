package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.FeedReports

class FeedReportsDAOSpec extends DAOSpec {

  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]
  val feedReportsDAO: FeedReportsDAO = injector.instance[FeedReportsDAO]

  import db._

  test("create") {

    val sessionAccount1 = createAccount("FeedReportsDAOSpec1")
    val sessionAccount2 = createAccount("FeedReportsDAOSpec2")
    val sessionAccount3 = createAccount("FeedReportsDAOSpec3")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val reportId1 = Await.result(feedReportsDAO.create(feedId, ReportType.inappropriate, sessionAccount2.id.toSessionId))
    val reportId2 = Await.result(feedReportsDAO.create(feedId, ReportType.inappropriate, sessionAccount3.id.toSessionId))
    val result = Await.result(db.run(query[FeedReports].filter(_.feedId == lift(feedId)).sortBy(_.id)))
    assert(result.size == 2)
    val report1 = result(0)
    val report2 = result(1)
    assert(report1.feedId == feedId)
    assert(report2.feedId == feedId)
    assert(report1.by == sessionAccount2.id)
    assert(report2.by == sessionAccount3.id)
  }

}

