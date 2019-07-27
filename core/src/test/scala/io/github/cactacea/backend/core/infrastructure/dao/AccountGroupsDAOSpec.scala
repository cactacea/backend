package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class AccountGroupsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should join an account to a group") {
      forOne(accountGen, accountGen, accountGen, groupGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId, sessionId))
        await(accountGroupsDAO.create(accountId2, groupId, sessionId))
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), true)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId2), true)
        val result = await(groupsDAO.find(groupId, sessionId))
        assert(result.headOption.map(_.accountCount) == Option(2L))
      }
    }

    scenario("should return an exception occurs if duplication") {
      forOne(accountGen, accountGen, groupGen) { (s, a1, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        await(accountGroupsDAO.create(accountId1, groupId, sessionId))
        assert(intercept[ServerError] {
          await(accountGroupsDAO.create(accountId1, groupId, sessionId))
        }.code == 1062)
      }
    }

  }

  feature("delete") {
    scenario("should leave an account from a group") {
      forAll(accountGen, accountGen, accountGen, groupGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId, sessionId))
        await(accountGroupsDAO.create(accountId2, groupId, sessionId))
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), true)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId2), true)
        val result1 = await(groupsDAO.find(groupId, sessionId))
        assert(result1.headOption.map(_.accountCount) == Option(2L))

        await(accountGroupsDAO.delete(accountId1, groupId))
        await(accountGroupsDAO.delete(accountId2, groupId))
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), false)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId2), false)
        val result2 = await(groupsDAO.find(groupId, sessionId))
        assert(result2.headOption.map(_.accountCount) == Option(0L))

      }
    }
  }

  feature("exists") {
    scenario("should return joined or not") {
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
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), true)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId2), false)
      }
    }
  }

  feature("updateUnreadCount") {
    scenario("should increase unread message count") {
      forOne(accountGen, accountGen, groupGen) { (s, a1, g) =>
        // preparing
        //  session account creates a group
        //  account1 joins to the group
        //  session account hides the group
        //  session account update unread count twice
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId, sessionId))
        await(accountGroupsDAO.updateHidden(groupId, true, sessionId))
        await(accountGroupsDAO.updateUnreadCount(groupId))
        await(accountGroupsDAO.updateUnreadCount(groupId))

        // return the group not hidden
        // return the group unread count is 2
        val result = findAccountGroup(groupId, accountId1)
        assert(result.exists(!_.hidden))
        assert(result.map(_.unreadCount) == Option(2L))
      }
    }
  }

  feature("updateHidden") {
    scenario("should show and hide group") {
      forOne(accountGen, accountGen, accountGen, groupGen, groupGen) { (s, a1, a2, g1, g2) =>
        // preparing
        //   session account creates two group2
        //   account1 joins to the group 1
        //   account2 joins to the group 2
        //   account1 hides the group 1
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val groupId1 = await(groupsDAO.create(g1.name, g1.invitationOnly, g1.privacyType, g1.authorityType, sessionId))
        val groupId2 = await(groupsDAO.create(g2.name, g2.invitationOnly, g2.privacyType, g2.authorityType, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId1, sessionId))
        await(accountGroupsDAO.create(accountId2, groupId2, sessionId))
        await(accountGroupsDAO.updateHidden(groupId1, true, accountId1.toSessionId))

        // return group1 is hidden
        // return group2 is not hidden
        assertFutureValue(accountGroupsDAO.isHidden(groupId1, accountId1.toSessionId), Option(true))
        assertFutureValue(accountGroupsDAO.isHidden(groupId2, accountId2.toSessionId), Option(false))

        // preparing
        //   account1 shows the group1
        //   account2 hides the group2
        await(accountGroupsDAO.updateHidden(groupId1, false, accountId1.toSessionId))
        await(accountGroupsDAO.updateHidden(groupId2, true, accountId2.toSessionId))

        // return group1 is hidden
        // return group2 is not hidden
        assertFutureValue(accountGroupsDAO.isHidden(groupId1, accountId1.toSessionId), Option(false))
        assertFutureValue(accountGroupsDAO.isHidden(groupId2, accountId2.toSessionId), Option(true))
      }
    }
  }

  feature("findByAccountId") {
    scenario("should return a group") {
      forOne(accountGen, accountGen) { (s, a1 ) =>

        // preparing
        //   session create a group
        //   account1 join the group
        //   session create a message
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val groupId = await(groupsDAO.create(sessionId))
        await(accountGroupsDAO.create(groupId, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId, sessionId))

        // result
        val result = await(accountGroupsDAO.findByAccountId(accountId1, sessionId))
        assert(result.isDefined)
        assert(result.exists(_.invitationOnly))
        assert(result.exists(_.accountCount == 2))
        assert(result.exists(_.authorityType == GroupAuthorityType.member))
        assert(result.exists(_.id == groupId))
        assert(result.exists(_.message.isEmpty))
        assert(result.exists(_.privacyType == GroupPrivacyType.everyone))
      }

    }
    scenario("should return a group with latest message") {
      forOne(accountGen, accountGen, textMessageGen) { (s, a1, m) =>

        // preparing
        //   session create a group
        //   account1 join the group
        //   session create a message
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val groupId = await(groupsDAO.create(sessionId))
        await(accountGroupsDAO.create(groupId, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId, sessionId))
        val messageId = await(messagesDAO.create(groupId, m.message.getOrElse(""), 2, sessionId))
        await(accountMessagesDAO.create(groupId, messageId, sessionId))

        // result
        val result = await(accountGroupsDAO.findByAccountId(accountId1, sessionId))
        assert(result.isDefined)
        assert(result.exists(_.invitationOnly))
        assert(result.exists(_.accountCount == 2))
        assert(result.exists(_.authorityType == GroupAuthorityType.member))
        assert(result.exists(_.id == groupId))
        assert(result.exists(_.message.exists(_.message == m.message)))
        assert(result.exists(_.message.exists(_.messageType == m.messageType)))
        assert(result.exists(_.message.exists(_.accountCount == 2)))
        assert(result.exists(_.message.exists(_.account.id == sessionId.toAccountId)))
        assert(result.exists(_.message.exists(_.readAccountCount == 0)))
        assert(result.exists(_.message.exists(!_.rejected)))
        assert(result.exists(_.name.isEmpty))
        assert(result.exists(_.privacyType == GroupPrivacyType.everyone))
      }
    }

    scenario("should return a group with latest message and medium") {
      forOne(accountGen, accountGen, mediumGen) {
        (s, a1, i) =>

          // preparing
          //   session create a group
          //   account1 join the group
          //   session create a message
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val groupId = await(groupsDAO.create(sessionId))
          await(accountGroupsDAO.create(groupId, sessionId))
          await(accountGroupsDAO.create(accountId1, groupId, sessionId))
          val mediumId = await(mediumsDAO.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val accountCount = await(groupsDAO.findAccountCount(groupId))
          val messageId = await(messagesDAO.create(groupId, mediumId, accountCount, sessionId))
          await(accountMessagesDAO.create(groupId, messageId, sessionId))

          // result
          val result = await(accountGroupsDAO.findByAccountId(accountId1, sessionId))
          assert(result.isDefined)
          assert(result.exists(_.invitationOnly))
          assert(result.exists(_.accountCount == 2))
          assert(result.exists(_.authorityType == GroupAuthorityType.member))
          assert(result.exists(_.id == groupId))
          assert(result.exists(_.message.exists(_.message.isEmpty)))
          assert(result.exists(_.message.exists(_.messageType == MessageType.medium)))
          assert(result.exists(_.message.exists(_.accountCount == 2)))
          assert(result.exists(_.message.exists(_.account.id == sessionId.toAccountId)))
          assert(result.exists(_.message.exists(_.readAccountCount == 0)))
          assert(result.exists(_.message.exists(!_.rejected)))
          assert(result.exists(_.message.exists(_.medium.exists(_.id == mediumId))))
          assert(result.exists(_.message.exists(_.medium.exists(_.uri == i.uri))))
          assert(result.exists(_.message.exists(_.medium.exists(_.thumbnailUrl == i.thumbnailUrl))))
          assert(result.exists(_.message.exists(_.medium.exists(_.mediumType == i.mediumType))))
          assert(result.exists(_.message.exists(_.medium.exists(_.width == i.width))))
          assert(result.exists(_.message.exists(_.medium.exists(_.height == i.height))))
          assert(result.exists(_.message.exists(_.medium.exists(_.size == i.size))))
          assert(result.exists(_.name.isEmpty))
          assert(result.exists(_.privacyType == GroupPrivacyType.everyone))

      }
    }

  }



  feature("find") {
    scenario("should return groups") {
      forOne(accountGen, accountGen, messagesGen, group20ListGen) { (s, a1, m, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val groups = g.map({g =>
          val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountGroupsDAO.create(accountId1, groupId, sessionId))
          val messageId = await(messagesDAO.create(groupId, m.message.getOrElse(""), 2, sessionId))
          await(accountMessagesDAO.create(groupId, messageId, sessionId))
          g.copy(id = groupId)
        }).reverse

        // page1 found
        val result1 = await(accountGroupsDAO.find(accountId1, None, 0, 10, false, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == groups(i).id)
          assert(r.message.exists(_.groupId == groups(i).id))
          assert(r.message.exists(_.message.getOrElse("") == m.message.getOrElse("")))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(accountGroupsDAO.find(accountId1, result1.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == groups(i + size1).id)
          assert(r.message.exists(_.groupId == groups(i + size1).id))
          assert(r.message.exists(_.message.getOrElse("") == m.message.getOrElse("")))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page3 not found
        val result3 = await(accountGroupsDAO.find(accountId1, result2.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result3.size == 0)

      }

    }

    scenario("should return latest message and medium") {

      forOne(accountGen, accountGen, mediumGen, group20ListGen) { (s, a1, i, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val groups = g.map({g =>
          val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountGroupsDAO.create(accountId1, groupId, sessionId))
          val mediumId = await(mediumsDAO.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val messageId = await(messagesDAO.create(groupId, mediumId, 2, sessionId))
          await(accountMessagesDAO.create(groupId, messageId, sessionId))
          g.copy(id = groupId)
        }).reverse

        // page1 found
        val result1 = await(accountGroupsDAO.find(accountId1, None, 0, 10, false, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, index) =>
          assert(r.id == groups(index).id)
          assert(r.message.exists(_.groupId == groups(index).id))
          assert(r.message.exists(_.message.isEmpty))
          assert(r.message.exists(_.messageType == MessageType.medium))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
          assert(r.message.exists(_.medium.exists(_.uri == i.uri)))
          assert(r.message.exists(_.medium.exists(_.thumbnailUrl == i.thumbnailUrl)))
          assert(r.message.exists(_.medium.exists(_.mediumType == i.mediumType)))
          assert(r.message.exists(_.medium.exists(_.width == i.width)))
          assert(r.message.exists(_.medium.exists(_.height == i.height)))
          assert(r.message.exists(_.medium.exists(_.size == i.size)))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(accountGroupsDAO.find(accountId1, result1.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, index) =>
          assert(r.id == groups(index + size1).id)
          assert(r.message.exists(_.groupId == groups(index + size1).id))
          assert(r.message.exists(_.message.isEmpty))
          assert(r.message.exists(_.messageType == MessageType.medium))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
          assert(r.message.exists(_.medium.exists(_.uri == i.uri)))
          assert(r.message.exists(_.medium.exists(_.thumbnailUrl == i.thumbnailUrl)))
          assert(r.message.exists(_.medium.exists(_.mediumType == i.mediumType)))
          assert(r.message.exists(_.medium.exists(_.width == i.width)))
          assert(r.message.exists(_.medium.exists(_.height == i.height)))
          assert(r.message.exists(_.medium.exists(_.size == i.size)))
        }

        // page3 not found
        val result3 = await(accountGroupsDAO.find(accountId1, result2.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result3.size == 0)

      }

    }

    scenario("should filter hidden or not") {

      forOne(accountGen, accountGen, messagesGen, group20ListGen) { (s, a1, m, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val groups = g.map({g =>
          val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountGroupsDAO.create(accountId1, groupId, sessionId))
          val messageId = await(messagesDAO.create(groupId, m.message.getOrElse(""), 2, sessionId))
          await(accountMessagesDAO.create(groupId, messageId, sessionId))
          g.copy(id = groupId)
        }).reverse

        groups.foreach({ g =>
          await(accountGroupsDAO.updateHidden(g.id, false, accountId1.toSessionId))
        })

        val result1 = await(accountGroupsDAO.find(accountId1, None, 0, 10, true, sessionId))
        assert(result1.size == 0)

        groups.foreach({ g =>
          await(accountGroupsDAO.updateHidden(g.id, true, accountId1.toSessionId))
        })

        val result2 = await(accountGroupsDAO.find(accountId1, None, 0, 10, false, sessionId))
        assert(result2.size == 0)

      }

    }
  }

  feature("isHidden") {
    scenario("should return hidden or not") {
      forAll(accountGen, accountGen, groupGen, groupGen, groupGen) { (s, a1, g1, g2, g3) =>
        // preparing
        //   session account creates two group2
        //   account1 joins to the group 1
        //   account1 joins to the group 2
        //   account1 dose not join to the group 3
        //   account1 hides the group 1
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val groupId1 = await(groupsDAO.create(g1.name, g1.invitationOnly, g1.privacyType, g1.authorityType, sessionId))
        val groupId2 = await(groupsDAO.create(g2.name, g2.invitationOnly, g2.privacyType, g2.authorityType, sessionId))
        val groupId3 = await(groupsDAO.create(g3.name, g3.invitationOnly, g3.privacyType, g3.authorityType, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId1, sessionId))
        await(accountGroupsDAO.create(accountId1, groupId2, sessionId))
        await(accountGroupsDAO.updateHidden(groupId1, true, accountId1.toSessionId))

        // return group1 is hidden
        // return group2 is not hidden
        // return group3 is Unknown
        assertFutureValue(accountGroupsDAO.isHidden(groupId1, accountId1.toSessionId), Option(true))
        assertFutureValue(accountGroupsDAO.isHidden(groupId2, accountId1.toSessionId), Option(false))
        assertFutureValue(accountGroupsDAO.isHidden(groupId3, accountId1.toSessionId), None)
      }
    }
  }



}

