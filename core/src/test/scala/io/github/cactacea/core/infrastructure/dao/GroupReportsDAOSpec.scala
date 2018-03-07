package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.GroupReports

class GroupReportsDAOSpec extends DAOSpec {

  import db._

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val groupReportsDAO: GroupReportsDAO = injector.instance[GroupReportsDAO]

  test("create") {

    val sessionAccount = createAccount("account0")
    val reportedUser = createAccount("account1")

    val groupId = Await.result(groupsDAO.create(sessionAccount.id.toSessionId))
    val groupReportId = Await.result(groupReportsDAO.create(groupId, ReportType.spam, reportedUser.id.toSessionId))

    val result = Await.result(db.run(query[GroupReports].filter(_.id == lift(groupReportId)))).head
    assert(result.id == groupReportId)
    assert(result.by == reportedUser.id)
    assert(result.groupId == groupId)
    assert(result.reportType == ReportType.spam)

  }

  test("delete") {

    val sessionAccount = createAccount("account0")
    val reportedUser = createAccount("account1")

    val groupId = Await.result(groupsDAO.create(sessionAccount.id.toSessionId))
    val groupReportId = Await.result(groupReportsDAO.create(groupId, ReportType.spam, reportedUser.id.toSessionId))

    val result = Await.result(groupReportsDAO.delete(groupId))
    assert(result == true)

  }

}

