package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.MediumType
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao.MediumsDAO

class MediumsRepositorySpec extends RepositorySpec {

  val mediumRepository = injector.instance[MediumsRepository]
  val mediumsDAO = injector.instance[MediumsDAO]

  test("create") {

    val sessionUser = signUp("MediumsRepositorySpec1", "session user password", "session user udid")
    val (id, _) = execute(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, sessionUser.id.toSessionId))
    assert(execute(mediumsDAO.exist(id, sessionUser.id.toSessionId)) == true)

  }

}
