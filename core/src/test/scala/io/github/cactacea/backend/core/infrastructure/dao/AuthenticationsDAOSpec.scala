package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class AuthenticationsDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create a authentication") {
      forAll(authenticationGen) { (u) =>
        await(authenticationsDAO.create(u.providerId, u.providerKey, u.password, u.hasher))
        val result = await(authenticationsDAO.find(u.providerId, u.providerKey))
        assert(result.headOption.exists(_.providerId == u.providerId))
        assert(result.headOption.exists(_.providerKey == u.providerKey))
        assert(result.headOption.exists(_.accountId.isEmpty))
        assert(result.headOption.exists(_.password == u.password))
        assert(result.headOption.exists(_.hasher == u.hasher))
        assert(result.headOption.exists(!_.confirm))
      }
    }
  }

  feature("update") {
    scenario("should update password and hasher") {
      forOne(authenticationGen, authenticationGen) { (u1, u2) =>
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.update(u1.providerId, u1.providerKey, u2.password, u2.hasher))
        val result = await(authenticationsDAO.find(u1.providerId, u1.providerKey))
        assert(result.headOption.exists(_.providerId == u1.providerId))
        assert(result.headOption.exists(_.providerKey == u1.providerKey))
        assert(result.headOption.exists(_.accountId.isEmpty))
        assert(result.headOption.exists(_.password == u2.password))
        assert(result.headOption.exists(_.hasher == u2.hasher))
        assert(result.headOption.exists(!_.confirm))
      }
    }

    scenario("should update confirm") {
      forOne(authenticationGen) { (u) =>
        await(authenticationsDAO.create(u.providerId, u.providerKey, u.password, u.hasher))
        val result1 = await(authenticationsDAO.find(u.providerId, u.providerKey))
        assert(result1.headOption.exists(_.providerId == u.providerId))
        assert(result1.headOption.exists(_.providerKey == u.providerKey))
        assert(result1.headOption.exists(!_.confirm))

        await(authenticationsDAO.updateConfirm(u.providerId, u.providerKey, true))
        val result2 = await(authenticationsDAO.find(u.providerId, u.providerKey))
        assert(result2.headOption.exists(_.providerId == u.providerId))
        assert(result2.headOption.exists(_.providerKey == u.providerKey))
        assert(result2.headOption.exists(_.confirm))
      }
    }

    scenario("should update account id") {
      forOne(accountGen, authenticationGen) { (s, u1) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.updateAccountId(u1.providerId, u1.providerKey, sessionId))
        val result = await(authenticationsDAO.find(u1.providerId, u1.providerKey))
        assert(result.headOption.exists(_.providerId == u1.providerId))
        assert(result.headOption.exists(_.providerKey == u1.providerKey))
        assert(result.headOption.exists(_.accountId.exists(_ == sessionId.toAccountId)))
        assert(result.headOption.exists(_.password == u1.password))
        assert(result.headOption.exists(_.hasher == u1.hasher))
        assert(result.headOption.exists(!_.confirm))
      }
    }

    scenario("should update provider key") {
      forOne(accountGen, authenticationGen, authenticationGen) { (s, u1, u2) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.updateAccountId(u1.providerId, u1.providerKey, sessionId))
        await(authenticationsDAO.updateProviderKey(u1.providerId, u2.providerKey, sessionId))
        val result = await(authenticationsDAO.find(u1.providerId, u2.providerKey))
        assert(result.headOption.exists(_.providerId == u1.providerId))
        assert(result.headOption.exists(_.providerKey == u2.providerKey))
        assert(result.headOption.exists(_.accountId.exists(_ == sessionId.toAccountId)))
        assert(result.headOption.exists(_.password == u1.password))
        assert(result.headOption.exists(_.hasher == u1.hasher))
        assert(result.headOption.exists(!_.confirm))
      }
    }
  }



  feature("exists") {
    scenario("should return exist or not") {
      forOne(authenticationGen, authenticationGen) { (u1, u2) =>
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        assertFutureValue(authenticationsDAO.exists(u1.providerId, u1.providerKey), true)
        assertFutureValue(authenticationsDAO.exists(u2.providerId, u2.providerKey), false)
      }
    }
  }

  feature("find") {
    scenario("should find an authentication") {
      forOne(authenticationGen, authenticationGen) { (u1, u2) =>
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.create(u2.providerId, u2.providerKey, u2.password, u2.hasher))

        val result1 = await(authenticationsDAO.find(u1.providerId, u1.providerKey))
        assert(result1.headOption.exists(_.providerId == u1.providerId))
        assert(result1.headOption.exists(_.providerKey == u1.providerKey))
        assert(result1.headOption.exists(_.accountId.isEmpty))
        assert(result1.headOption.exists(_.password == u1.password))
        assert(result1.headOption.exists(_.hasher == u1.hasher))
        assert(result1.headOption.exists(!_.confirm))

        val result2 = await(authenticationsDAO.find(u2.providerId, u2.providerKey))
        assert(result2.headOption.exists(_.providerId == u2.providerId))
        assert(result2.headOption.exists(_.providerKey == u2.providerKey))
        assert(result2.headOption.exists(_.accountId.isEmpty))
        assert(result2.headOption.exists(_.password == u2.password))
        assert(result2.headOption.exists(_.hasher == u2.hasher))
        assert(result2.headOption.exists(!_.confirm))
      }
    }

    scenario("should filter by confirm ") {
      forOne(authenticationGen, authenticationGen) { (u1, u2) =>
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.create(u2.providerId, u2.providerKey, u2.password, u2.hasher))

        await(authenticationsDAO.updateConfirm(u1.providerId, u1.providerKey, true))
        val result1 = await(authenticationsDAO.find(u1.providerId, u1.providerKey, true))
        assert(result1.headOption.exists(_.providerId == u1.providerId))
        assert(result1.headOption.exists(_.providerKey == u1.providerKey))
        assert(result1.headOption.exists(_.accountId.isEmpty))
        assert(result1.headOption.exists(_.password == u1.password))
        assert(result1.headOption.exists(_.hasher == u1.hasher))
        assert(result1.headOption.exists(_.confirm))

        await(authenticationsDAO.updateConfirm(u2.providerId, u2.providerKey, true))
        val result2 = await(authenticationsDAO.find(u2.providerId, u2.providerKey, false))
        assert(result2.isEmpty)
      }
    }

  }


  feature("delete") {
    scenario("should delete an authentication") {
      forOne(authenticationGen, authenticationGen) { (u1, u2) =>
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.create(u2.providerId, u2.providerKey, u2.password, u2.hasher))

        await(authenticationsDAO.delete(u2.providerId, u2.providerKey))
        val result1 = await(authenticationsDAO.find(u1.providerId, u1.providerKey))
        assert(result1.headOption.exists(_.providerId == u1.providerId))
        assert(result1.headOption.exists(_.providerKey == u1.providerKey))
        assert(result1.headOption.exists(_.accountId.isEmpty))
        assert(result1.headOption.exists(_.password == u1.password))
        assert(result1.headOption.exists(_.hasher == u1.hasher))
        assert(result1.headOption.exists(!_.confirm))

        val result2 = await(authenticationsDAO.find(u2.providerId, u2.providerKey))
        assert(result2.isEmpty)
      }
    }
  }


}

