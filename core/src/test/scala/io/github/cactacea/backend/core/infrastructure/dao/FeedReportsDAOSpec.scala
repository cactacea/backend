package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.FeedReports

class FeedReportsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    forAll(userGen, userGen, feedGen, feedReportGen, feedReportGen) { (a1, a2, f, r1, r2) =>
      val userId1 = await(usersDAO.create(a1.userName))
      val userId2 = await(usersDAO.create(a2.userName))
      val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, userId1.sessionId))
      await(feedReportsDAO.create(feedId, r1.reportType, r1.reportContent, userId1.sessionId))
      await(feedReportsDAO.create(feedId, r2.reportType, r2.reportContent, userId2.sessionId))
      val result = await(db.run(query[FeedReports].filter(_.feedId == lift(feedId)).sortBy(_.id)))
      assert(result.size == 2)
      assert(result(0).feedId == feedId)
      assert(result(0).by == userId1)
      assert(result(0).reportType == r1.reportType)
      assert(result(0).reportContent == r1.reportContent)
      assert(result(1).feedId == feedId)
      assert(result(1).by == userId2)
      assert(result(1).reportType == r2.reportType)
      assert(result(1).reportContent == r2.reportContent)
    }
  }

  feature("delete") {

    scenario("should delete a feed reports") {
      forAll(userGen, userGen, feedGen, feedReportGen, feedReportGen) { (a1, a2, f, r1, r2) =>

        // preparing
        //  session user create a feed
        //  session user create a feed
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // create some reports each user
        await(feedReportsDAO.create(feedId, r1.reportType, r1.reportContent, sessionId))
        await(feedReportsDAO.create(feedId, r2.reportType, r2.reportContent, userId.sessionId))

        // delete the feed
        await(feedReportsDAO.delete(feedId))

        // should no return
        val result = await(db.run(query[FeedReports].filter(_.feedId == lift(feedId)).sortBy(_.id)))
        assert(result.size == 0)
      }
    }

  }
  
}

