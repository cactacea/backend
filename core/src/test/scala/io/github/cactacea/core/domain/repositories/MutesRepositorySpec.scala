package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class MutesRepositorySpec extends RepositorySpec {

  val mutesRepository = injector.instance[MutesRepository]
  val blocksRepository = injector.instance[BlocksRepository]

  test("mute a user") {

    val sessionUser = signUp("MutesRepositorySpec1", "session user password", "session user udid")
    val user = signUp("MutesRepositorySpec2", "muted user password", "muted user udid")
    execute(mutesRepository.create(user.id, sessionUser.id.toSessionId))
    val results = execute(mutesRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(results.size == 1)
    val result = results(0)
    assert(user.id == result.id)

  }

  test("mute a blocked user") {

    val sessionUser = signUp("MutesRepositorySpec3", "session user password", "session user udid")
    val blockingUser = signUp("MutesRepositorySpec4", "blocked user password", "blocked user udid")

    execute(blocksRepository.create(sessionUser.id, blockingUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(mutesRepository.create(sessionUser.id, blockingUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("mute a muted user") {

    val sessionUser = signUp("MutesRepositorySpec5", "session user password", "session user udid")
    val user = signUp("MutesRepositorySpec6", "mute password", "mute udid")

    execute(mutesRepository.create(user.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(mutesRepository.create(user.id, sessionUser.id.toSessionId))
    }.error == AccountAlreadyMuted)

  }

  test("mute a session user") {

    val sessionUser = signUp("MutesRepositorySpec7", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      execute(mutesRepository.create(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

  test("delete mute") {

    val sessionUser = signUp("MutesRepositorySpec8", "session user password", "session user udid")
    val user = signUp("MutesRepositorySpec9", "user password", "user udid")

    execute(mutesRepository.create(user.id, sessionUser.id.toSessionId))
    execute(mutesRepository.delete(user.id, sessionUser.id.toSessionId))

    // TODO : Check
  }

  test("delete no exist account mute") {

    val sessionUser = signUp("MutesRepositorySpec10", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      execute(mutesRepository.delete(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("delete mute no muted user") {

    val sessionUser = signUp("MutesRepositorySpec11", "session user password", "session user udid")
    val user = signUp("MutesRepositorySpec12", "user password", "user udid")

    assert(intercept[CactaceaException] {
      execute(mutesRepository.delete(user.id, sessionUser.id.toSessionId))
    }.error == AccountNotMuted)

  }

  test("delete mute session user") {

    val sessionUser = signUp("MutesRepositorySpec13", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      execute(mutesRepository.delete(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

}
