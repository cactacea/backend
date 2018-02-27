package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.identifiers.AccountId
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException

class MutesRepositorySpec extends RepositorySpec {

  val mutesRepository = injector.instance[MutesRepository]
  val blocksRepository = injector.instance[BlocksRepository]

  test("mute a user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("muted user name", "muted user password", "muted user udid").account
    Await.result(mutesRepository.create(user.id, sessionUser.id.toSessionId))
    val results = Await.result(mutesRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(results.size == 1)
    val result = results(0)
    assert(user.id == result.id)

  }

  test("mute a blocked user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val blockedUser = signUp("blocked user name", "blocked user password", "blocked user udid").account

    Await.result(blocksRepository.create(blockedUser.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(mutesRepository.create(blockedUser.id, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("mute a muted user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("mute name", "mute password", "mute udid").account

    Await.result(mutesRepository.create(user.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(mutesRepository.create(user.id, sessionUser.id.toSessionId))
    }.error == AccountAlreadyMuted)

  }

  test("mute a session user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(mutesRepository.create(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

  test("delete mute") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account

    Await.result(mutesRepository.create(user.id, sessionUser.id.toSessionId))
    Await.result(mutesRepository.delete(user.id, sessionUser.id.toSessionId))

    // TODO : Check
  }

  test("delete no exist account mute") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(mutesRepository.delete(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("delete mute no muted user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account

    assert(intercept[CactaceaException] {
      Await.result(mutesRepository.delete(user.id, sessionUser.id.toSessionId))
    }.error == AccountNotMuted)

  }

  test("delete mute session user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(mutesRepository.delete(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

}
