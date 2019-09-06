package jp.smartreach.members.backend.core.infrastructure.daos

import jp.smartreach.members.backend.core.infrastructure.specs.DAOSpec

class MembersDAOSpec extends DAOSpec {

  feature("create") {
    scenario("") {
      forOne(memberGen) { (m) =>
        val id = await(membersDAO.create(m.communicationType, None, m.email, m.phoneNo))
        assertFutureValue(membersDAO.exists(id), true)
      }
    }
  }
}
