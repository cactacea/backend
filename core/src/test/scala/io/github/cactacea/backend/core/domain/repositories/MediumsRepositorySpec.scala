package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec

class MediumsRepositorySpec extends RepositorySpec {


  feature("create") {
    forAll(accountGen, mediumGen) { (a, m) =>

      // preparing
      val sessionId = await(accountsRepository.create(a.accountName)).id.toSessionId
      val mediumId = await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))

      // result
      val result = await(mediumsDAO.find(mediumId, sessionId))
      assert(result.map(_.key) == Option(m.key))
      assert(result.map(_.uri) == Option(m.uri))
      assert(result.map(_.thumbnailUrl) == Option(m.thumbnailUrl))
      assert(result.map(_.mediumType) == Option(m.mediumType))
      assert(result.map(_.width) == Option(m.width))
      assert(result.map(_.height) == Option(m.height))
      assert(result.map(_.size) == Option(m.size))
    }
  }

  feature("delete") {
    forAll(accountGen, mediumGen) { (a, m) =>

      // preparing
      val sessionId = await(accountsRepository.create(a.accountName)).id.toSessionId
      val mediumId = await(mediumsRepository.create(
        m.key,
        m.uri,
        m.thumbnailUrl,
        m.mediumType,
        m.width,
        m.height,
        m.size,
        sessionId))

      // result
      assert(await(mediumsDAO.own(mediumId, sessionId)))
      await(mediumsRepository.delete(mediumId, sessionId))
      assert(!await(mediumsDAO.own(mediumId, sessionId)))
    }
  }
}
