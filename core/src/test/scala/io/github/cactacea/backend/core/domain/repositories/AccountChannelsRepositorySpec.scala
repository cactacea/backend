package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.{ChannelPrivacyType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, AccountNotJoined, ChannelAlreadyHidden, ChannelNotHidden, InvalidAccountIdError}

class AccountChannelsRepositorySpec extends RepositorySpec {

  feature("findOrCreate") {
    scenario("should return a channel") {
      forOne(accountGen, accountGen) {
        (s, a) =>

          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))

          // result

          // create a channel
          val result = await(accountChannelsRepository.findOrCreate(accountId, sessionId))
          assert(result.message.isEmpty)
          assert(result.accountCount == 2L)

          // find a channel
          val result2 = await(accountChannelsRepository.findOrCreate(accountId, sessionId))
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
            await(accountChannelsRepository.findOrCreate(sessionId.toAccountId, sessionId))
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
            await(accountChannelsRepository.findOrCreate(accountId, sessionId))
          }.error == AccountNotFound)

      }
    }
  }

  feature("delete") {
    scenario("should hide a channel") {
      forOne(accountGen, channelGen, messageTextGen) {
        (a, g, m) =>
          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val message = await(messagesRepository.createText(channelId, m, sessionId))

          // result
          val result1 = await(accountChannelsRepository.find(None, 0, 1, false, sessionId)).headOption
          assert(result1.exists(_.id == channelId))
          assert(result1.exists(_.message.exists(_.id == message.id)))

          await(accountChannelsRepository.delete(channelId, sessionId))
          val result2 = await(accountChannelsRepository.find(None, 0, 1, true, sessionId)).headOption
          assert(result2.exists(_.id == channelId))
          assert(result2.exists(_.message.isEmpty))
      }
    }

    scenario("should return exception if do not join.") {
      forOne(accountGen, accountGen, channelGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountChannelsRepository.delete(channelId, accountId.toSessionId))
          }.error == AccountNotJoined)

      }
    }
  }

  feature("find") {
    scenario("should return an account`s channels") {
      forOne(accountGen, accountGen, messageTextGen, channel20ListGen) { (s, a1, m, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channels = g.map({g =>
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, ChannelPrivacyType.everyone, g.authorityType, sessionId))
          await(channelAccountsRepository.create(accountId1, channelId, sessionId))
          await(messagesRepository.createText(channelId, m, sessionId))
          g.copy(id = channelId)
        }).reverse

        // page1 found
        val result1 = await(accountChannelsRepository.find(accountId1, None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i).id)
          assert(r.message.exists(_.channelId == channels(i).id))
          assert(r.message.exists(_.message.getOrElse("") == m))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(accountChannelsRepository.find(accountId1, result1.lastOption.flatMap(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i + size1).id)
          assert(r.message.exists(_.channelId == channels(i + size1).id))
          assert(r.message.exists(_.message.exists(_ == m)))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page3 not found
        val result3 = await(accountChannelsRepository.find(accountId1, result2.lastOption.flatMap(_.next), 0, 10, sessionId))
        assert(result3.size == 0)

      }
    }

    scenario("should return error if session id and account id is same.") {
      forOne(accountGen, channelGen) {
        (s, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountChannelsRepository.find(sessionId.toAccountId, None, 0, 10, sessionId))
          }.error == InvalidAccountIdError)

      }
    }

    scenario("should return error when account is blocked.") {
      forOne(accountGen, accountGen, channelGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          await(blocksRepository.create(sessionId.toAccountId, accountId.toSessionId))
          await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountChannelsRepository.find(accountId, None, 0, 10, sessionId))
          }.error == AccountNotFound)

      }
    }
  }

  feature("find session's channels") {
    scenario("should return channels") {
      forOne(accountGen, messageTextGen, channel20ListGen) { (s, m, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val channels = g.map({g =>
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, ChannelPrivacyType.everyone, g.authorityType, sessionId))
          await(messagesRepository.createText(channelId, m, sessionId))
          g.copy(id = channelId)
        }).reverse

        // page1 found
        val result1 = await(accountChannelsRepository.find(None, 0, 10, false, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i).id)
          assert(r.message.exists(_.channelId == channels(i).id))
          assert(r.message.exists(_.message.getOrElse("") == m))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 1))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(accountChannelsRepository.find(result1.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i + size1).id)
          assert(r.message.exists(_.channelId == channels(i + size1).id))
          assert(r.message.exists(_.message.exists(_ == m)))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 1))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page3 not found
        val result3 = await(accountChannelsRepository.find(result2.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result3.size == 0)

      }
    }

  }

  feature("show") {
    scenario("should show a channel") {
      forOne(accountGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsRepository.delete(channelId, sessionId))
          await(accountChannelsRepository.show(channelId, sessionId))

          // result
          val result = await(accountChannelsRepository.find(None, 0, 1, false, sessionId)).headOption
          assert(result.exists(_.id == channelId))
      }
    }

    scenario("should return exception if do not join.") {
      forOne(accountGen, accountGen, channelGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountChannelsRepository.show(channelId, accountId.toSessionId))
          }.error == AccountNotJoined)

      }
    }

    scenario("should return exception if a channel is already shown") {
      forOne(accountGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountChannelsRepository.show(channelId, sessionId))
          }.error == ChannelNotHidden)

      }
    }
  }

  feature("hide") {
    scenario("should hide a channel") {
      forOne(accountGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsRepository.hide(channelId, sessionId))

          // result
          val result = await(accountChannelsRepository.find(None, 0, 1, true, sessionId)).headOption
          assert(result.exists(_.id == channelId))
      }
    }

    scenario("should return exception if do not join.") {
      forOne(accountGen, accountGen, channelGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountChannelsRepository.hide(channelId, accountId.toSessionId))
          }.error == AccountNotJoined)

      }
    }

    scenario("should return exception if a channel is already hidden") {
      forOne(accountGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsRepository.hide(channelId, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(accountChannelsRepository.hide(channelId, sessionId))
          }.error == ChannelAlreadyHidden)

      }

    }
  }
  


}
