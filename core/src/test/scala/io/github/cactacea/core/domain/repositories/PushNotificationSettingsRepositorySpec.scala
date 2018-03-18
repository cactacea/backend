package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.util.responses.CactaceaErrors.AccountNotFound
import io.github.cactacea.core.util.exceptions.CactaceaException

class PushNotificationSettingsRepositorySpec extends RepositorySpec {

  val notificationSettingsRepository = injector.instance[PushNotificationSettingsRepository]

  test("find session setting") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
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

    val sessionUser = signUp("session user name", "session user password", "session udid").account

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
