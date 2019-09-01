package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.UserStatusType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId

class UsersDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should create user") {
      forAll(userGen) { a =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val result = await(usersDAO.find(sessionId))
        assert(result.exists(_.userName == a.userName))
        assert(result.exists(_.userStatus == UserStatusType.normally))
      }
    }

    scenario("should create an user and update display name") {
      forAll(userGen) { a =>
        val sessionId = await(usersDAO.create(a.userName, a.displayName)).sessionId
        val result = await(usersDAO.find(sessionId))
        assert(result.exists(_.userName == a.userName))
        assert(result.exists(_.displayName == a.displayName))
        assert(result.exists(_.userStatus == UserStatusType.normally))
      }
    }

    scenario("should return exception if duplicate user name") {
      forOne(userGen) {
        a1 =>
          await(usersDAO.create(a1.userName)).sessionId
          // exception occurs
          assert(intercept[ServerError] {
            await(usersDAO.create(a1.userName))
          }.code == 1062)
      }
    }

  }


  feature("updateProfile") {
    scenario("should update user profile") {
      forAll(userGen) { a =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        await(usersDAO.updateProfile(
          a.displayName,
          a.web,
          a.birthday,
          a.location,
          a.bio,
          sessionId))
        val result = await(usersDAO.find(sessionId))
        assert(result.map(_.userName) == Option(a.userName))
        assert(result.map(_.displayName) == Option(a.displayName))
        assert(result.map(_.web) == Option(a.web))
        assert(result.map(_.birthday) == Option(a.birthday))
        assert(result.map(_.location) == Option(a.location))
        assert(result.map(_.bio) == Option(a.bio))
      }
    }
  }

  feature("updateProfileImageUrl") {
    scenario("should update user profile image") {
      forAll(userGen, mediumGen) { (a, m) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val mediumId = await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))
        await(usersDAO.updateProfileImageUrl(Option(m.uri), Option(mediumId), sessionId))
        val result1 = await(usersDAO.find(sessionId))
        assert(result1.flatMap(_.profileImageUrl) == Option(m.uri))
        await(usersDAO.updateProfileImageUrl(None, None, sessionId))
        val result2 = await(usersDAO.find(sessionId))
        assert(result2.flatMap(_.profileImageUrl).isEmpty)
      }
    }
  }

  feature("updateUserName") {
    scenario("should update user name") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        await(usersDAO.create(a3.userName))
        val result1 = await(usersDAO.find(sessionId))
        assert(result1.map(_.userName) == Option(a1.userName))
        assert(result1.map(_.displayName) == Option(a1.userName))
        await(usersDAO.updateUserName(a2.userName, sessionId))
        val result2 = await(usersDAO.find(sessionId))
        assert(result2.map(_.userName) == Option(a2.userName))
      }
    }

    scenario("should return exception if duplicate user name") {
      forOne(userGen, userGen) { (a1, a2) =>
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        await(usersDAO.create(a2.userName))
        // exception occurs
        assert(intercept[ServerError] {
          await(usersDAO.updateUserName(a2.userName, sessionId))
        }.code == 1062)
      }
    }
  }

  feature("updateUserStatus") {
    scenario("should update user status") {
      forAll(userGen) { a =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        await(usersDAO.updateUserStatus(a.userStatus, sessionId))
        val result = await(usersDAO.find(sessionId))
        assert(result.exists(_.userStatus == a.userStatus))
      }
    }
  }



  feature("updateDisplayName") {
    scenario("should update display name") {
      forAll(userGen, userGen, userGen) { (a1, a2, d) =>
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        await(usersDAO.updateDisplayName(userId, Option(d.displayName), sessionId))
        val result1 = await(usersDAO.find(userId, sessionId))
        assert(result1.map(_.displayName) == Option(d.displayName))
        await(usersDAO.updateDisplayName(userId, None, sessionId))
        val result2 = await(usersDAO.find(userId, sessionId))
        assert(result2.map(_.displayName) == Option(a2.userName))
      }
    }
  }


  feature("exist - by user name") {
    scenario("should return user name exist or not") {
      forAll(userGen, userGen) { (a1, a2) =>
        await(usersDAO.create(a1.userName))
        assert(await(usersDAO.exists(a1.userName)))
        assert(!await(usersDAO.exists(a2.userName)))
      }
    }
    scenario("should return user name exist or not ignore session user") {
      forAll(userGen, userGen) { (a1, a2) =>
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        assert(!await(usersDAO.exists(a1.userName, sessionId)))
        assert(!await(usersDAO.exists(a2.userName, sessionId)))
      }
    }
  }


  feature("exist - by user id") {
    scenario("should return user exist or not") {
      forAll(userGen) { (a) =>
        val userId = await(usersDAO.create(a.userName))
        assert(await(usersDAO.exists(userId)))
        assert(!await(usersDAO.exists(UserId(0L))))
      }
    }
    scenario("should return user id not exist when user is blocked") {
      forAll(userGen, userGen, userGen, userGen) { (a1, a2, a3, a4) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        await(blocksDAO.create(userId3, userId1.sessionId))
        await(blocksDAO.create(userId1, userId2.sessionId))
        await(blocksDAO.create(userId2, userId3.sessionId))
        assert(!await(usersDAO.exists(userId2, userId1.sessionId)))
        assert(await(usersDAO.exists(userId3, userId1.sessionId)))
        assert(await(usersDAO.exists(userId1, userId2.sessionId)))
        assert(!await(usersDAO.exists(userId3, userId2.sessionId)))
        assert(!await(usersDAO.exists(userId1, userId3.sessionId)))
        assert(await(usersDAO.exists(userId2, userId3.sessionId)))
        assert(await(usersDAO.exists(userId1, userId4.sessionId)))
        assert(await(usersDAO.exists(userId2, userId4.sessionId)))
        assert(await(usersDAO.exists(userId3, userId4.sessionId)))
      }
    }
  }


  feature("exist - by user ids") {
    scenario("should return all user exist or not") {
      forAll(userGen, userGen, userGen, userGen) { (a1, a2, a3, a4) =>
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        await(blocksDAO.create(userId3, sessionId))
        await(blocksDAO.create(sessionId.userId, userId4.sessionId))
        assert(await(usersDAO.exists(List(userId2, userId3), sessionId)))
        assert(!await(usersDAO.exists(List(userId2, userId4), sessionId)))
        assert(await(usersDAO.exists(List(userId2), sessionId)))
      }
    }
  }

  feature("signOut") {
    scenario("should sign out") {
      forAll(userGen) { a =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val result1 = await(usersDAO.find(sessionId))
        assert(result1.flatMap(_.signedOutAt).isEmpty)
        await(usersDAO.signOut(sessionId))
        val result2 = await(usersDAO.find(sessionId))
        assert(result2.flatMap(_.signedOutAt).isDefined)
      }
    }
  }

  feature("find an user") {
    scenario("should return an user") {
      forAll(userGen, userGen, userGen, userGen) { (s, a1, a2, a3) =>

        // preparing
        //  user2 block session user
        //  session user block user3
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(blocksDAO.create(sessionId.userId, userId2.sessionId))
        await(blocksDAO.create(userId3, sessionId))

        // return user1 found
        // return user2 not found
        // return user3 found
        val result1 = await(usersDAO.find(userId1, sessionId))
        val result2 = await(usersDAO.find(userId2, sessionId))
        val result3 = await(usersDAO.find(userId3, sessionId))
        assert(result1.isDefined)
        assert(result2.isEmpty)
        assert(result3.isDefined)

      }
    }

  }

  feature("find an authentication") {
    scenario("should return an user") {
      forAll(userGen, authenticationGen, authenticationGen) { (s, a1, a2) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        await(authenticationsDAO.create(a1.providerId, a1.providerKey, a1.password, a1.hasher))
        await(authenticationsDAO.updateUserId(a1.providerId, a1.providerKey, sessionId))
        val result1 = await(usersDAO.find(a1.providerId, a1.providerKey))
        val result2 = await(usersDAO.find(a2.providerId, a2.providerKey))
        assert(result1.isDefined)
        assert(result2.isEmpty)
      }
    }
  }

  feature("find user list") {
    scenario("should return user list") {
      forAll(sortedNameGen, sortedUserGen, sortedUserGen, sortedUserGen, sortedUserGen, sortedUserGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //  user2 block session user
        //  session user block user3
        val sessionId = await(usersDAO.create(h + s.userName)).sessionId
        val userId1 = await(usersDAO.create(h + a1.userName))
        val userId2 = await(usersDAO.create(h + a2.userName))
        val userId3 = await(usersDAO.create(h + a3.userName))
        await(usersDAO.create(h + a4.userName))
        await(blocksDAO.create(sessionId.userId, userId2.sessionId))
        await(blocksDAO.create(userId3, sessionId))

        // return user1 found
        // return user2 not found
        // return user3 not found
        // return user4 found
        val result1 = await(usersDAO.find(Option(h), None, 0, 1, sessionId))
        assert(result1.size == 1)
        assert(result1(0).id == userId1)

        val result2 = await(usersDAO.find(Option(h), result1.lastOption.map(_.next), 0, 1, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == userId3)


      }
    }
  }


}

