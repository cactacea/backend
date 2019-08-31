package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.ChannelReports


class ChannelReportsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create a report") {
      forOne(accountGen, accountGen, feedReportGen, feedReportGen) { (a1, a2, r1, r2) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val channelId = await(channelsDAO.create(accountId1.toSessionId))
        await(channelReportsDAO.create(channelId, r1.reportType, r1.reportContent, accountId1.toSessionId))
        await(channelReportsDAO.create(channelId, r2.reportType, r2.reportContent, accountId2.toSessionId))
        val result = await(db.run(query[ChannelReports].filter(_.channelId == lift(channelId)).sortBy(_.id)))
        assert(result.size == 2)
        assert(result(0).channelId == channelId)
        assert(result(0).by == accountId1)
        assert(result(0).reportType == r1.reportType)
        assert(result(0).reportContent == r1.reportContent)
        assert(result(1).reportContent == r2.reportContent)
        assert(result(1).reportType == r2.reportType)
        assert(result(1).by == accountId2)
        assert(result(1).channelId == channelId)
      }
    }
  }

}
