package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class NotificationSettingsDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create notification settings") {
      forOne(userGen) { (a) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        await(notificationSettingsDAO.create(sessionId))
        val result = await(notificationSettingDAO.find(sessionId))
        assert(result.exists(_.comment))
        assert(result.exists(_.friendRequest))
        assert(result.exists(_.tweet))
        assert(result.exists(_.channelMessage))
        assert(result.exists(_.invitation))
        assert(result.exists(_.showMessage))
        assert(result.exists(_.userId == sessionId.userId))
      }
    }

    scenario("should return an exception occurs if duplicated") {
      forOne(userGen) { (a) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        await(notificationSettingsDAO.create(sessionId))

        // exception occurs
        assert(intercept[ServerError] {
          await(notificationSettingsDAO.create(sessionId))
        }.code == 1062)
      }
    }

    scenario("should update notification settings") {
      forAll(userGen, boolean7SeqGen) { (a, b) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        await(notificationSettingsDAO.create(sessionId))
        await(notificationSettingsDAO.update(b(0), b(1), b(2), b(3), b(4), b(5), b(6), sessionId))
        val result = await(notificationSettingDAO.find(sessionId))
        assert(result.exists(_.tweet == b(0)))
        assert(result.exists(_.comment == b(1)))
        assert(result.exists(_.friendRequest == b(2)))
        assert(result.exists(_.message == b(3)))
        assert(result.exists(_.channelMessage == b(4)))
        assert(result.exists(_.invitation == b(5)))
        assert(result.exists(_.showMessage == b(6)))
        assert(result.exists(_.userId == sessionId.userId))
      }
    }

  }

}
