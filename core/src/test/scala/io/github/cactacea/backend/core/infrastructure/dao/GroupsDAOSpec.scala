package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class GroupsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should create one to one chat group") {
      forAll(accountGen) { a =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        val groupId = await(groupsDAO.create(sessionId))
        assert(await(existsGroup(groupId)))
      }
    }

    scenario("should create new group") {
      forAll(accountGen, groupNameGen, booleanGen, groupPrivacyTypeGen, groupAuthorityTypeGen) {
        (a, groupName, invitationOnly, privacyType, authorityType) =>
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val groupId = await(groupsDAO.create(groupName, invitationOnly, privacyType, authorityType, sessionId))
          val result = await(findGroup(groupId))
          assert(result.flatMap(_.name) == groupName)
          assert(result.map(_.invitationOnly) == Option(invitationOnly))
          assert(result.map(_.privacyType) == Option(privacyType))
          assert(result.map(_.authorityType) == Option(authorityType))
      }
    }

  }

  feature("delete") {

    scenario("should delete a group") {
      forOne(accountGen, groupNameGen, booleanGen, groupPrivacyTypeGen, groupAuthorityTypeGen) {
        (a, groupName, invitationOnly, privacyType, authorityType) =>
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val groupId1 = await(groupsDAO.create(groupName, invitationOnly, privacyType, authorityType, sessionId))
          val groupId2 = await(groupsDAO.create(groupName, invitationOnly, privacyType, authorityType, sessionId))
          val groupId3 = await(groupsDAO.create(groupName, invitationOnly, privacyType, authorityType, sessionId))
          val groupId4 = await(groupsDAO.create(groupName, invitationOnly, privacyType, authorityType, sessionId))
          await(groupsDAO.delete(groupId1))
          await(groupsDAO.delete(groupId2))
          await(groupsDAO.delete(groupId3))
          await(groupsDAO.delete(groupId4))
          assert(!await(existsGroup(groupId1)))
          assert(!await(existsGroup(groupId2)))
          assert(!await(existsGroup(groupId3)))
          assert(!await(existsGroup(groupId4)))
      }
    }

    scenario("should delete all invitations if group deleted") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a, g) =>
        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(invitationsDAO.create(accountId, groupId, sessionId))
        await(groupsDAO.delete(groupId))

        // result
        assertFutureValue(existsGroup(groupId), false)
        assertFutureValue(invitationsDAO.exists(accountId, groupId), false)
      }
    }

    scenario("should delete all messages if group deleted") {
      forOne(accountGen, accountGen, everyoneGroupGen, textMessageGen) { (s, a, g, m) =>
        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountGroupsDAO.create(groupId, sessionId))
        await(accountGroupsDAO.create(groupId, accountId.toSessionId))
        val messageId = await(messagesDAO.create(groupId, m.message.getOrElse(""), 2, sessionId))
        await(accountMessagesDAO.create(groupId, messageId, sessionId))
        await(groupsDAO.delete(groupId))

        // result
        assertFutureValue(existsMessage(messageId), false)
        assertFutureValue(existsGroup(groupId), false)
        val result1 = await(accountMessagesDAO.find(groupId, None, 0, 10, false, sessionId))
        assert(result1.size == 0)
        val result2 = await(accountMessagesDAO.find(groupId, None, 0, 10, false, accountId.toSessionId))
        assert(result2.size == 0)
      }
    }

  }

  feature("exists") {
    scenario("should return a group exist or not") {
      forOne(accountGen, accountGen, accountGen, accountGen) { (a1, a2, a3, a4) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        val groupId = await(groupsDAO.create(accountId1.toSessionId))
        await(blocksDAO.create(accountId3, accountId1.toSessionId))
        await(blocksDAO.create(accountId1, accountId2.toSessionId))
        await(blocksDAO.create(accountId2, accountId3.toSessionId))
        assertFutureValue(groupsDAO.exists(groupId, accountId1.toSessionId), true)
        assertFutureValue(groupsDAO.exists(groupId, accountId2.toSessionId), true)
        assertFutureValue(groupsDAO.exists(groupId, accountId3.toSessionId), false)
        assertFutureValue(groupsDAO.exists(groupId, accountId4.toSessionId), true)
      }
    }

  }

  feature("find a group") {
    scenario("should return a group") {
      forAll(accountGen, accountGen, accountGen, groupGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId, sessionId))
        await(accountGroupsDAO.create(accountId2, groupId, sessionId))
        val result = await(groupsDAO.find(groupId, sessionId))
        assert(result.isDefined)
        assert(result.headOption.exists(_.id == groupId))
        assert(result.headOption.exists(_.name == g.name))
        assert(result.headOption.exists(_.invitationOnly == g.invitationOnly))
        assert(result.headOption.exists(_.privacyType == g.privacyType))
        assert(result.headOption.exists(_.authorityType == g.authorityType))
        assert(result.headOption.exists(_.accountCount == 2L))
      }
    }

    scenario("should not return if account being blocked") {
      forAll(accountGen, accountGen, accountGen, groupGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId, sessionId))
        await(blocksDAO.create(accountId2, sessionId))
        val result = await(groupsDAO.find(groupId, accountId2.toSessionId))
        assert(result.isEmpty)
      }
    }

    }

  feature("findAccountCount") {
    scenario("should return group account count") {
      forAll(accountGen, accountGen, accountGen, groupGen) {
        (s, a1, a2, g) =>
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountGroupsDAO.create(groupId, sessionId))
          await(accountGroupsDAO.create(accountId1, groupId, sessionId))
          await(accountGroupsDAO.create(accountId2, groupId, sessionId))
          val result1 = await(groupsDAO.findAccountCount(groupId))
          assert(result1 == 3)
          await(accountGroupsDAO.delete(accountId1, groupId))
          await(accountGroupsDAO.delete(accountId2, groupId))
          val result2 = await(groupsDAO.findAccountCount(groupId))
          assert(result2 == 1)
      }
    }
  }


  feature("updateLatestMessage") (pending)

  feature("update") {
    scenario("should update a group") {
      forAll(accountGen, groupNameGen, booleanGen, groupPrivacyTypeGen, groupAuthorityTypeGen) {
        (a, groupName, invitationOnly, privacyType, authorityType) =>
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val groupId = await(groupsDAO.create(sessionId))
          await(groupsDAO.update(groupId, groupName, invitationOnly, privacyType, authorityType, sessionId))
          val result = await(findGroup(groupId))
          assert(result.flatMap(_.name) == groupName)
          assert(result.map(_.invitationOnly) == Option(invitationOnly))
          assert(result.map(_.privacyType) == Option(privacyType))
          assert(result.map(_.authorityType) == Option(authorityType))
      }
    }
  }




}

