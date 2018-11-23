package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.GroupReports

class GroupReportsDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("GroupReportsDAOSpec1")
    val reportedUser = createAccount("GroupReportsDAOSpec2")
    val reportContent = Some("report content")

    val groupId = execute(groupsDAO.create(sessionAccount.id.toSessionId))
    val groupReportId = execute(groupReportsDAO.create(groupId, ReportType.spam, reportContent, reportedUser.id.toSessionId))

    val result = execute(db.run(query[GroupReports].filter(_.id == lift(groupReportId)))).head
    assert(result.id == groupReportId)
    assert(result.by == reportedUser.id)
    assert(result.groupId == groupId)
    assert(result.reportType == ReportType.spam)
    assert(result.reportContent == reportContent)
  }

  test("delete") {

    val sessionAccount = createAccount("GroupReportsDAOSpec3")
    val reportedUser = createAccount("GroupReportsDAOSpec4")
    val reportContent = Some("report content")

    val groupId = execute(groupsDAO.create(sessionAccount.id.toSessionId))
    execute(groupReportsDAO.create(groupId, ReportType.spam, reportContent, reportedUser.id.toSessionId))
    execute(groupReportsDAO.delete(groupId))

  }

}

