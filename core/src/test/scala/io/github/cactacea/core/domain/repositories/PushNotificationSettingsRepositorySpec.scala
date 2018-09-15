package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.AccountNotFound
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class PushNotificationSettingsRepositorySpec extends RepositorySpec {

  val notificationSettingsRepository = injector.instance[PushNotificationSettingsRepository]

  test("find session setting") {

    val sessionUser = signUp("PushNotificationSettingsRepositorySpec1", "session user password", "session udid")
    val result = Await.result(notificationSettingsRepository.find(sessionUser.id.toSessionId))
    assert(result.groupMessage == true)
    assert(result.directMessage == true)
    assert(result.feedComment == true)
    assert(result.groupInvitation == true)
    assert(result.followerFeed == true)

  }

  test("find no exist session setting") {

    assert(intercept[CactaceaException] {
      Await.result(notificationSettingsRepository.find(SessionId(0L)))
    }.error == AccountNotFound)

  }

  test("update session setting") {

    val sessionUser = signUp("PushNotificationSettingsRepositorySpec2", "session user password", "session udid")

    Await.result(notificationSettingsRepository.update(false, false, false, false, false, false, sessionUser.id.toSessionId))
    val result = Await.result(notificationSettingsRepository.find(sessionUser.id.toSessionId))
    assert(result.groupMessage == false)
    assert(result.directMessage == false)
    assert(result.feedComment == false)
    assert(result.groupInvitation == false)
    assert(result.followerFeed == false)

  }

  test("update no exist session setting") {

    assert(intercept[CactaceaException] {
      Await.result(notificationSettingsRepository.update(false, false, false, false, false, false, SessionId(0L)))
    }.error == AccountNotFound)

  }

}
