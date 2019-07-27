package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.FeedReports

class FeedReportsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    forAll(accountGen, accountGen, feedGen, feedReportGen, feedReportGen) { (a1, a2, f, r1, r2) =>
      val accountId1 = await(accountsDAO.create(a1.accountName))
      val accountId2 = await(accountsDAO.create(a2.accountName))
      val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, accountId1.toSessionId))
      await(feedReportsDAO.create(feedId, r1.reportType, r1.reportContent, accountId1.toSessionId))
      await(feedReportsDAO.create(feedId, r2.reportType, r2.reportContent, accountId2.toSessionId))
      val result = await(db.run(query[FeedReports].filter(_.feedId == lift(feedId)).sortBy(_.id)))
      assert(result.size == 2)
      assert(result(0).feedId == feedId)
      assert(result(0).by == accountId1)
      assert(result(0).reportType == r1.reportType)
      assert(result(0).reportContent == r1.reportContent)
      assert(result(1).feedId == feedId)
      assert(result(1).by == accountId2)
      assert(result(1).reportType == r2.reportType)
      assert(result(1).reportContent == r2.reportContent)
    }
  }

  feature("delete") {

    scenario("should delete a feed reports") {
      forAll(accountGen, accountGen, feedGen, feedReportGen, feedReportGen) { (a1, a2, f, r1, r2) =>

        // preparing
        //  session account create a feed
        //  session account create a feed
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a2.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // create some reports each account
        await(feedReportsDAO.create(feedId, r1.reportType, r1.reportContent, sessionId))
        await(feedReportsDAO.create(feedId, r2.reportType, r2.reportContent, accountId.toSessionId))

        // delete the feed
        await(feedReportsDAO.delete(feedId))

        // should no return
        val result = await(db.run(query[FeedReports].filter(_.feedId == lift(feedId)).sortBy(_.id)))
        assert(result.size == 0)
      }
    }

  }
  
}

