package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotJoined, AuthorityNotFound, ChannelNotFound}

class ChannelsRepositorySpec extends RepositorySpec {

  feature("create") {
    scenario("should create a channel") {
      forAll(accountGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          val result1 = await(channelsRepository.find(channelId, sessionId))
          assert(result1.id == channelId)
          assert(result1.message.isEmpty)
          assert(result1.accountCount == 1L)
          assert(result1.privacyType == g.privacyType)
          assert(result1.authorityType == g.authorityType)
          assert(result1.invitationOnly == g.invitationOnly)
          assert(result1.lastPostedAt.isEmpty)

          val result2 = await(accountChannelsRepository.find(None, 0, 10, false, sessionId))
          assert(result2.size == 1L)
          assert(result2.headOption.exists(_.id == channelId))
          assert(result2.headOption.exists(_.message.isEmpty))
          assert(result2.headOption.exists(_.accountCount == 1L))
          assert(result2.headOption.exists(_.privacyType == g.privacyType))
          assert(result2.headOption.exists(_.authorityType == g.authorityType))
          assert(result2.headOption.exists(_.invitationOnly == g.invitationOnly))
          assert(result2.headOption.exists(_.lastPostedAt.isEmpty))

      }
    }
  }

  feature("update") {
    scenario("should update a channel if organizer") {
      forOne(accountGen, channelGen, channelGen) {
        (a, g1, g2) =>
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsRepository.create(g1.name, g1.invitationOnly, g1.privacyType, g1.authorityType, sessionId))
          await(channelsRepository.update(channelId, g2.name, g2.invitationOnly, g2.privacyType, g2.authorityType, sessionId))
          val result = await(channelsRepository.find(channelId, sessionId))
          assert(result.id == channelId)
          assert(result.message.isEmpty)
          assert(result.accountCount == 1L)
          assert(result.privacyType == g2.privacyType)
          assert(result.authorityType == g2.authorityType)
          assert(result.invitationOnly == g2.invitationOnly)
          assert(result.lastPostedAt.isEmpty)
      }
    }

    scenario("should return exception if channel is direct message channel") {
      forOne(accountGen, accountGen, channelGen) {
        (s, a, g) =>
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          val channelId = await(accountChannelsRepository.findOrCreate(accountId, sessionId)).id

          // result
          assert(intercept[CactaceaException] {
            await(channelsRepository.update(channelId, g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          }.error == AuthorityNotFound)

      }
    }

    scenario("should return exception if dose not have authority") {
      forOne(accountGen, accountGen, organizerChannelGen, organizerChannelGen) {
        (s, a, g1, g2) =>

          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          val channelId = await(channelsRepository.create(g1.name, g1.invitationOnly, g1.privacyType, g2.authorityType, sessionId))
          await(accountChannelsDAO.create(accountId, channelId, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(channelsRepository.update(channelId, g2.name, g2.invitationOnly, g2.privacyType, g2.authorityType, accountId.toSessionId))
          }.error == AuthorityNotFound)

      }
    }

    scenario("should return exception if not joined") {
      forOne(accountGen, accountGen, organizerChannelGen, organizerChannelGen) {
        (s, a, g1, g2) =>

          // preparing
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId = await(accountsDAO.create(a.accountName))
          val channelId = await(channelsRepository.create(g1.name, g1.invitationOnly, g1.privacyType, g1.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(channelsRepository.update(channelId, g2.name, g2.invitationOnly, g2.privacyType, g2.authorityType, accountId.toSessionId))
          }.error == AccountNotJoined)

      }
    }

  }

  feature("find a channel") {
    scenario("should return a channel") {
      forOne(accountGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          val result1 = await(channelsRepository.find(channelId, sessionId))
          assert(result1.id == channelId)
          assert(result1.message.isEmpty)
          assert(result1.accountCount == 1L)
          assert(result1.privacyType == g.privacyType)
          assert(result1.authorityType == g.authorityType)
          assert(result1.invitationOnly == g.invitationOnly)
          assert(result1.lastPostedAt.isEmpty)

      }
    }

    scenario("should return exception if a channel not exist") {
      forOne(accountGen) {
        (a) =>

          // preparing
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = ChannelId(0L)

          // result
          assert(intercept[CactaceaException] {
            await(channelsRepository.find(channelId, sessionId))
          }.error == ChannelNotFound)

      }
    }
  }

}
