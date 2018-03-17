package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.dao.SocialAccountsDAO
import io.github.cactacea.core.util.responses.CactaceaErrors.{SocialAccountAlreadyConnected, SocialAccountNotConnected}
import io.github.cactacea.core.util.exceptions.CactaceaException

class SocialAccountsRepositorySpec extends RepositorySpec {

  val socialAccountsRepository = injector.instance[SocialAccountsRepository]
  var socialAccountsDAO = injector.instance[SocialAccountsDAO]

  test("connect a social account") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
    Await.result(socialAccountsRepository.create("facebook", "session token", sessionUser.id.toSessionId))
    assert(Await.result(socialAccountsDAO.exist("facebook", sessionUser.id.toSessionId)) == true)

  }

  test("find social accounts") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
    Await.result(socialAccountsRepository.create("facebook", "session token", sessionUser.id.toSessionId))
    Await.result(socialAccountsRepository.create("google", "session token", sessionUser.id.toSessionId))
    Await.result(socialAccountsRepository.create("twitter", "session token", sessionUser.id.toSessionId))

    val result = Await.result(socialAccountsRepository.findAll(sessionUser.id.toSessionId))
    assert(result.size == 3)

  }

  test("connect connected social account") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
    Await.result(socialAccountsRepository.create("facebook", "session token", sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(socialAccountsRepository.create("facebook", "session token", sessionUser.id.toSessionId))
    }.error == SocialAccountAlreadyConnected)

  }

  test("disconnect a social account") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
    Await.result(socialAccountsRepository.create("facebook", "session token", sessionUser.id.toSessionId))
    Await.result(socialAccountsRepository.delete("facebook", sessionUser.id.toSessionId))
    assert(Await.result(socialAccountsDAO.exist("facebook", sessionUser.id.toSessionId)) == false)

  }

  test("disconnect not connected social account") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account

    assert(intercept[CactaceaException] {
      Await.result(socialAccountsRepository.delete("facebook", sessionUser.id.toSessionId))
    }.error == SocialAccountNotConnected)

  }

}
