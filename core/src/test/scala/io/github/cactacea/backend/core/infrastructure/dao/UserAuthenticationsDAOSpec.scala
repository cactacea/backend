package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class UserAuthenticationsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should create a authentication") {
      forAll(userGen, userAuthenticationGen) { (u, a) =>
        val userId = await(usersDAO.create(u.userName))
        await(userAuthenticationsDAO.create(userId, a.providerId, a.providerKey))
        val result = await(findUserAuthentication(a.providerId, a.providerKey))
        assert(result.headOption.exists(_.providerId == a.providerId))
        assert(result.headOption.exists(_.providerKey == a.providerKey))
        assert(result.headOption.exists(_.userId == userId))
      }
    }

    scenario("should return exception if duplicated") {
      forOne(userGen, userAuthenticationGen) {
        (u, a) =>
          val userId = await(usersDAO.create(u.userName))
          await(userAuthenticationsDAO.create(userId, a.providerId, a.providerKey))
          // exception occurs
          assert(intercept[ServerError] {
            await(userAuthenticationsDAO.create(userId, a.providerId, a.providerKey))
          }.code == 1062)
      }
    }

  }

  feature("update") {
    scenario("should update provider key") {
      forOne(userGen, userAuthenticationGen, userAuthenticationGen) { (u, a1, a2) =>
        val userId = await(usersDAO.create(u.userName))
        await(userAuthenticationsDAO.create(userId, a1.providerId, a1.providerKey))
        await(userAuthenticationsDAO.update(a1.providerId, a1.providerKey, a2.providerKey))
        val result = await(findUserAuthentication(a1.providerId, a2.providerKey))
        assert(result.headOption.exists(_.providerId == a1.providerId))
        assert(result.headOption.exists(_.providerKey == a2.providerKey))
        assert(result.headOption.exists(_.userId == userId))
      }
    }
  }



  feature("exists") {
    scenario("should return exist or not") {
      forOne(userGen, userAuthenticationGen, userAuthenticationGen) { (u, a1, a2) =>
        val userId = await(usersDAO.create(u.userName))
        await(userAuthenticationsDAO.create(userId, a1.providerId, a1.providerKey))
        assertFutureValue(userAuthenticationsDAO.exists(a1.providerId, a1.providerKey), true)
        assertFutureValue(userAuthenticationsDAO.exists(a2.providerId, a2.providerKey), false)
      }
    }
  }

  feature("find") {
    scenario("should find an authentication") {
      forOne(userGen, userGen, userAuthenticationGen, userAuthenticationGen) { (u1, u2, a1, a2) =>
        val userId1 = await(usersDAO.create(u1.userName))
        val userId2 = await(usersDAO.create(u2.userName))
        await(userAuthenticationsDAO.create(userId1, a1.providerId, a1.providerKey))
        await(userAuthenticationsDAO.create(userId2, a2.providerId, a2.providerKey))

        val result1 = await(findUserAuthentication(a1.providerId, a1.providerKey))
        assert(result1.headOption.exists(_.providerId == a1.providerId))
        assert(result1.headOption.exists(_.providerKey == a1.providerKey))
        assert(result1.headOption.exists(_.userId == userId1))

        val result2 = await(findUserAuthentication(a2.providerId, a2.providerKey))
        assert(result2.headOption.exists(_.providerId == a2.providerId))
        assert(result2.headOption.exists(_.providerKey == a2.providerKey))
        assert(result2.headOption.exists(_.userId == userId2))
      }
    }
  }

}

