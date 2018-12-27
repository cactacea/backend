package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.AccountNotFound

class PushNotificationSettingsRepositorySpec extends RepositorySpec {

  val notificationSettingsRepository = injector.instance[PushNotificationSettingsRepository]

  test("find session setting") {

    val sessionUser = signUp("PushNotificationSettingsRepositorySpec1", "session user password", "session udid")
    val result = execute(notificationSettingsRepository.find(sessionUser.id.toSessionId))
    assert(result.groupMessage == true)
    assert(result.message == true)
    assert(result.comment == true)
    assert(result.groupInvitation == true)
    assert(result.feed == true)

  }

  test("find no exist session setting") {

    assert(intercept[CactaceaException] {
      execute(notificationSettingsRepository.find(SessionId(0L)))
    }.error == AccountNotFound)

  }

  test("update session setting") {

    val sessionUser = signUp("PushNotificationSettingsRepositorySpec2", "session user password", "session udid")

    execute(notificationSettingsRepository.update(false, false, false, false, false, false, false, sessionUser.id.toSessionId))
    val result = execute(notificationSettingsRepository.find(sessionUser.id.toSessionId))
    assert(!result.groupMessage)
    assert(!result.message)
    assert(!result.comment)
    assert(!result.groupInvitation)
    assert(!result.friendRequest)
    assert(!result.feed)

  }

}
