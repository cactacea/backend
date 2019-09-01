package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.ChannelReports


class ChannelReportsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create a report") {
      forOne(userGen, userGen, feedReportGen, feedReportGen) { (a1, a2, r1, r2) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val channelId = await(channelsDAO.create(userId1.sessionId))
        await(channelReportsDAO.create(channelId, r1.reportType, r1.reportContent, userId1.sessionId))
        await(channelReportsDAO.create(channelId, r2.reportType, r2.reportContent, userId2.sessionId))
        val result = await(db.run(query[ChannelReports].filter(_.channelId == lift(channelId)).sortBy(_.id)))
        assert(result.size == 2)
        assert(result(0).channelId == channelId)
        assert(result(0).by == userId1)
        assert(result(0).reportType == r1.reportType)
        assert(result(0).reportContent == r1.reportContent)
        assert(result(1).reportContent == r2.reportContent)
        assert(result(1).reportType == r2.reportType)
        assert(result(1).by == userId2)
        assert(result(1).channelId == channelId)
      }
    }
  }

}
