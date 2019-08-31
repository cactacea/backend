package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class PushNotificationSettingsDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create notification settings") {
      forOne(accountGen) { (a) =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        await(pushNotificationSettingsDAO.create(sessionId))
        val result = await(pushNotificationSettingDAO.find(sessionId))
        assert(result.exists(_.comment))
        assert(result.exists(_.friendRequest))
        assert(result.exists(_.feed))
        assert(result.exists(_.channelMessage))
        assert(result.exists(_.invitation))
        assert(result.exists(_.showMessage))
        assert(result.exists(_.accountId == sessionId.toAccountId))
      }
    }

    scenario("should return an exception occurs if duplicated") {
      forOne(accountGen) { (a) =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        await(pushNotificationSettingsDAO.create(sessionId))

        // exception occurs
        assert(intercept[ServerError] {
          await(pushNotificationSettingsDAO.create(sessionId))
        }.code == 1062)
      }
    }

    scenario("should update notification settings") {
      forAll(accountGen, boolean7ListGen) { (a, b) =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        await(pushNotificationSettingsDAO.create(sessionId))
        await(pushNotificationSettingsDAO.update(b(0), b(1), b(2), b(3), b(4), b(5), b(6), sessionId))
        val result = await(pushNotificationSettingDAO.find(sessionId))
        assert(result.exists(_.feed == b(0)))
        assert(result.exists(_.comment == b(1)))
        assert(result.exists(_.friendRequest == b(2)))
        assert(result.exists(_.message == b(3)))
        assert(result.exists(_.channelMessage == b(4)))
        assert(result.exists(_.invitation == b(5)))
        assert(result.exists(_.showMessage == b(6)))
        assert(result.exists(_.accountId == sessionId.toAccountId))
      }
    }

  }

}
