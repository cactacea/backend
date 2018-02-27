package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.dao.MediumsDAO

class MediumsRepositorySpec extends RepositorySpec {

  val mediumRepository = injector.instance[MediumsRepository]
  val mediumsDAO = injector.instance[MediumsDAO]

  test("create") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val mediumCreated = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, sessionUser.id.toSessionId))
    assert(Await.result(mediumsDAO.exist(mediumCreated.id, sessionUser.id.toSessionId)) == true)

  }

}
