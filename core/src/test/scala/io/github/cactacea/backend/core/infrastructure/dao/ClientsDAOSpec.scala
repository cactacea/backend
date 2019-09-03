package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class ClientsDAOSpec extends DAOSpec {

  feature("find") {
    scenario("should return a clients") {
      forOne(clientGen) { (c) =>
        val result = await(clientsDAO.find(c.id, c.secret.getOrElse("")))
        assert(result.isEmpty)
      }
    }
  }

  feature("exists") {
    scenario("should return exist or not") {
      forOne(clientGen) { (c) =>
        val result = await(clientsDAO.exists(c.id, c.secret.getOrElse(""), ""))
        assert(!result)
      }
    }
  }

}

