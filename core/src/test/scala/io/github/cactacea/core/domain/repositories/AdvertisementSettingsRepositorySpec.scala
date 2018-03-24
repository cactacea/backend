package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.AccountNotFound
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class AdvertisementSettingsRepositorySpec extends RepositorySpec {

  val advertisementSettingsRepository = injector.instance[AdvertisementSettingsRepository]

  test("find session's setting") {

    val sessionUser = signUp("session user name", "session user password", "session udid")
    val result = Await.result(advertisementSettingsRepository.find(sessionUser.id.toSessionId))
    assert(result.ad1 == true)
    assert(result.ad2 == true)
    assert(result.ad3 == true)
    assert(result.ad4 == true)
    assert(result.ad5 == true)

  }

  test("find no exist session setting") {

    assert(intercept[CactaceaException] {
      Await.result(advertisementSettingsRepository.find(SessionId(0L)))
    }.error == AccountNotFound)

  }

  test("update session's setting") {

    val sessionUser = signUp("session user name", "session user password", "session udid")

    Await.result(advertisementSettingsRepository.update(false, false, false, false, false, sessionUser.id.toSessionId))
    val result = Await.result(advertisementSettingsRepository.find(sessionUser.id.toSessionId))
    assert(result.ad1 == false)
    assert(result.ad2 == false)
    assert(result.ad3 == false)
    assert(result.ad4 == false)
    assert(result.ad5 == false)

  }

  test("update no exist session setting") {

    assert(intercept[CactaceaException] {
      Await.result(advertisementSettingsRepository.update(false, false, false, false, false, SessionId(0L)))
    }.error == AccountNotFound)

  }

}

