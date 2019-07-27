package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

class AccountsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should create account") {
      forAll(accountGen) { a =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        val result = await(accountsDAO.find(sessionId))
        assert(result.exists(_.accountName == a.accountName))
        assert(result.exists(_.accountStatus == AccountStatusType.normally))
      }
    }

    scenario("should create an account and update display name") {
      forAll(accountGen) { a =>
        val sessionId = await(accountsDAO.create(a.accountName, a.displayName)).toSessionId
        val result = await(accountsDAO.find(sessionId))
        assert(result.exists(_.accountName == a.accountName))
        assert(result.exists(_.displayName == a.displayName))
        assert(result.exists(_.accountStatus == AccountStatusType.normally))
      }
    }

    scenario("should return exception if duplicate account name") {
      forOne(accountGen) {
        a1 =>
          await(accountsDAO.create(a1.accountName)).toSessionId
          // exception occurs
          assert(intercept[ServerError] {
            await(accountsDAO.create(a1.accountName))
          }.code == 1062)
      }
    }

  }


  feature("updateProfile") {
    scenario("should update account profile") {
      forAll(accountGen) { a =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        await(accountsDAO.updateProfile(
          a.displayName,
          a.web,
          a.birthday,
          a.location,
          a.bio,
          sessionId))
        val result = await(accountsDAO.find(sessionId))
        assert(result.map(_.accountName) == Option(a.accountName))
        assert(result.map(_.displayName) == Option(a.displayName))
        assert(result.map(_.web) == Option(a.web))
        assert(result.map(_.birthday) == Option(a.birthday))
        assert(result.map(_.location) == Option(a.location))
        assert(result.map(_.bio) == Option(a.bio))
      }
    }
  }

  feature("updateProfileImageUrl") {
    scenario("should update account profile image") {
      forAll(accountGen, mediumGen) { (a, m) =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        val mediumId = await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))
        await(accountsDAO.updateProfileImageUrl(Option(m.uri), Option(mediumId), sessionId))
        val result1 = await(accountsDAO.find(sessionId))
        assert(result1.flatMap(_.profileImageUrl) == Option(m.uri))
        await(accountsDAO.updateProfileImageUrl(None, None, sessionId))
        val result2 = await(accountsDAO.find(sessionId))
        assert(result2.flatMap(_.profileImageUrl).isEmpty)
      }
    }
  }

  feature("updateAccountName") {
    scenario("should update account name") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        await(accountsDAO.create(a3.accountName))
        val result1 = await(accountsDAO.find(sessionId))
        assert(result1.map(_.accountName) == Option(a1.accountName))
        assert(result1.map(_.displayName) == Option(a1.accountName))
        await(accountsDAO.updateAccountName(a2.accountName, sessionId))
        val result2 = await(accountsDAO.find(sessionId))
        assert(result2.map(_.accountName) == Option(a2.accountName))
      }
    }

    scenario("should return exception if duplicate account name") {
      forOne(accountGen, accountGen) { (a1, a2) =>
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        await(accountsDAO.create(a2.accountName))
        // exception occurs
        assert(intercept[ServerError] {
          await(accountsDAO.updateAccountName(a2.accountName, sessionId))
        }.code == 1062)
      }
    }
  }

  feature("updateAccountStatus") {
    scenario("should update account status") {
      forAll(accountGen) { a =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        await(accountsDAO.updateAccountStatus(a.accountStatus, sessionId))
        val result = await(accountsDAO.find(sessionId))
        assert(result.exists(_.accountStatus == a.accountStatus))
      }
    }
  }



  feature("updateDisplayName") {
    scenario("should update display name") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, d) =>
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a2.accountName))
        await(accountsDAO.updateDisplayName(accountId, Option(d.displayName), sessionId))
        val result1 = await(accountsDAO.find(accountId, sessionId))
        assert(result1.map(_.displayName) == Option(d.displayName))
        await(accountsDAO.updateDisplayName(accountId, None, sessionId))
        val result2 = await(accountsDAO.find(accountId, sessionId))
        assert(result2.map(_.displayName) == Option(a2.accountName))
      }
    }
  }


  feature("exist - by account name") {
    scenario("should return account name exist or not") {
      forAll(accountGen, accountGen) { (a1, a2) =>
        await(accountsDAO.create(a1.accountName))
        assert(await(accountsDAO.exists(a1.accountName)))
        assert(!await(accountsDAO.exists(a2.accountName)))
      }
    }
    scenario("should return account name exist or not ignore session account") {
      forAll(accountGen, accountGen) { (a1, a2) =>
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        assert(!await(accountsDAO.exists(a1.accountName, sessionId)))
        assert(!await(accountsDAO.exists(a2.accountName, sessionId)))
      }
    }
  }


  feature("exist - by account id") {
    scenario("should return account exist or not") {
      forAll(accountGen) { (a) =>
        val accountId = await(accountsDAO.create(a.accountName))
        assert(await(accountsDAO.exists(accountId)))
        assert(!await(accountsDAO.exists(AccountId(0L))))
      }
    }
    scenario("should return account id not exist when account is blocked") {
      forAll(accountGen, accountGen, accountGen, accountGen) { (a1, a2, a3, a4) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(blocksDAO.create(accountId3, accountId1.toSessionId))
        await(blocksDAO.create(accountId1, accountId2.toSessionId))
        await(blocksDAO.create(accountId2, accountId3.toSessionId))
        assert(!await(accountsDAO.exists(accountId2, accountId1.toSessionId)))
        assert(await(accountsDAO.exists(accountId3, accountId1.toSessionId)))
        assert(await(accountsDAO.exists(accountId1, accountId2.toSessionId)))
        assert(!await(accountsDAO.exists(accountId3, accountId2.toSessionId)))
        assert(!await(accountsDAO.exists(accountId1, accountId3.toSessionId)))
        assert(await(accountsDAO.exists(accountId2, accountId3.toSessionId)))
        assert(await(accountsDAO.exists(accountId1, accountId4.toSessionId)))
        assert(await(accountsDAO.exists(accountId2, accountId4.toSessionId)))
        assert(await(accountsDAO.exists(accountId3, accountId4.toSessionId)))
      }
    }
  }


  feature("exist - by account ids") {
    scenario("should return all account exist or not") {
      forAll(accountGen, accountGen, accountGen, accountGen) { (a1, a2, a3, a4) =>
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(blocksDAO.create(accountId3, sessionId))
        await(blocksDAO.create(sessionId.toAccountId, accountId4.toSessionId))
        assert(await(accountsDAO.exists(List(accountId2, accountId3), sessionId)))
        assert(!await(accountsDAO.exists(List(accountId2, accountId4), sessionId)))
        assert(await(accountsDAO.exists(List(accountId2), sessionId)))
      }
    }
  }

  feature("signOut") {
    scenario("should sign out") {
      forAll(accountGen) { a =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        val result1 = await(accountsDAO.find(sessionId))
        assert(result1.flatMap(_.signedOutAt).isEmpty)
        await(accountsDAO.signOut(sessionId))
        val result2 = await(accountsDAO.find(sessionId))
        assert(result2.flatMap(_.signedOutAt).isDefined)
      }
    }
  }

  feature("find an account") {
    scenario("should return an account") {
      forAll(accountGen, accountGen, accountGen, accountGen) { (s, a1, a2, a3) =>

        // preparing
        //  account2 block session account
        //  session account block account3
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(blocksDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        await(blocksDAO.create(accountId3, sessionId))

        // return account1 found
        // return account2 not found
        // return account3 found
        val result1 = await(accountsDAO.find(accountId1, sessionId))
        val result2 = await(accountsDAO.find(accountId2, sessionId))
        val result3 = await(accountsDAO.find(accountId3, sessionId))
        assert(result1.isDefined)
        assert(result2.isEmpty)
        assert(result3.isDefined)

      }
    }

  }

  feature("find an authentication") {
    scenario("should return an account") {
      forAll(accountGen, authenticationGen, authenticationGen) { (s, a1, a2) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        await(authenticationsDAO.create(a1.providerId, a1.providerKey, a1.password, a1.hasher))
        await(authenticationsDAO.updateAccountId(a1.providerId, a1.providerKey, sessionId))
        val result1 = await(accountsDAO.find(a1.providerId, a1.providerKey))
        val result2 = await(accountsDAO.find(a2.providerId, a2.providerKey))
        assert(result1.isDefined)
        assert(result2.isEmpty)
      }
    }
  }

  feature("find account list") {
    scenario("should return account list") {
      forAll(sortedNameGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //  account2 block session account
        //  session account block account3
        val sessionId = await(accountsDAO.create(h + s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(h + a1.accountName))
        val accountId2 = await(accountsDAO.create(h + a2.accountName))
        val accountId3 = await(accountsDAO.create(h + a3.accountName))
        await(accountsDAO.create(h + a4.accountName))
        await(blocksDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        await(blocksDAO.create(accountId3, sessionId))

        // return account1 found
        // return account2 not found
        // return account3 not found
        // return account4 found
        val result1 = await(accountsDAO.find(Option(h), None, 0, 1, sessionId))
        assert(result1.size == 1)
        assert(result1(0).id == accountId1)

        val result2 = await(accountsDAO.find(Option(h), result1.lastOption.map(_.next), 0, 1, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == accountId3)


      }
    }
  }


}

