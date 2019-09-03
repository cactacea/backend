package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.CommentReports

class CommentReportsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should report a comment") {
      forAll(userGen, userGen, feedGen, commentGen, commentReportGen, commentReportGen) { (a1, a2, f, c, r1, r2) =>

        // preparing
        //  session user create a feed
        //  session user create a comment
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))

        // create some reports each user
        await(commentReportsDAO.create(commentId, r1.reportType, r1.reportContent, sessionId))
        await(commentReportsDAO.create(commentId, r2.reportType, r2.reportContent, userId.sessionId))

        // should return 2 reports
        val result = await(db.run(query[CommentReports].filter(_.commentId == lift(commentId)).sortBy(_.id)))
        assert(result.size == 2)
        assert(result(0).commentId == commentId)
        assert(result(0).by == sessionId.userId)
        assert(result(0).reportType == r1.reportType)
        assert(result(0).reportContent == r1.reportContent)
        assert(result(1).commentId == commentId)
        assert(result(1).by == userId)
        assert(result(1).reportType == r2.reportType)
        assert(result(1).reportContent == r2.reportContent)
      }
    }

  }

  feature("delete") {

    scenario("should delete a comment reports") {
      forAll(userGen, userGen, feedGen, commentGen, commentReportGen, commentReportGen) { (a1, a2, f, c, r1, r2) =>

        // preparing
        //  session user create a feed
        //  session user create a comment
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))

        // create some reports each user
        await(commentReportsDAO.create(commentId, r1.reportType, r1.reportContent, sessionId))
        await(commentReportsDAO.create(commentId, r2.reportType, r2.reportContent, userId.sessionId))

        // delete the comment
        await(commentReportsDAO.delete(commentId))

        // should no return
        val result = await(db.run(query[CommentReports].filter(_.commentId == lift(commentId)).size))
        assert(result == 0)
      }
    }

    scenario("should delete a comment reports when a feed deleted") {
      forAll(userGen, userGen, feedGen, commentGen, commentReportGen, commentReportGen) { (a1, a2, f, c, r1, r2) =>

        // preparing
        //  session user create a feed
        //  session user create a comment
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))

        // create some reports each user
        await(commentReportsDAO.create(commentId, r1.reportType, r1.reportContent, sessionId))
        await(commentReportsDAO.create(commentId, r2.reportType, r2.reportContent, userId.sessionId))

        // delete the comment
        await(commentsDAO.delete(feedId, commentId, sessionId))

        // should no return
        val result = await(db.run(query[CommentReports].filter(_.commentId == lift(commentId)).size))
        assert(result == 0)
      }
    }

  }

}

