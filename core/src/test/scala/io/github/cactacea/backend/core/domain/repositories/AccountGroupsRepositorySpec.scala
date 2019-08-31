package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.{GroupPrivacyType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, AccountNotJoined, GroupAlreadyHidden, GroupNotHidden, InvalidAccountIdError}

class AccountGroupsRepositorySpec extends RepositorySpec {

  feature("findOrCreate") {
    scenario("should return a group") {
      forOne(accountGen, accountGen) {
        (s, a) =>

          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))

          // result

          // create a group
          val result = await(accountGroupsRepository.findOrCreate(accountId, sessionId))
          assert(result.message.isEmpty)
          assert(result.accountCount == 2L)

          // find a group
          val result2 = await(accountGroupsRepository.findOrCreate(accountId, sessionId))
          assert(result2.message.isEmpty)
          assert(result2.accountCount == 2L)
      }
    }

    scenario("should return exception if session id and account id is same.") {
      forOne(accountGen) {
        (s) =>

          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId

          // result
          assert(intercept[CactaceaException] {
            await(accountGroupsRepository.findOrCreate(sessionId.toAccountId, sessionId))
          }.error == InvalidAccountIdError)
      }
    }

    scenario("should return exception if account is blocked.") {
      forOne(accountGen, accountGen) {
        (s, a) =>

          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          await(blocksRepository.create(sessionId.toAccountId, accountId.toSessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountGroupsRepository.findOrCreate(accountId, sessionId))
          }.error == AccountNotFound)

      }
    }
  }

  feature("delete") {
    scenario("should hide a group") {
      forOne(accountGen, groupGen, messageTextGen) {
        (a, g, m) =>
          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val message = await(messagesRepository.createText(groupId, m, sessionId))

          // result
          val result1 = await(accountGroupsRepository.find(None, 0, 1, false, sessionId)).headOption
          assert(result1.exists(_.id == groupId))
          assert(result1.exists(_.message.exists(_.id == message.id)))

          await(accountGroupsRepository.delete(groupId, sessionId))
          val result2 = await(accountGroupsRepository.find(None, 0, 1, true, sessionId)).headOption
          assert(result2.exists(_.id == groupId))
          assert(result2.exists(_.message.isEmpty))
      }
    }

    scenario("should return exception if do not join.") {
      forOne(accountGen, accountGen, groupGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountGroupsRepository.delete(groupId, accountId.toSessionId))
          }.error == AccountNotJoined)

      }
    }
  }

  feature("find") {
    scenario("should return an account`s groups") {
      forOne(accountGen, accountGen, messageTextGen, group20ListGen) { (s, a1, m, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val groups = g.map({g =>
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, GroupPrivacyType.everyone, g.authorityType, sessionId))
          await(groupAccountsRepository.create(accountId1, groupId, sessionId))
          await(messagesRepository.createText(groupId, m, sessionId))
          g.copy(id = groupId)
        }).reverse

        // page1 found
        val result1 = await(accountGroupsRepository.find(accountId1, None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == groups(i).id)
          assert(r.message.exists(_.groupId == groups(i).id))
          assert(r.message.exists(_.message.getOrElse("") == m))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(accountGroupsRepository.find(accountId1, result1.lastOption.flatMap(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == groups(i + size1).id)
          assert(r.message.exists(_.groupId == groups(i + size1).id))
          assert(r.message.exists(_.message.exists(_ == m)))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page3 not found
        val result3 = await(accountGroupsRepository.find(accountId1, result2.lastOption.flatMap(_.next), 0, 10, sessionId))
        assert(result3.size == 0)

      }
    }

    scenario("should return error if session id and account id is same.") {
      forOne(accountGen, groupGen) {
        (s, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountGroupsRepository.find(sessionId.toAccountId, None, 0, 10, sessionId))
          }.error == InvalidAccountIdError)

      }
    }

    scenario("should return error when account is blocked.") {
      forOne(accountGen, accountGen, groupGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          await(blocksRepository.create(sessionId.toAccountId, accountId.toSessionId))
          await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountGroupsRepository.find(accountId, None, 0, 10, sessionId))
          }.error == AccountNotFound)

      }
    }
  }

  feature("find session's groups") {
    scenario("should return groups") {
      forOne(accountGen, messageTextGen, group20ListGen) { (s, m, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val groups = g.map({g =>
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, GroupPrivacyType.everyone, g.authorityType, sessionId))
          await(messagesRepository.createText(groupId, m, sessionId))
          g.copy(id = groupId)
        }).reverse

        // page1 found
        val result1 = await(accountGroupsRepository.find(None, 0, 10, false, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == groups(i).id)
          assert(r.message.exists(_.groupId == groups(i).id))
          assert(r.message.exists(_.message.getOrElse("") == m))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 1))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(accountGroupsRepository.find(result1.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == groups(i + size1).id)
          assert(r.message.exists(_.groupId == groups(i + size1).id))
          assert(r.message.exists(_.message.exists(_ == m)))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 1))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page3 not found
        val result3 = await(accountGroupsRepository.find(result2.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result3.size == 0)

      }
    }

  }

  feature("show") {
    scenario("should show a group") {
      forOne(accountGen, groupGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountGroupsRepository.delete(groupId, sessionId))
          await(accountGroupsRepository.show(groupId, sessionId))

          // result
          val result = await(accountGroupsRepository.find(None, 0, 1, false, sessionId)).headOption
          assert(result.exists(_.id == groupId))
      }
    }

    scenario("should return exception if do not join.") {
      forOne(accountGen, accountGen, groupGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountGroupsRepository.show(groupId, accountId.toSessionId))
          }.error == AccountNotJoined)

      }
    }

    scenario("should return exception if a group is already shown") {
      forOne(accountGen, groupGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountGroupsRepository.show(groupId, sessionId))
          }.error == GroupNotHidden)

      }
    }
  }

  feature("hide") {
    scenario("should hide a group") {
      forOne(accountGen, groupGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountGroupsRepository.hide(groupId, sessionId))

          // result
          val result = await(accountGroupsRepository.find(None, 0, 1, true, sessionId)).headOption
          assert(result.exists(_.id == groupId))
      }
    }

    scenario("should return exception if do not join.") {
      forOne(accountGen, accountGen, groupGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountGroupsRepository.hide(groupId, accountId.toSessionId))
          }.error == AccountNotJoined)

      }
    }

    scenario("should return exception if a group is already hidden") {
      forOne(accountGen, groupGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountGroupsRepository.hide(groupId, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountGroupsRepository.hide(groupId, sessionId))
          }.error == GroupAlreadyHidden)

      }

    }
  }
  


}
