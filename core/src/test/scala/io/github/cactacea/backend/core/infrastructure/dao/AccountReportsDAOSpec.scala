package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.AccountReports

class AccountReportsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should report an account") {
      forAll(accountGen, accountGen, accountReportGen) { (a1, a2, r) =>
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a2.accountName))
        val id = await(accountReportsDAO.create(accountId, r.reportType, r.reportContent, sessionId))
        val result = await(db.run(query[AccountReports].filter(_.id == lift(id)))).head
        assert(result.id == id)
        assert(result.by == sessionId.toAccountId)
        assert(result.accountId == accountId)
        assert(result.reportType == r.reportType)
        assert(result.reportContent == r.reportContent)
      }
    }

    scenario("should not return an exception if duplicate") {
      forAll(accountGen, accountGen, accountReportGen) { (a1, a2, r) =>
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a2.accountName))
        await(accountReportsDAO.create(accountId, r.reportType, r.reportContent, sessionId))
        await(accountReportsDAO.create(accountId, r.reportType, r.reportContent, sessionId))
        val result = await(db.run(query[AccountReports]
          .filter(_.accountId == lift(accountId))
          .filter(_.by == lift(sessionId.toAccountId)))).size
        assert(result == 2)
      }
    }
  }

}

