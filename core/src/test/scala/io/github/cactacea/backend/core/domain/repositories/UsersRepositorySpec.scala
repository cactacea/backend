package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.{ActiveStatusType, UserStatusType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId, UserId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class UsersRepositorySpec extends RepositorySpec {

  feature("create") {
    scenario("should create an user") {
      forAll(userGen, userAuthenticationGen) { (u, a) =>
        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, u.userName, None)).sessionId
        val result = await(usersRepository.find(sessionId))
        assert(result.userName == u.userName)
        assert(result.displayName == u.userName)
        assert(result.feedCount == 0)
        assert(result.followCount == 0)
        assert(result.followerCount == 0)
        assert(result.friendCount == 0)
        assert(!result.isFollower)
        assert(!result.isFriend)
        assert(!result.blocked)
        assert(!result.muted)
        assert(!result.follow)
        assert(!result.friendRequestInProgress)
        assert(result.bio.isEmpty)
        assert(result.birthday.isEmpty)
        assert(result.location.isEmpty)
        assert(result.profileImageUrl.isEmpty)
        assert(result.userStatus == UserStatusType.normally)

        val result2 = await(pushNotificationSettingsRepository.find(sessionId))
        assert(result2.feed)
        assert(result2.comment)
        assert(result2.friendRequest)
        assert(result2.message)
        assert(result2.channelMessage)
        assert(result2.invitation)
        assert(result2.showMessage)

      }
    }
    scenario("should return exception if user name is already exist") {
      forOne(userGen, userAuthenticationGen) { (u, a) =>
        await(usersRepository.create(a.providerId, a.providerKey, u.userName, None))
        assert(intercept[CactaceaException] {
          await(usersRepository.create(a.providerId, a.providerKey, u.userName, None))
        }.error == UserAlreadyExist)
      }
    }
  }



  feature("create and update display name") {
    scenario("should create an user") {
      forOne(userGen, userAuthenticationGen) { (u, a) =>
        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, u.userName, Option(u.displayName))).sessionId
        val result = await(usersRepository.find(sessionId))
        assert(result.userName == u.userName)
        assert(result.displayName == u.displayName)
        assert(result.feedCount == 0)
        assert(result.followCount == 0)
        assert(result.followerCount == 0)
        assert(result.friendCount == 0)
        assert(!result.isFollower)
        assert(!result.isFriend)
        assert(!result.blocked)
        assert(!result.muted)
        assert(!result.follow)
        assert(!result.friendRequestInProgress)
        assert(result.bio.isEmpty)
        assert(result.birthday.isEmpty)
        assert(result.location.isEmpty)
        assert(result.userStatus == UserStatusType.normally)
      }
    }

    scenario("should return exception if user name is already exist") {
      forOne(userGen, userAuthenticationGen) { (u, a) =>
        await(usersRepository.create(a.providerId, a.providerKey, u.userName, Option(u.displayName)))
        assert(intercept[CactaceaException] {
          await(usersRepository.create(a.providerId, a.providerKey, u.userName, Option(u.displayName)))
        }.error == UserAlreadyExist)
      }
    }
  }

  feature("find(session id)") {
    scenario("should return user") {
      forOne(userGen, userGen, userGen, userGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        val sessionId = await(usersRepository.create("credentials", s.userName, s.userName, Option(s.displayName))).sessionId
        val followId = await(usersRepository.create("credentials", a1.userName, a1.userName, None))
        val followerId = await(usersRepository.create("credentials", a2.userName, a2.userName, None))
        val friendId = await(usersRepository.create("credentials", a3.userName, a3.userName, None))
        await(usersRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))
        await(followsRepository.create(followId, sessionId))
        await(followsRepository.create(sessionId.userId, followerId.sessionId))
        val requestId = await(friendRequestsRepository.create(friendId, sessionId))
        await(friendRequestsRepository.accept(requestId, friendId.sessionId))
        await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result = await(usersRepository.find(sessionId))
        assert(result.userName == s.userName)
        assert(result.displayName == s.displayName)
        assert(result.bio == s.bio)
        assert(result.birthday == s.birthday)
        assert(result.location == s.location)
        assert(result.web == s.web)
        assert(result.feedCount == 1)
        assert(result.followCount == 1)
        assert(result.followerCount == 1)
        assert(result.friendCount == 1)
        assert(!result.isFollower)
        assert(!result.isFriend)
        assert(!result.blocked)
        assert(!result.muted)
        assert(!result.follow)
        assert(!result.friendRequestInProgress)
        assert(result.userStatus == UserStatusType.normally)

      }

    }

    scenario("should return exception if user is not exist"){
      assert(intercept[CactaceaException] {
        await(usersRepository.find(SessionId(0L)))
      }.error == SessionNotAuthorized)
    }

  }

  feature("find(user id, session id)") {

    scenario("should return user by follow user") {
      forOne(userGen, userGen, userGen, userGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        val sessionId = await(usersRepository.create("credentials", s.userName, s.userName, Option(s.displayName))).sessionId
        val followId = await(usersRepository.create("credentials", a1.userName, a1.userName, None))
        val followerId = await(usersRepository.create("credentials", a2.userName, a2.userName, None))
        val friendId = await(usersRepository.create("credentials", a3.userName, a3.userName, None))
        await(usersRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))
        await(followsRepository.create(followId, sessionId))
        await(followsRepository.create(sessionId.userId, followerId.sessionId))
        val requestId = await(friendRequestsRepository.create(friendId, sessionId))
        await(friendRequestsRepository.accept(requestId, friendId.sessionId))
        await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result = await(usersRepository.find(sessionId.userId, followId.sessionId))
        assert(result.userName == s.userName)
        assert(result.displayName == s.displayName)
        assert(result.bio == s.bio)
        assert(result.birthday == s.birthday)
        assert(result.location == s.location)
        assert(result.web == s.web)
        assert(result.feedCount == 1)
        assert(result.followCount == 1)
        assert(result.followerCount == 1)
        assert(result.friendCount == 1)
        assert(result.isFollower)
        assert(!result.isFriend)
        assert(!result.blocked)
        assert(!result.muted)
        assert(!result.follow)
        assert(!result.friendRequestInProgress)
        assert(result.userStatus == UserStatusType.normally)

      }
    }

    scenario("should return user by follower user") {
      forOne(userGen, userGen, userGen, userGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        val sessionId = await(usersRepository.create("credentials", s.userName, s.userName, Option(s.displayName))).sessionId
        val followId = await(usersRepository.create("credentials", a1.userName, a1.userName, None))
        val followerId = await(usersRepository.create("credentials", a2.userName, a2.userName, None))
        val friendId = await(usersRepository.create("credentials", a3.userName, a3.userName, None))
        await(usersRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))
        await(followsRepository.create(followId, sessionId))
        await(followsRepository.create(sessionId.userId, followerId.sessionId))
        val requestId = await(friendRequestsRepository.create(friendId, sessionId))
        await(friendRequestsRepository.accept(requestId, friendId.sessionId))
        await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result = await(usersRepository.find(sessionId.userId, followerId.sessionId))
        assert(result.userName == s.userName)
        assert(result.displayName == s.displayName)
        assert(result.bio == s.bio)
        assert(result.birthday == s.birthday)
        assert(result.location == s.location)
        assert(result.web == s.web)
        assert(result.feedCount == 1)
        assert(result.followCount == 1)
        assert(result.followerCount == 1)
        assert(result.friendCount == 1)
        assert(!result.isFollower)
        assert(!result.isFriend)
        assert(!result.blocked)
        assert(!result.muted)
        assert(result.follow)
        assert(!result.friendRequestInProgress)
        assert(result.userStatus == UserStatusType.normally)

      }
    }

    scenario("should return user by follower and mute user") {
      forOne(userGen, userGen, userGen, userGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        val sessionId = await(usersRepository.create("credentails", s.userName, s.userName, Option(s.displayName))).sessionId
        val followId = await(usersRepository.create("credentials", a1.userName, a1.userName, None))
        val followerId = await(usersRepository.create("credentials", a2.userName, a2.userName, None))
        val friendId = await(usersRepository.create("credentials", a3.userName, a3.userName, None))
        await(usersRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))
        await(followsRepository.create(followId, sessionId))
        await(followsRepository.create(sessionId.userId, followerId.sessionId))
        val requestId = await(friendRequestsRepository.create(friendId, sessionId))
        await(friendRequestsRepository.accept(requestId, friendId.sessionId))
        await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(mutesRepository.create(sessionId.userId, followerId.sessionId))

        // result
        val result = await(usersRepository.find(sessionId.userId, followerId.sessionId))
        assert(result.userName == s.userName)
        assert(result.displayName == s.displayName)
        assert(result.bio == s.bio)
        assert(result.birthday == s.birthday)
        assert(result.location == s.location)
        assert(result.web == s.web)
        assert(result.feedCount == 1)
        assert(result.followCount == 1)
        assert(result.followerCount == 1)
        assert(result.friendCount == 1)
        assert(!result.isFollower)
        assert(!result.isFriend)
        assert(!result.blocked)
        assert(result.muted)
        assert(result.follow)
        assert(!result.friendRequestInProgress)
        assert(result.userStatus == UserStatusType.normally)

      }
    }

    scenario("should return exception if user is not exist") {
      forOne(userGen, userAuthenticationGen) { (s, a) =>
        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, s.userName, Option(s.displayName))).sessionId
        assert(intercept[CactaceaException] {
          await(usersRepository.find(UserId(0L), sessionId))
        }.error == UserNotFound)
      }
    }

  }

  feature("updateUserStatus") {
    scenario("should update user status") {
      forAll(userGen, userAuthenticationGen, userStatusGen) { (s, a, t) =>

        // preparing
        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, s.userName, Option(s.displayName))).sessionId
        await(usersRepository.updateUserStatus(t, sessionId))

        // result
        val result = await(usersRepository.find(sessionId))
        assert(result.userStatus == t)

      }
    }
  }




//  feature("find(provider id, provider key)") {
//    scenario("should return user") {
//      forOne(userGen, authenticationGen) { (s, u1) =>
//
//        // preparing
//        val sessionId = await(usersRepository.create(s.userName)).sessionId
//        await(userAuthenticationsDAO.create(sessionId.userId, u1.providerId, u1.providerKey))
////        await(userAuthenticationsDAO.updateUserId(u1.providerId, u1.providerKey, sessionId))
//
//        // result
//        val result = await(usersRepository.find(u1.providerId, u1.providerKey))
//        assert(result.userName == s.userName)
//        assert(result.displayName == s.userName)
//        assert(result.feedCount == 0)
//        assert(result.followCount == 0)
//        assert(result.followerCount == 0)
//        assert(result.friendCount == 0)
//        assert(!result.isFollower)
//        assert(!result.isFriend)
//        assert(!result.blocked)
//        assert(!result.muted)
//        assert(!result.follow)
//        assert(!result.friendRequestInProgress)
//        assert(result.bio.isEmpty)
//        assert(result.birthday.isEmpty)
//        assert(result.location.isEmpty)
//        assert(result.profileImageUrl.isEmpty)
//        assert(result.userStatus == UserStatusType.normally)
//
//
//      }
//    }
//
//    scenario("should return exception if user is not exist") {
//      forOne(authenticationGen) { (u1) =>
//
//        // result
//        assert(intercept[CactaceaException] {
//          await(usersRepository.find(u1.providerId, u1.providerKey))
//        }.error == UserNotFound)
//
//      }
//    }
//
//    scenario("should return exception if user is terminated") {
//      forOne(userGen, authenticationGen) { (s, u1) =>
//
//        // preparing
//        val sessionId = await(usersRepository.create(s.userName)).sessionId
//        await(userAuthenticationsDAO.create(sessionId.userId, u1.providerId, u1.providerKey))
////        await(userAuthenticationsDAO.updateUserId(u1.providerId, u1.providerKey, sessionId))
//        await(usersDAO.updateUserStatus(UserStatusType.terminated, sessionId))
//
//        // result
//        assert(intercept[CactaceaException] {
//          await(usersRepository.find(u1.providerId, u1.providerKey))
//        }.error == UserTerminated)
//
//      }
//    }
//
//    scenario("should return exception if user is deleted") {
//      forOne(userGen, authenticationGen) { (s, u1) =>
//
//        // preparing
//        val sessionId = await(usersRepository.create(s.userName)).sessionId
//        await(userAuthenticationsDAO.create(sessionId.userId, u1.providerId, u1.providerKey))
////        await(userAuthenticationsDAO.updateUserId(u1.providerId, u1.providerKey, sessionId))
//        await(usersDAO.updateUserStatus(UserStatusType.deleted, sessionId))
//
//        // result
//        assert(intercept[CactaceaException] {
//          await(usersRepository.find(u1.providerId, u1.providerKey))
//        }.error == UserDeleted)
//
//      }
//    }
//
//  }

  feature("findActiveStatus") {

    scenario("should return user active status") {
      forOne(userGen, userGen, userAuthenticationGen, userAuthenticationGen, deviceGen, deviceGen) { (s, u1, a1, a2, d1, d2) =>

        val sessionId = await(usersRepository.create(a1.providerId, a1.providerKey, s.userName, Option(s.displayName))).sessionId
        val userId = await(usersRepository.create(a2.providerId, a2.providerKey, u1.userName, Option(u1.displayName)))
        assertFutureValue(usersRepository.findActiveStatus(userId, sessionId).map(_.status), ActiveStatusType.inactive)
        await(devicesRepository.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, userId.sessionId))
        await(devicesRepository.create(d2.udid, d2.pushToken, d2.deviceType, d2.userAgent, userId.sessionId))
        assertFutureValue(usersRepository.findActiveStatus(userId, sessionId).map(_.status), ActiveStatusType.active)
        await(usersRepository.signOut(d1.udid, userId.sessionId))
        assertFutureValue(usersRepository.findActiveStatus(userId, sessionId).map(_.status), ActiveStatusType.active)
        await(usersRepository.signOut(d2.udid, userId.sessionId))
        assertFutureValue(usersRepository.findActiveStatus(userId, sessionId).map(_.status), ActiveStatusType.inactive)
      }
    }

    scenario("should return exception if an user not exist") {
      forOne(userGen, userAuthenticationGen) { (s, a) =>

        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, s.userName, None)).sessionId
        assert(intercept[CactaceaException] {
          await(usersRepository.findActiveStatus(UserId(-1), sessionId))
        }.error == UserNotFound)

      }
    }
  }





  feature("isRegistered") {
    scenario("should return user name already registered or not") {
      forOne(userGen, userAuthenticationGen) { (s, a) =>
        assertFutureValue(usersRepository.isRegistered(s.userName), false)
        await(usersRepository.create(a.providerId, a.providerKey, s.userName, Option(s.displayName))).sessionId
        assertFutureValue(usersRepository.isRegistered(s.userName), true)
      }

    }

  }

//  feature("find") {
//    scenario("should return session user") {
//      forOne(userGen) { (s) =>
//
//        // preparing
//        val sessionId = await(usersRepository.create(s.userName, Option(s.displayName)))
//        val sessionId = session.sessionId
//
//        val result = await(usersRepository.find(sessionId, DateTime.now().getMillis))
//        assert(result.id == sessionId.userId)
//
//      }
//    }
//
//    scenario("should return exception if expired") {
//      forOne(userGen, deviceGen) { (s, d1) =>
//
//        // preparing
//        val sessionId = await(usersRepository.create(s.userName, Option(s.displayName)))
//        val sessionId = session.sessionId
//        await(devicesRepository.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
//        await(usersRepository.signOut(d1.udid, sessionId))
//
//        assert(intercept[CactaceaException] {
//          await(usersRepository.find(sessionId, System.currentTimeMillis()))
//        }.error == SessionTimeout)
//
//      }
//    }
//
//    scenario("should return exception if user is terminated") {
//      forOne(userGen, authenticationGen) { (s, u1) =>
//
//        // preparing
//        val sessionId = await(usersRepository.create(s.userName)).sessionId
//        await(userAuthenticationsDAO.create(sessionId.userId, u1.providerId, u1.providerKey))
////        await(userAuthenticationsDAO.updateUserId(u1.providerId, u1.providerKey, sessionId))
//        await(usersDAO.updateUserStatus(UserStatusType.terminated, sessionId))
//
//        // result
//        assert(intercept[CactaceaException] {
//          await(usersRepository.find(sessionId, System.currentTimeMillis()))
//        }.error == UserTerminated)
//
//      }
//    }
//
//    scenario("should return exception if user is deleted") {
//      forOne(userGen, authenticationGen) { (s, u1) =>
//
//        // preparing
//        val sessionId = await(usersRepository.create(s.userName)).sessionId
//        await(userAuthenticationsDAO.create(sessionId.userId, u1.providerId, u1.providerKey))
////        await(userAuthenticationsDAO.updateUserId(u1.providerId, u1.providerKey, sessionId))
//        await(usersDAO.updateUserStatus(UserStatusType.deleted, sessionId))
//
//        // result
//        assert(intercept[CactaceaException] {
//          await(usersRepository.find(sessionId, System.currentTimeMillis()))
//        }.error == UserDeleted)
//
//      }
//    }
//  }

  feature("find user list by name") {
    scenario("should return user list") {
      forAll(sortedNameGen, sortedUserGen, sortedUserGen, sortedUserGen, sortedUserGen, sortedUserGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //  user2 block session user
        //  session user block user3
        val sessionId = await(usersRepository.create("credentails", h + s.userName, h + s.userName, None)).sessionId
        val userId1 = await(usersRepository.create("credentails", h + a1.userName, h + a1.userName, None))
        val userId2 = await(usersRepository.create("credentails", h + a2.userName, h + a2.userName, None))
        val userId3 = await(usersRepository.create("credentails", h + a3.userName, h + a3.userName, None))
        await(usersRepository.create("credentails", h + a4.userName, h + a4.userName, None))
        await(blocksRepository.create(sessionId.userId, userId2.sessionId))
        await(blocksRepository.create(userId3, sessionId))

        // return user1 found
        // return user2 not found
        // return user3 not found
        // return user4 found
        val result1 = await(usersRepository.find(Option(h), None, 0, 1, sessionId))
        assert(result1.size == 1)
        assert(result1(0).id == userId1)

        val result2 = await(usersRepository.find(Option(h), result1.lastOption.map(_.next), 0, 1, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == userId3)


      }
    }
  }
  feature("updateDisplayName") {
    scenario("should update display name") {
      forAll(userGen, userGen, userAuthenticationGen, userAuthenticationGen,  uniqueDisplayNameOptGen) { (s, u1, a1, a2, d) =>

        // preparing
        val sessionId = await(usersRepository.create(a1.providerId, a1.providerKey, s.userName, Option(s.displayName))).sessionId
        val userId = await(usersRepository.create(a2.providerId, a2.providerKey, u1.userName, Option(u1.displayName)))

        await(usersRepository.updateDisplayName(userId, d, sessionId))

        // result
        val result = await(usersRepository.find(userId, sessionId))
        assert(result.displayName == d.getOrElse(u1.displayName))
      }
    }

    scenario("should return exception if user is not exist"){
      forAll(userGen, userAuthenticationGen, uniqueDisplayNameOptGen) { (s, a, d) =>

        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, s.userName, Option(s.displayName))).sessionId
        assert(intercept[CactaceaException] {
          await(usersRepository.updateDisplayName(UserId(0L), d, sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if user and session are same."){
      forAll(userGen, userAuthenticationGen, uniqueDisplayNameOptGen) { (s, a, d) =>

        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, s.userName, Option(s.displayName))).sessionId
        assert(intercept[CactaceaException] {
          await(usersRepository.updateDisplayName(sessionId.userId, d, sessionId))
        }.error == InvalidUserIdError)

      }
    }

  }

  feature("updateProfile") {
    scenario("should update profile") {
      forOne(userGen, userAuthenticationGen) { (s, a) =>

        // preparing
        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, s.userName, Option(s.displayName))).sessionId
        await(usersRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))

        // result
        val result = await(usersRepository.find(sessionId))
        assert(result.userName == s.userName)
        assert(result.displayName == s.displayName)
        assert(result.bio == s.bio)
        assert(result.birthday == s.birthday)
        assert(result.location == s.location)
        assert(result.web == s.web)

      }
    }
  }

  feature("updateProfileImage") {
    scenario("should update profile image") {
      forAll(userGen, userAuthenticationGen, mediumOptGen) { (s, a, m) =>
        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, s.userName, Option(s.displayName))).sessionId
        val medium = m.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
        await(usersRepository.updateProfileImage(medium, sessionId))

        val result = await(usersRepository.find(sessionId))
        assert(result.userName == s.userName)
        assert(result.profileImageUrl == m.map(_.uri))
      }
    }

    scenario("should return exception if a medium is not exist") {
      forOne(userGen, userAuthenticationGen) { (s, a) =>
        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, s.userName, Option(s.displayName))).sessionId

        assert(intercept[CactaceaException] {
          await(usersRepository.updateProfileImage(Option(MediumId(0)), sessionId))
        }.error == MediumNotFound)

      }
    }
  }

  feature("report") {

    scenario("should report an user") {
      forAll(userGen, userGen, userAuthenticationGen, userAuthenticationGen, userReportGen) { (u1, u2, a1, a2, r) =>
        val sessionId = await(usersRepository.create(a1.providerId, a1.providerKey, u1.userName, Option(u1.displayName))).sessionId
        val userId = await(usersRepository.create(a2.providerId, a2.providerKey, u2.userName, Option(u2.displayName)))
//        val sessionId = await(usersRepository.create(u1.userName)).sessionId
//        val userId = await(usersRepository.create(u2.userName)).id
        await(usersRepository.report(userId, r.reportType, r.reportContent, sessionId))
        val result = await(findUserReport(userId, sessionId))
        assert(result.exists(_.by == sessionId.userId))
        assert(result.exists(_.userId == userId))
        assert(result.exists(_.reportType == r.reportType))
        assert(result.exists(_.reportContent == r.reportContent))
      }
    }

    scenario("should not return an exception if duplicate") {
      forAll(userGen, userGen, userReportGen) { (a1, a2, r) =>
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        await(userReportsDAO.create(userId, r.reportType, r.reportContent, sessionId))
        await(userReportsDAO.create(userId, r.reportType, r.reportContent, sessionId))
      }
    }


  }

  feature("signOut") {

    scenario("should update last signed in") {
      forOne(userGen, userAuthenticationGen, deviceGen) { (s, a, d1) =>

        // preparing
        val sessionId = await(usersRepository.create(a.providerId, a.providerKey, s.userName, Option(s.displayName))).sessionId
        await(devicesRepository.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
        await(usersRepository.signOut(d1.udid, sessionId))

        val result = await(usersRepository.find(sessionId))
        assert(result.signedOutAt.isDefined)

      }
    }
  }


}
