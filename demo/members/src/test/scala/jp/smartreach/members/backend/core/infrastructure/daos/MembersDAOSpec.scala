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

  feature("update") {
    scenario("") {
      forOne(memberGen, memberGen) { (m1, m2) =>
        val id = await(membersDAO.create(m1.communicationType, None, m1.email, m1.phoneNo))
        await(membersDAO.update(id, m2.communicationType, None, m2.email, m2.phoneNo))
        assertFutureValue(membersDAO.exists(id), true)
      }
    }
  }

}
