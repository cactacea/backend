package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.AccountReports

class AccountReportsDAOSpec extends DAOSpec {

  import db._

  val userReportsDAO: AccountReportsDAO = injector.instance[AccountReportsDAO]

  test("create") {

    val sessionAccount = createAccount("AccountReportsDAOSpec1")
    val reportedUser = createAccount("AccountReportsDAOSpec2")

    val userReportId = Await.result(userReportsDAO.create(reportedUser.id, ReportType.spam, sessionAccount.id.toSessionId))

    val result = Await.result(db.run(query[AccountReports].filter(_.id == lift(userReportId)))).head
    assert(result.id == userReportId)
    assert(result.by == sessionAccount.id)
    assert(result.accountId == reportedUser.id)
    assert(result.reportType == ReportType.spam)
  }

}

