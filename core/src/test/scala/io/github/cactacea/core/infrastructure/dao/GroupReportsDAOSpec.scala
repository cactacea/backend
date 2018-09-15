package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.GroupReports

class GroupReportsDAOSpec extends DAOSpec {

  import db._

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val groupReportsDAO: GroupReportsDAO = injector.instance[GroupReportsDAO]

  test("create") {

    val sessionAccount = createAccount("GroupReportsDAOSpec1")
    val reportedUser = createAccount("GroupReportsDAOSpec2")

    val groupId = Await.result(groupsDAO.create(sessionAccount.id.toSessionId))
    val groupReportId = Await.result(groupReportsDAO.create(groupId, ReportType.spam, reportedUser.id.toSessionId))

    val result = Await.result(db.run(query[GroupReports].filter(_.id == lift(groupReportId)))).head
    assert(result.id == groupReportId)
    assert(result.by == reportedUser.id)
    assert(result.groupId == groupId)
    assert(result.reportType == ReportType.spam)

  }

  test("delete") {

    val sessionAccount = createAccount("GroupReportsDAOSpec3")
    val reportedUser = createAccount("GroupReportsDAOSpec4")

    val groupId = Await.result(groupsDAO.create(sessionAccount.id.toSessionId))
    val groupReportId = Await.result(groupReportsDAO.create(groupId, ReportType.spam, reportedUser.id.toSessionId))

    val result = Await.result(groupReportsDAO.delete(groupId))
    assert(result == true)

  }

}

