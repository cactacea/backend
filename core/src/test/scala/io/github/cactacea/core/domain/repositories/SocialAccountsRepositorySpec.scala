package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao.SocialAccountsDAO
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{SocialAccountAlreadyConnected, SocialAccountNotConnected}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class SocialAccountsRepositorySpec extends RepositorySpec {

  val socialAccountsRepository = injector.instance[SocialAccountsRepository]
  var socialAccountsDAO = injector.instance[SocialAccountsDAO]

  test("connect a social account") {

    val sessionUser = signUp("SocialAccountsRepositorySpec1", "SocialAccountsRepositorySpec1", "session udid")
    Await.result(socialAccountsRepository.create("facebook", "SocialAccountsRepositorySpec2", sessionUser.id.toSessionId))
    assert(Await.result(socialAccountsDAO.exist("facebook", sessionUser.id.toSessionId)) == true)

  }

  test("find social accounts") {

    val sessionUser = signUp("SocialAccountsRepositorySpec2", "session user password", "session udid")
    Await.result(socialAccountsRepository.create("facebook", "SocialAccountsRepositorySpec3", sessionUser.id.toSessionId))
    Await.result(socialAccountsRepository.create("google", "SocialAccountsRepositorySpec4", sessionUser.id.toSessionId))
    Await.result(socialAccountsRepository.create("twitter", "SocialAccountsRepositorySpec5", sessionUser.id.toSessionId))

    val result = Await.result(socialAccountsRepository.findAll(sessionUser.id.toSessionId))
    assert(result.size == 3)

  }

  test("connect connected social account") {

    val sessionUser = signUp("SocialAccountsRepositorySpec3", "session user password", "session udid")
    Await.result(socialAccountsRepository.create("facebook", "SocialAccountsRepositorySpec6", sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(socialAccountsRepository.create("facebook", "SocialAccountsRepositorySpec7", sessionUser.id.toSessionId))
    }.error == SocialAccountAlreadyConnected)

  }

  test("disconnect a social account") {

    val sessionUser = signUp("SocialAccountsRepositorySpec4", "session user password", "session udid")
    Await.result(socialAccountsRepository.create("facebook", "SocialAccountsRepositorySpec8", sessionUser.id.toSessionId))
    Await.result(socialAccountsRepository.delete("facebook", sessionUser.id.toSessionId))
    assert(Await.result(socialAccountsDAO.exist("facebook", sessionUser.id.toSessionId)) == false)

  }

  test("disconnect not connected social account") {

    val sessionUser = signUp("SocialAccountsRepositorySpec5", "session user password", "session udid")

    assert(intercept[CactaceaException] {
      Await.result(socialAccountsRepository.delete("facebook", sessionUser.id.toSessionId))
    }.error == SocialAccountNotConnected)

  }

}
