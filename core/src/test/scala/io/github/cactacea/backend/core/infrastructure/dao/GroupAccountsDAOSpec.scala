package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class GroupAccountsDAOSpec extends DAOSpec {

  feature("find") {
    scenario("should return groups") {
      forOne(accountGen, accounts20ListGen, groupGen) { (s, l, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val accountIds = l.map({ a =>
          val accountId = await(accountsDAO.create(a.accountName))
          await(accountGroupsDAO.create(accountId, groupId, sessionId))
          accountId
        }).reverse

        // page1 found
        val result1 = await(groupAccountsDAO.find(groupId, None, 0, 10))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (a, i) =>
            assert(a.id == accountIds(i))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(groupAccountsDAO.find(groupId, result1.lastOption.map(_.next), 0, 10))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (a, i) =>
          assert(a.id == accountIds(i + size1))
        }

        // page3 not found
        val result3 = await(groupAccountsDAO.find(groupId, result2.lastOption.map(_.next), 0, 10))
        assert(result3.size == 0)


      }
    }

  }

  feature("exists") {
    scenario("should return join or not") {
      forOne(accountGen, accountGen, accountGen, groupGen) { (s, a1, a2, g) =>
        // preparing
        //  session account creates a group
        //  account1 joins to the group
        //  account2 dose not join to the group
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId, sessionId))

        // return account1 joined
        // return account2 not joined
        assertFutureValue(groupAccountsDAO.exists(groupId, accountId1), true)
        assertFutureValue(groupAccountsDAO.exists(groupId, accountId2), false)
      }
    }
  }

}

