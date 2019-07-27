package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.MessageType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, MediumId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotJoined, AuthorityNotFound}

class MessagesRepositorySpec extends RepositorySpec {

  feature("createText") {

    scenario("should create a message") {
      forOne(accountGen, accountGen, everyoneGroupGen, textMessageGen) {
        (s, a1, g, m) =>
          // preparing
          //  session account is owner
          //  accountId1 is a member
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId1 = await(accountsRepository.create(a1.accountName)).id
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(groupAccountsRepository.create(accountId1, groupId, sessionId))
          val result = await(messagesRepository.createText(groupId, m.message.getOrElse(""), sessionId))
          assert(result.messageType == MessageType.text)
          assert(result.message.getOrElse("") == m.message.getOrElse(""))
          assert(!result.warning)
          assert(!result.rejected)
          assert(result.accountCount == 2)
          assert(result.groupId == groupId)
          assert(result.medium.isEmpty)
          assert(result.readAccountCount == 0)
      }
    }

    scenario("should create a account message") {
      forOne(accountGen, accounts20ListGen, everyoneGroupGen, textMessageGen) {
        (s, l, g, m) =>
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val accountIds = l.map({ a =>
            val accountId = await(accountsRepository.create(a.accountName)).id
            await(groupAccountsRepository.create(accountId, groupId, sessionId))
            accountId
          })

          val message = await(messagesRepository.createText(groupId, m.message.getOrElse(""), sessionId))
          accountIds.zipWithIndex.foreach({ case (accountId, i) =>
            val result = await(messagesRepository.find(groupId, None, 0, 10, false, accountId.toSessionId))
            assert(result.headOption.exists(_.id == message.id))
          })

      }
    }

    scenario("should exception if not joined to a group") {
      forOne(accountGen, accountGen, everyoneGroupGen, textMessageGen) {
        (s, a1, g, m) =>
          // preparing
          //  session account is owner
          //  accountId1 is a not member
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId1 = await(accountsRepository.create(a1.accountName)).id
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // should return exception by no member account
          assert(intercept[CactaceaException] {
            await(messagesRepository.createText(groupId, m.message.getOrElse(""), accountId1.toSessionId))
          }.error == AccountNotJoined)

      }
    }
  }

  feature("createMedium") {

    scenario("should create a message") {
      forOne(accountGen, accountGen, everyoneGroupGen, mediumGen) {
        (s, a1, g, i) =>

          // preparing
          //   session create a group
          //   account1 join the group
          //   session create a message
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId1 = await(accountsRepository.create(a1.accountName)).id
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(groupAccountsRepository.create(accountId1, groupId, sessionId))
          val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val result = await(messagesRepository.createMedium(groupId, mediumId, sessionId))
          assert(result.message.isEmpty)
          assert(result.messageType == MessageType.medium)
          assert(result.accountCount == 2)
          assert(result.readAccountCount == 0)
          assert(result.medium.exists(_.id == mediumId))
      }
    }

    scenario("should create a account message") {
      forOne(accountGen, accounts20ListGen, everyoneGroupGen, messagesGen, mediumGen) {
        (s, l, g, m, i) =>
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val accountIds = l.map({ a =>
            val accountId = await(accountsRepository.create(a.accountName)).id
            await(groupAccountsRepository.create(accountId, groupId, sessionId))
            accountId
          })

          val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val message = await(messagesRepository.createMedium(groupId, mediumId, sessionId))

          accountIds.zipWithIndex.foreach({ case (accountId, i) =>
            val result = await(messagesRepository.find(groupId, None, 0, 10, false, accountId.toSessionId))
            assert(result.headOption.exists(_.id == message.id))
          })

      }
    }

    scenario("should exception if not joined to a group") {
      forOne(accountGen, accountGen, everyoneGroupGen, mediumGen) {
        (s, a1, g, i) =>
          // preparing
          //  session account is owner
          //  accountId1 is a not member
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId1 = await(accountsRepository.create(a1.accountName)).id
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // should return exception by no member account
          assert(intercept[CactaceaException] {
            val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, accountId1.toSessionId))
            await(messagesRepository.createMedium(groupId, mediumId, accountId1.toSessionId))

          }.error == AccountNotJoined)

      }
    }


    scenario("should exception if a medium not exist") {
      forOne(accountGen, accountGen, everyoneGroupGen) {
        (s, a1, g) =>
          // preparing
          //  session account is owner
          //  accountId1 is a not member
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId = await(accountsRepository.create(a1.accountName)).id
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(groupAccountsRepository.create(accountId, groupId, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(messagesRepository.createMedium(groupId, MediumId(0), accountId.toSessionId))

          }.error == AuthorityNotFound)

      }
    }

  }


  feature("delete") {
    scenario("should delete an account message") {
      forOne(accountGen, groupGen, messagesGen) {
        (s, g, m) =>
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val messageId = await(messagesRepository.createText(groupId, m.message.getOrElse(""), sessionId)).id
          await(messagesRepository.delete(groupId, sessionId))
          val result = await(accountMessagesDAO.find(messageId, sessionId))
          assert(result.isEmpty)
      }
    }
  }

  feature("find") {

    scenario("should return message list") {
      forOne(accountGen, accountGen, everyoneGroupGen, message20ListGen, booleanGen) { (s, a1, g, l, ascending) =>

        // preparing
        //  session account is owner
        //  accountId is not a member
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a1.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(accountId, groupId, sessionId))

        // create message
        val messages = l.map({ case (m, i) =>
          val mediumId = i.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
          val messageId = mediumId match {
            case Some(id) =>
              await(messagesRepository.createMedium(groupId, id, sessionId)).id
            case None =>
              await(messagesRepository.createText(groupId, m.message.getOrElse(""), sessionId)).id
          }
          m.copy(id = messageId, mediumId = mediumId)
        })

        // sorting
        val createdMessages = if (ascending) {
          messages
        } else {
          messages.reverse
        }

        //   first message is system message so offset set to 1
        val offset = if (ascending) { 1 } else { 0 }
        val lessCount = if (ascending) { 0 } else { 1 }

        // should return first page
        val result1 = await(messagesRepository.find(groupId, None, offset, 10, ascending, accountId.toSessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map({ case (m, i) =>
          assert(m.id == createdMessages(i).id)
          assert(m.medium.map(_.id) == createdMessages(i).mediumId)
        })

        // should return next page
        val size2 = result1.size
        val result2 = await(messagesRepository.find(groupId, result1.lastOption.map(_.next), 0, 10, ascending, accountId.toSessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map({ case (m, i) =>
          assert(m.id == createdMessages(i + size2).id)
          assert(m.medium.map(_.id) == createdMessages(i + size2).mediumId)
        })

        // should not return
        val result3 = await(messagesRepository.find(groupId, result2.lastOption.map(_.next), 0, 10, ascending, accountId.toSessionId))
        assert(result3.size == lessCount)



      }

    }

    scenario("should update unread count") {
      forOne(accountGen, accounts20ListGen, everyoneGroupGen, messagesGen, mediumGen) {
        (s, l, g, m, i) =>
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val accountIds = l.map({ a =>
            val accountId = await(accountsRepository.create(a.accountName)).id
            await(groupAccountsRepository.create(accountId, groupId, sessionId))
            accountId
          })

          val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val message = await(messagesRepository.createMedium(groupId, mediumId, sessionId))

          accountIds.zipWithIndex.foreach({ case (accountId, i) =>
            val result = await(messagesRepository.find(groupId, None, 0, 10, false, accountId.toSessionId))
            assert(result.headOption.exists(_.id == message.id))
            assert(result.headOption.exists(_.accountCount == message.accountCount))
            assert(result.headOption.exists(_.readAccountCount == i))
          })

      }
    }

    scenario("should update unread status") {
      forOne(accountGen, accounts20ListGen, everyoneGroupGen, messagesGen, mediumGen) {
        (s, l, g, m, i) =>
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val accountIds = l.map({ a =>
            val accountId = await(accountsRepository.create(a.accountName)).id
            await(groupAccountsRepository.create(accountId, groupId, sessionId))
            accountId
          })

          val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val message = await(messagesRepository.createMedium(groupId, mediumId, sessionId))

          accountIds.zipWithIndex.foreach({ case (accountId, i) =>
            await(messagesRepository.find(groupId, None, 0, 10, false, accountId.toSessionId))
            val result = await(messagesRepository.find(groupId, None, 0, 10, false, accountId.toSessionId))
            assert(result.headOption.exists(_.id == message.id))
            assert(result.headOption.exists(_.accountCount == message.accountCount))
            assert(result.headOption.exists(_.readAccountCount == 1 + i))
            assert(result.headOption.exists(!_.unread))
          })

      }
    }

    scenario("should return exception if a group not exist") {
      forOne(accountGen) {
        (s) =>
          // preparing
          //  session account is owner
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId

          // should return exception by no member account
          assert(intercept[CactaceaException] {
            await(messagesRepository.find(GroupId(0), None, 0, 10, false, sessionId))
          }.error == AccountNotJoined)

      }
    }

    scenario("should return exception if an account not joined to a group") {
      forOne(accountGen, accountGen, everyoneGroupGen) {
        (s, a1, g) =>
          // preparing
          //  session account is owner
          //  accountId1 is a not member
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId = await(accountsRepository.create(a1.accountName)).id
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // should return exception by no member account
          assert(intercept[CactaceaException] {
            await(messagesRepository.find(groupId, None, 0, 10, false, accountId.toSessionId))
          }.error == AccountNotJoined)

      }
    }
  }

}

