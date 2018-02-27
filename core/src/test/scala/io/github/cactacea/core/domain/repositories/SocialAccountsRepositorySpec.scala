package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.SocialAccountType
import io.github.cactacea.core.infrastructure.dao.SocialAccountsDAO
import io.github.cactacea.core.specs.RepositorySpec
import io.github.cactacea.core.util.responses.CactaceaError.{SocialAccountAlreadyConnected, SocialAccountNotConnected}
import io.github.cactacea.core.util.exceptions.CactaceaException

class SocialAccountsRepositorySpec extends RepositorySpec {

  val socialAccountsRepository = injector.instance[SocialAccountsRepository]
  var socialAccountsDAO = injector.instance[SocialAccountsDAO]

  test("connect a social account") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
    Await.result(socialAccountsRepository.create(SocialAccountType.facebook, "session token", sessionUser.id.toSessionId))
    assert(Await.result(socialAccountsDAO.exist(SocialAccountType.facebook, sessionUser.id.toSessionId)) == true)

  }

  test("find social accounts") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
    Await.result(socialAccountsRepository.create(SocialAccountType.facebook, "session token", sessionUser.id.toSessionId))
    Await.result(socialAccountsRepository.create(SocialAccountType.google, "session token", sessionUser.id.toSessionId))
    Await.result(socialAccountsRepository.create(SocialAccountType.twitter, "session token", sessionUser.id.toSessionId))

    val result = Await.result(socialAccountsRepository.findAll(sessionUser.id.toSessionId))
    assert(result.size == 3)

  }

  test("connect connected social account") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
    Await.result(socialAccountsRepository.create(SocialAccountType.facebook, "session token", sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(socialAccountsRepository.create(SocialAccountType.facebook, "session token", sessionUser.id.toSessionId))
    }.error == SocialAccountAlreadyConnected)

  }

  test("disconnect a social account") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
    Await.result(socialAccountsRepository.create(SocialAccountType.facebook, "session token", sessionUser.id.toSessionId))
    Await.result(socialAccountsRepository.delete(SocialAccountType.facebook, sessionUser.id.toSessionId))
    assert(Await.result(socialAccountsDAO.exist(SocialAccountType.facebook, sessionUser.id.toSessionId)) == false)

  }

  test("disconnect not connected social account") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account

    assert(intercept[CactaceaException] {
      Await.result(socialAccountsRepository.delete(SocialAccountType.facebook, sessionUser.id.toSessionId))
    }.error == SocialAccountNotConnected)

  }

}
