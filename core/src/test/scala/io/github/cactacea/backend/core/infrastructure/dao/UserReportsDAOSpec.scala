package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.UserReports

class UserReportsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should report an user") {
      forAll(userGen, userGen, userReportGen) { (a1, a2, r) =>
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        val id = await(userReportsDAO.create(userId, r.reportType, r.reportContent, sessionId))
        val result = await(db.run(query[UserReports].filter(_.id == lift(id)))).head
        assert(result.id == id)
        assert(result.by == sessionId.userId)
        assert(result.userId == userId)
        assert(result.reportType == r.reportType)
        assert(result.reportContent == r.reportContent)
      }
    }

    scenario("should not return an exception if duplicate") {
      forAll(userGen, userGen, userReportGen) { (a1, a2, r) =>
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        await(userReportsDAO.create(userId, r.reportType, r.reportContent, sessionId))
        await(userReportsDAO.create(userId, r.reportType, r.reportContent, sessionId))
        val result = await(db.run(query[UserReports]
          .filter(_.userId == lift(userId))
          .filter(_.by == lift(sessionId.userId)))).size
        assert(result == 2)
      }
    }
  }

}

