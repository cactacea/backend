package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.{ChannelPrivacyType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserNotFound, UserNotJoined, ChannelAlreadyHidden, ChannelNotHidden, InvalidUserIdError}

class UserChannelsRepositorySpec extends RepositorySpec {

  feature("findOrCreate") {
    scenario("should return a channel") {
      forOne(userGen, userGen) {
        (s, a) =>

          // preparing
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId = await(createUser(a.userName)).id

          // result

          // create a channel
          val result = await(userChannelsRepository.findOrCreate(userId, sessionId))
          assert(result.message.isEmpty)
          assert(result.userCount == 2L)

          // find a channel
          val result2 = await(userChannelsRepository.findOrCreate(userId, sessionId))
          assert(result2.message.isEmpty)
          assert(result2.userCount == 2L)
      }
    }

    scenario("should return exception if session id and user id is same.") {
      forOne(userGen) {
        (s) =>

          // preparing
          val sessionId = await(createUser(s.userName)).id.sessionId

          // result
          assert(intercept[CactaceaException] {
            await(userChannelsRepository.findOrCreate(sessionId.userId, sessionId))
          }.error == InvalidUserIdError)
      }
    }

    scenario("should return exception if user is blocked.") {
      forOne(userGen, userGen) {
        (s, a) =>

          // preparing
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId = await(createUser(a.userName)).id
          await(blocksRepository.create(sessionId.userId, userId.sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(userChannelsRepository.findOrCreate(userId, sessionId))
          }.error == UserNotFound)

      }
    }
  }

  feature("delete") {
    scenario("should hide a channel") {
      forOne(userGen, channelGen, messageTextGen) {
        (a, g, m) =>
          // preparing
          val sessionId = await(createUser(a.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val message = await(messagesRepository.createText(channelId, m, sessionId))

          // result
          val result1 = await(userChannelsRepository.find(None, 0, 1, false, sessionId)).headOption
          assert(result1.exists(_.id == channelId))
          assert(result1.exists(_.message.exists(_.id == message.id)))

          await(userChannelsRepository.delete(channelId, sessionId))
          val result2 = await(userChannelsRepository.find(None, 0, 1, true, sessionId)).headOption
          assert(result2.exists(_.id == channelId))
          assert(result2.exists(_.message.isEmpty))
      }
    }

    scenario("should return exception if do not join.") {
      forOne(userGen, userGen, channelGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId = await(createUser(a.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(userChannelsRepository.delete(channelId, userId.sessionId))
          }.error == UserNotJoined)

      }
    }
  }

  feature("find") {
    scenario("should return an user`s channels") {
      forOne(userGen, userGen, messageTextGen, channel20SeqGen) { (s, a1, m, g) =>

        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val channels = g.map({g =>
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, ChannelPrivacyType.everyone, g.authorityType, sessionId))
          await(channelUsersRepository.create(userId1, channelId, sessionId))
          await(messagesRepository.createText(channelId, m, sessionId))
          g.copy(id = channelId)
        }).reverse

        // page1 found
        val result1 = await(userChannelsRepository.find(userId1, None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i).id)
          assert(r.message.exists(_.channelId == channels(i).id))
          assert(r.message.exists(_.message.getOrElse("") == m))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.userCount == 2))
          assert(r.message.exists(_.readUserCount == 0))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(userChannelsRepository.find(userId1, result1.lastOption.flatMap(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i + size1).id)
          assert(r.message.exists(_.channelId == channels(i + size1).id))
          assert(r.message.exists(_.message.exists(_ == m)))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.userCount == 2))
          assert(r.message.exists(_.readUserCount == 0))
        }

        // page3 not found
        val result3 = await(userChannelsRepository.find(userId1, result2.lastOption.flatMap(_.next), 0, 10, sessionId))
        assert(result3.size == 0)

      }
    }

    scenario("should return error if session id and user id is same.") {
      forOne(userGen, channelGen) {
        (s, g) =>
          // preparing
          val sessionId = await(createUser(s.userName)).id.sessionId
          await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(userChannelsRepository.find(sessionId.userId, None, 0, 10, sessionId))
          }.error == InvalidUserIdError)

      }
    }

    scenario("should return error when user is blocked.") {
      forOne(userGen, userGen, channelGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId = await(createUser(a.userName)).id
          await(blocksRepository.create(sessionId.userId, userId.sessionId))
          await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(userChannelsRepository.find(userId, None, 0, 10, sessionId))
          }.error == UserNotFound)

      }
    }
  }

  feature("find session's channels") {
    scenario("should return channels") {
      forOne(userGen, messageTextGen, channel20SeqGen) { (s, m, g) =>

        val sessionId = await(createUser(s.userName)).id.sessionId
        val channels = g.map({g =>
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, ChannelPrivacyType.everyone, g.authorityType, sessionId))
          await(messagesRepository.createText(channelId, m, sessionId))
          g.copy(id = channelId)
        }).reverse

        // page1 found
        val result1 = await(userChannelsRepository.find(None, 0, 10, false, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i).id)
          assert(r.message.exists(_.channelId == channels(i).id))
          assert(r.message.exists(_.message.getOrElse("") == m))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.userCount == 1))
          assert(r.message.exists(_.readUserCount == 0))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(userChannelsRepository.find(result1.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i + size1).id)
          assert(r.message.exists(_.channelId == channels(i + size1).id))
          assert(r.message.exists(_.message.exists(_ == m)))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.userCount == 1))
          assert(r.message.exists(_.readUserCount == 0))
        }

        // page3 not found
        val result3 = await(userChannelsRepository.find(result2.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result3.size == 0)

      }
    }

  }

  feature("show") {
    scenario("should show a channel") {
      forOne(userGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(createUser(a.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsRepository.delete(channelId, sessionId))
          await(userChannelsRepository.show(channelId, sessionId))

          // result
          val result = await(userChannelsRepository.find(None, 0, 1, false, sessionId)).headOption
          assert(result.exists(_.id == channelId))
      }
    }

    scenario("should return exception if do not join.") {
      forOne(userGen, userGen, channelGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId = await(createUser(a.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(userChannelsRepository.show(channelId, userId.sessionId))
          }.error == UserNotJoined)

      }
    }

    scenario("should return exception if a channel is already shown") {
      forOne(userGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(createUser(a.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(userChannelsRepository.show(channelId, sessionId))
          }.error == ChannelNotHidden)

      }
    }
  }

  feature("hide") {
    scenario("should hide a channel") {
      forOne(userGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(createUser(a.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsRepository.hide(channelId, sessionId))

          // result
          val result = await(userChannelsRepository.find(None, 0, 1, true, sessionId)).headOption
          assert(result.exists(_.id == channelId))
      }
    }

    scenario("should return exception if do not join.") {
      forOne(userGen, userGen, channelGen) {
        (s, a, g) =>
          // preparing
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId = await(createUser(a.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(userChannelsRepository.hide(channelId, userId.sessionId))
          }.error == UserNotJoined)

      }
    }

    scenario("should return exception if a channel is already hidden") {
      forOne(userGen, channelGen) {
        (a, g) =>

          // preparing
          val sessionId = await(createUser(a.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsRepository.hide(channelId, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(userChannelsRepository.hide(channelId, sessionId))
          }.error == ChannelAlreadyHidden)

      }

    }
  }
  


}
