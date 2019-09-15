package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.TweetReports

class TweetReportsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    forAll(userGen, userGen, tweetGen, tweetReportGen, tweetReportGen) { (a1, a2, f, r1, r2) =>
      val userId1 = await(usersDAO.create(a1.userName))
      val userId2 = await(usersDAO.create(a2.userName))
      val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, userId1.sessionId))
      await(tweetReportsDAO.create(tweetId, r1.reportType, r1.reportContent, userId1.sessionId))
      await(tweetReportsDAO.create(tweetId, r2.reportType, r2.reportContent, userId2.sessionId))
      val result = await(db.run(query[TweetReports].filter(_.tweetId == lift(tweetId)).sortBy(_.id)))
      assert(result.size == 2)
      assert(result(0).tweetId == tweetId)
      assert(result(0).by == userId1)
      assert(result(0).reportType == r1.reportType)
      assert(result(0).reportContent == r1.reportContent)
      assert(result(1).tweetId == tweetId)
      assert(result(1).by == userId2)
      assert(result(1).reportType == r2.reportType)
      assert(result(1).reportContent == r2.reportContent)
    }
  }

  feature("delete") {

    scenario("should delete a tweet reports") {
      forAll(userGen, userGen, tweetGen, tweetReportGen, tweetReportGen) { (a1, a2, f, r1, r2) =>

        // preparing
        //  session user create a tweet
        //  session user create a tweet
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // create some reports each user
        await(tweetReportsDAO.create(tweetId, r1.reportType, r1.reportContent, sessionId))
        await(tweetReportsDAO.create(tweetId, r2.reportType, r2.reportContent, userId.sessionId))

        // delete the tweet
        await(tweetReportsDAO.delete(tweetId))

        // should no return
        val result = await(db.run(query[TweetReports].filter(_.tweetId == lift(tweetId)).sortBy(_.id)))
        assert(result.size == 0)
      }
    }

  }
  
}

