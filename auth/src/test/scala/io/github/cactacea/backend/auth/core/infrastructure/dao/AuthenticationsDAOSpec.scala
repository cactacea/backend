package io.github.cactacea.backend.auth.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.auth.core.infrastructure.models.Authentications
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import org.scalacheck.Gen

class AuthenticationsDAOSpec extends DAOSpec {

  lazy val authenticationGen: Gen[Authentications] = for {
    providerId <- Gen.listOfN(30, Gen.alphaChar).map(_.mkString)
    providerKey <- uniqueGen.map(no => s"provider_key_${no}")
    password <- passwordGen
    hasher <-  hasherGen
    confirm <- booleanGen
  } yield (Authentications(providerId, providerKey, password, hasher, confirm))

  val authenticationsDAO = injector.instance[AuthenticationsDAO]

  feature("create") {

    scenario("should create a authentication") {
      forAll(authenticationGen) { (u) =>
        await(authenticationsDAO.create(u.providerId, u.providerKey, u.password, u.hasher))
        val result = await(authenticationsDAO.find(u.providerId, u.providerKey))
        assert(result.headOption.exists(_.providerId == u.providerId))
        assert(result.headOption.exists(_.providerKey == u.providerKey))
        assert(result.headOption.exists(_.password == u.password))
        assert(result.headOption.exists(_.hasher == u.hasher))
        assert(result.headOption.exists(!_.confirm))
      }
    }

    scenario("should return exception if duplicated") {
      forOne(authenticationGen) {
        (a) =>
          await(authenticationsDAO.create(a.providerId, a.providerKey, a.password, a.hasher))
          // exception occurs
          assert(intercept[ServerError] {
            await(authenticationsDAO.create(a.providerId, a.providerKey, a.password, a.hasher))
          }.code == 1062)
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

    scenario("should update provider key") {
      forOne(authenticationGen, authenticationGen) { (u1, u2) =>
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.updateProviderKey(u1.providerId, u1.providerKey, u2.providerKey))
        val result = await(authenticationsDAO.find(u1.providerId, u2.providerKey))
        assert(result.headOption.exists(_.providerId == u1.providerId))
        assert(result.headOption.exists(_.providerKey == u2.providerKey))
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
        assert(result1.headOption.exists(_.password == u1.password))
        assert(result1.headOption.exists(_.hasher == u1.hasher))
        assert(result1.headOption.exists(!_.confirm))

        val result2 = await(authenticationsDAO.find(u2.providerId, u2.providerKey))
        assert(result2.headOption.exists(_.providerId == u2.providerId))
        assert(result2.headOption.exists(_.providerKey == u2.providerKey))
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
        val result1 = await(authenticationsDAO.find(u1.providerId, u1.providerKey))
        assert(result1.headOption.exists(_.providerId == u1.providerId))
        assert(result1.headOption.exists(_.providerKey == u1.providerKey))
        assert(result1.headOption.exists(_.password == u1.password))
        assert(result1.headOption.exists(_.hasher == u1.hasher))
        assert(result1.headOption.exists(_.confirm))

        val result2 = await(authenticationsDAO.find(u2.providerId, u2.providerKey))
        assert(result2.headOption.exists(_.providerId == u2.providerId))
        assert(result2.headOption.exists(_.providerKey == u2.providerKey))
        assert(result2.headOption.exists(_.password == u2.password))
        assert(result2.headOption.exists(_.hasher == u2.hasher))
        assert(result2.headOption.exists(!_.confirm))
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
        assert(result1.headOption.exists(_.password == u1.password))
        assert(result1.headOption.exists(_.hasher == u1.hasher))
        assert(result1.headOption.exists(!_.confirm))

        val result2 = await(authenticationsDAO.find(u2.providerId, u2.providerKey))
        assert(result2.isEmpty)
      }
    }
  }


}

