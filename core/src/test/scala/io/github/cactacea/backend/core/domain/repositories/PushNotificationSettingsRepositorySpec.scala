package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.helpers.specs.RepositorySpec

class PushNotificationSettingsRepositorySpec extends RepositorySpec {

  feature("update") {

    scenario("should update notification settings") {
      forAll(accountGen, boolean7ListGen) { (a, b) =>
        val sessionId = await(accountsRepository.create(a.accountName)).id.toSessionId
        await(pushNotificationSettingsRepository.update(b(0), b(1), b(2), b(3), b(4), b(5), b(6), sessionId))
        val result = await(pushNotificationSettingsRepository.find(sessionId))
        assert(result.feed == b(0))
        assert(result.comment == b(1))
        assert(result.friendRequest == b(2))
        assert(result.message == b(3))
        assert(result.groupMessage == b(4))
        assert(result.invitation == b(5))
        assert(result.showMessage == b(6))
      }
    }

  }

}
