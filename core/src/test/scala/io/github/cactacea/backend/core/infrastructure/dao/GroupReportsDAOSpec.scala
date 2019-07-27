package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.GroupReports


class GroupReportsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create a report") {
      forOne(accountGen, accountGen, feedReportGen, feedReportGen) { (a1, a2, r1, r2) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val groupId = await(groupsDAO.create(accountId1.toSessionId))
        await(groupReportsDAO.create(groupId, r1.reportType, r1.reportContent, accountId1.toSessionId))
        await(groupReportsDAO.create(groupId, r2.reportType, r2.reportContent, accountId2.toSessionId))
        val result = await(db.run(query[GroupReports].filter(_.groupId == lift(groupId)).sortBy(_.id)))
        assert(result.size == 2)
        assert(result(0).groupId == groupId)
        assert(result(0).by == accountId1)
        assert(result(0).reportType == r1.reportType)
        assert(result(0).reportContent == r1.reportContent)
        assert(result(1).reportContent == r2.reportContent)
        assert(result(1).reportType == r2.reportType)
        assert(result(1).by == accountId2)
        assert(result(1).groupId == groupId)
      }
    }
  }

}
