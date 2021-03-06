package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class MediumsDAOSpec extends DAOSpec {

  feature("create and exist(mediumId: sessionId)") {
    forAll(userGen, mediumGen) { (a, m) =>
      val sessionId = await(usersDAO.create(a.userName)).sessionId
      val mediumId = await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))
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

  feature("delete and exist(mediumId: sessionId)") {
    forAll(userGen, mediumGen) { (a, m) =>
      val sessionId = await(usersDAO.create(a.userName)).sessionId
      val mediumId = await(mediumsDAO.create(
        m.key,
        m.uri,
        m.thumbnailUrl,
        m.mediumType,
        m.width,
        m.height,
        m.size,
        sessionId))
      assert(await(mediumsDAO.own(mediumId, sessionId)))
      await(mediumsDAO.delete(mediumId))
      assert(!await(mediumsDAO.own(mediumId, sessionId)))
    }
  }


  feature("exist(mediumIds: sessionId)") {
    forAll(userGen, medium5SeqGen) { (a, m) =>
      val sessionId = await(usersDAO.create(a.userName)).sessionId
      val mediumIds = m.map({ m =>
        await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))
      })
      assert(await(mediumsDAO.exists(mediumIds, sessionId)))
      mediumIds.map({ id => await(mediumsDAO.delete(id)) })
      assert(!await(mediumsDAO.exists(mediumIds, sessionId)))
    }
  }

}
