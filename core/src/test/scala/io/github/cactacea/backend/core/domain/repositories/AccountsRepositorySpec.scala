package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, ActiveStatusType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import org.joda.time.DateTime

class AccountsRepositorySpec extends RepositorySpec {

  feature("create") {
    scenario("should create an account") {
      forAll(accountGen) { (a) =>
        val result = await(accountsRepository.create(a.accountName))
        assert(result.accountName == a.accountName)
        assert(result.displayName == a.accountName)
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
        assert(result.accountStatus == AccountStatusType.normally)

        val result2 = await(pushNotificationSettingsRepository.find(result.id.toSessionId))
        assert(result2.feed)
        assert(result2.comment)
        assert(result2.friendRequest)
        assert(result2.message)
        assert(result2.channelMessage)
        assert(result2.invitation)
        assert(result2.showMessage)

      }
    }
    scenario("should return exception if account name is already exist") {
      forOne(accountGen) { (a) =>
        await(accountsRepository.create(a.accountName))
        assert(intercept[CactaceaException] {
          await(accountsRepository.create(a.accountName))
        }.error == AccountAlreadyExist)
      }
    }
  }



  feature("create and update display name") {
    scenario("should create an account") {
      forOne(accountGen) { (a) =>
        val result = await(accountsRepository.create(a.accountName, Option(a.displayName)))
        assert(result.accountName == a.accountName)
        assert(result.displayName == a.displayName)
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
        assert(result.accountStatus == AccountStatusType.normally)
      }
    }

    scenario("should return exception if account name is already exist") {
      forOne(accountGen) { (a) =>
        await(accountsRepository.create(a.accountName, Option(a.displayName)))
        assert(intercept[CactaceaException] {
          await(accountsRepository.create(a.accountName, Option(a.displayName)))
        }.error == AccountAlreadyExist)
      }
    }
  }

  feature("find(session id)") {
    scenario("should return account") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId
        val follow = await(accountsRepository.create(a1.accountName))
        val follower = await(accountsRepository.create(a2.accountName))
        val friend = await(accountsRepository.create(a3.accountName))
        await(accountsRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))
        await(followsRepository.create(follow.id, sessionId))
        await(followsRepository.create(sessionId.toAccountId, follower.id.toSessionId))
        val requestId = await(friendRequestsRepository.create(friend.id, sessionId))
        await(friendRequestsRepository.accept(requestId, friend.id.toSessionId))
        await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result = await(accountsRepository.find(sessionId))
        assert(result.accountName == s.accountName)
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
        assert(result.accountStatus == AccountStatusType.normally)

      }

    }

    scenario("should return exception if account is not exist"){
      assert(intercept[CactaceaException] {
        await(accountsRepository.find(SessionId(0L)))
      }.error == SessionNotAuthorized)
    }

  }

  feature("find(account id, session id)") {

    scenario("should return account by follow account") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId
        val follow = await(accountsRepository.create(a1.accountName))
        val follower = await(accountsRepository.create(a2.accountName))
        val friend = await(accountsRepository.create(a3.accountName))
        await(accountsRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))
        await(followsRepository.create(follow.id, sessionId))
        await(followsRepository.create(sessionId.toAccountId, follower.id.toSessionId))
        val requestId = await(friendRequestsRepository.create(friend.id, sessionId))
        await(friendRequestsRepository.accept(requestId, friend.id.toSessionId))
        await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result = await(accountsRepository.find(sessionId.toAccountId, follow.id.toSessionId))
        assert(result.accountName == s.accountName)
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
        assert(result.accountStatus == AccountStatusType.normally)

      }
    }

    scenario("should return account by follower account") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId
        val follow = await(accountsRepository.create(a1.accountName))
        val follower = await(accountsRepository.create(a2.accountName))
        val friend = await(accountsRepository.create(a3.accountName))
        await(accountsRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))
        await(followsRepository.create(follow.id, sessionId))
        await(followsRepository.create(sessionId.toAccountId, follower.id.toSessionId))
        val requestId = await(friendRequestsRepository.create(friend.id, sessionId))
        await(friendRequestsRepository.accept(requestId, friend.id.toSessionId))
        await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result = await(accountsRepository.find(sessionId.toAccountId, follower.id.toSessionId))
        assert(result.accountName == s.accountName)
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
        assert(result.accountStatus == AccountStatusType.normally)

      }
    }

    scenario("should return account by follower and mute account") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId
        val follow = await(accountsRepository.create(a1.accountName))
        val follower = await(accountsRepository.create(a2.accountName))
        val friend = await(accountsRepository.create(a3.accountName))
        await(accountsRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))
        await(followsRepository.create(follow.id, sessionId))
        await(followsRepository.create(sessionId.toAccountId, follower.id.toSessionId))
        val requestId = await(friendRequestsRepository.create(friend.id, sessionId))
        await(friendRequestsRepository.accept(requestId, friend.id.toSessionId))
        await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(mutesRepository.create(sessionId.toAccountId, follower.id.toSessionId))

        // result
        val result = await(accountsRepository.find(sessionId.toAccountId, follower.id.toSessionId))
        assert(result.accountName == s.accountName)
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
        assert(result.accountStatus == AccountStatusType.normally)

      }
    }

    scenario("should return exception if account is not exist") {
      forOne(accountGen) { (a) =>
        val session = await(accountsRepository.create(a.accountName, Option(a.displayName)))
        val sessionId = session.id.toSessionId
        assert(intercept[CactaceaException] {
          await(accountsRepository.find(AccountId(0L), sessionId))
        }.error == AccountNotFound)
      }
    }

  }

  feature("updateAccountStatus") {
    scenario("should update account status") {
      forAll(accountGen, accountStatusGen) { (s, a) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        await(accountsRepository.updateAccountStatus(a, sessionId))

        // result
        val result = await(accountsRepository.find(sessionId))
        assert(result.accountStatus == a)

      }
    }
  }




  feature("find(provider id, provider key)") {
    scenario("should return account") {
      forOne(accountGen, authenticationGen) { (s, u1) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.updateAccountId(u1.providerId, u1.providerKey, sessionId))

        // result
        val result = await(accountsRepository.find(u1.providerId, u1.providerKey))
        assert(result.accountName == s.accountName)
        assert(result.displayName == s.accountName)
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
        assert(result.accountStatus == AccountStatusType.normally)


      }
    }

    scenario("should return exception if account is not exist") {
      forOne(authenticationGen) { (u1) =>

        // result
        assert(intercept[CactaceaException] {
          await(accountsRepository.find(u1.providerId, u1.providerKey))
        }.error == AccountNotFound)

      }
    }

    scenario("should return exception if account is terminated") {
      forOne(accountGen, authenticationGen) { (s, u1) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.updateAccountId(u1.providerId, u1.providerKey, sessionId))
        await(accountsDAO.updateAccountStatus(AccountStatusType.terminated, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(accountsRepository.find(u1.providerId, u1.providerKey))
        }.error == AccountTerminated)

      }
    }

    scenario("should return exception if account is deleted") {
      forOne(accountGen, authenticationGen) { (s, u1) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.updateAccountId(u1.providerId, u1.providerKey, sessionId))
        await(accountsDAO.updateAccountStatus(AccountStatusType.deleted, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(accountsRepository.find(u1.providerId, u1.providerKey))
        }.error == AccountTerminated)

      }
    }

  }

  feature("findActiveStatus") {

    scenario("should return account active status") {
      forOne(accountGen, accountGen, deviceGen, deviceGen) { (s, a1, d1, d2) =>

        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a1.accountName)).id
        assertFutureValue(accountsRepository.findActiveStatus(accountId, sessionId).map(_.status), ActiveStatusType.inactive)
        await(devicesRepository.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, accountId.toSessionId))
        await(devicesRepository.create(d2.udid, d2.pushToken, d2.deviceType, d2.userAgent, accountId.toSessionId))
        assertFutureValue(accountsRepository.findActiveStatus(accountId, sessionId).map(_.status), ActiveStatusType.active)
        await(accountsRepository.signOut(d1.udid, accountId.toSessionId))
        assertFutureValue(accountsRepository.findActiveStatus(accountId, sessionId).map(_.status), ActiveStatusType.active)
        await(accountsRepository.signOut(d2.udid, accountId.toSessionId))
        assertFutureValue(accountsRepository.findActiveStatus(accountId, sessionId).map(_.status), ActiveStatusType.inactive)
      }
    }

    scenario("should return exception if an account not exist") {
      forOne(accountGen) { (s) =>

        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        assert(intercept[CactaceaException] {
          await(accountsRepository.findActiveStatus(AccountId(-1), sessionId))
        }.error == AccountNotFound)

      }
    }
  }





  feature("isRegistered") {
    scenario("should return account name already registered or not") {
      forOne(accountGen) { (a) =>
        assertFutureValue(accountsRepository.isRegistered(a.accountName), false)
        await(accountsRepository.create(a.accountName))
        assertFutureValue(accountsRepository.isRegistered(a.accountName), true)
      }

    }

  }

  feature("find") {
    scenario("should return session account") {
      forOne(accountGen) { (s) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId

        val result = await(accountsRepository.find(sessionId, DateTime.now().getMillis))
        assert(result.id == sessionId.toAccountId)

      }
    }

    scenario("should return exception if expired") {
      forOne(accountGen, deviceGen) { (s, d1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId
        await(devicesRepository.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
        await(accountsRepository.signOut(d1.udid, sessionId))

        assert(intercept[CactaceaException] {
          await(accountsRepository.find(sessionId, System.currentTimeMillis()))
        }.error == SessionTimeout)

      }
    }

    scenario("should return exception if account is terminated") {
      forOne(accountGen, authenticationGen) { (s, u1) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.updateAccountId(u1.providerId, u1.providerKey, sessionId))
        await(accountsDAO.updateAccountStatus(AccountStatusType.terminated, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(accountsRepository.find(sessionId, System.currentTimeMillis()))
        }.error == AccountTerminated)

      }
    }

    scenario("should return exception if account is deleted") {
      forOne(accountGen, authenticationGen) { (s, u1) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        await(authenticationsDAO.create(u1.providerId, u1.providerKey, u1.password, u1.hasher))
        await(authenticationsDAO.updateAccountId(u1.providerId, u1.providerKey, sessionId))
        await(accountsDAO.updateAccountStatus(AccountStatusType.deleted, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(accountsRepository.find(sessionId, System.currentTimeMillis()))
        }.error == AccountTerminated)

      }
    }
  }

  feature("find account list by name") {
    scenario("should return account list") {
      forAll(sortedNameGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //  account2 block session account
        //  session account block account3
        val sessionId = await(accountsRepository.create(h + s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(h + a1.accountName)).id
        val accountId2 = await(accountsRepository.create(h + a2.accountName)).id
        val accountId3 = await(accountsRepository.create(h + a3.accountName)).id
        await(accountsRepository.create(h + a4.accountName))
        await(blocksRepository.create(sessionId.toAccountId, accountId2.toSessionId))
        await(blocksRepository.create(accountId3, sessionId))

        // return account1 found
        // return account2 not found
        // return account3 not found
        // return account4 found
        val result1 = await(accountsRepository.find(Option(h), None, 0, 1, sessionId))
        assert(result1.size == 1)
        assert(result1(0).id == accountId1)

        val result2 = await(accountsRepository.find(Option(h), result1.lastOption.map(_.next), 0, 1, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == accountId3)


      }
    }
  }
  feature("updateDisplayName") {
    scenario("should update display name") {
      forAll(accountGen, accountGen, uniqueDisplayNameOptGen) { (s, a1, d) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId
        val account = await(accountsRepository.create(a1.accountName, Option(a1.displayName)))
        await(accountsRepository.updateDisplayName(account.id, d, sessionId))

        // result
        val result = await(accountsRepository.find(account.id, sessionId))
        assert(result.displayName == d.getOrElse(a1.displayName))
      }
    }

    scenario("should return exception if account is not exist"){
      forAll(accountGen, uniqueDisplayNameOptGen) { (s, d) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId

        assert(intercept[CactaceaException] {
          await(accountsRepository.updateDisplayName(AccountId(0L), d, sessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if account and session are same."){
      forAll(accountGen, uniqueDisplayNameOptGen) { (s, d) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId

        assert(intercept[CactaceaException] {
          await(accountsRepository.updateDisplayName(sessionId.toAccountId, d, sessionId))
        }.error == InvalidAccountIdError)

      }
    }

  }

  feature("updateProfile") {
    scenario("should update profile") {
      forOne(accountGen) { (s) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId
        await(accountsRepository.updateProfile(s.displayName, s.web, s.birthday, s.location, s.bio, sessionId))

        // result
        val result = await(accountsRepository.find(sessionId))
        assert(result.accountName == s.accountName)
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
      forAll(accountGen, mediumOptGen) { (a, m) =>
        val session = await(accountsRepository.create(a.accountName))
        val sessionId = session.id.toSessionId
        val medium = m.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
        await(accountsRepository.updateProfileImage(medium, sessionId))

        val result = await(accountsRepository.find(sessionId))
        assert(result.accountName == a.accountName)
        assert(result.profileImageUrl == m.map(_.uri))
      }
    }

    scenario("should return exception if a medium is not exist") {
      forOne(accountGen) { (a) =>
        val session = await(accountsRepository.create(a.accountName))
        val sessionId = session.id.toSessionId

        assert(intercept[CactaceaException] {
          await(accountsRepository.updateProfileImage(Option(MediumId(0)), sessionId))
        }.error == MediumNotFound)

      }
    }
  }

  feature("report") {

    scenario("should report an account") {
      forAll(accountGen, accountGen, accountReportGen) { (a1, a2, r) =>
        val sessionId = await(accountsRepository.create(a1.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a2.accountName)).id
        await(accountsRepository.report(accountId, r.reportType, r.reportContent, sessionId))
        val result = await(findAccountReport(accountId, sessionId))
        assert(result.exists(_.by == sessionId.toAccountId))
        assert(result.exists(_.accountId == accountId))
        assert(result.exists(_.reportType == r.reportType))
        assert(result.exists(_.reportContent == r.reportContent))
      }
    }

    scenario("should not return an exception if duplicate") {
      forAll(accountGen, accountGen, accountReportGen) { (a1, a2, r) =>
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a2.accountName))
        await(accountReportsDAO.create(accountId, r.reportType, r.reportContent, sessionId))
        await(accountReportsDAO.create(accountId, r.reportType, r.reportContent, sessionId))
      }
    }


  }

  feature("signOut") {

    scenario("should update last signed in") {
      forOne(accountGen, deviceGen) { (s, d1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName, Option(s.displayName)))
        val sessionId = session.id.toSessionId
        await(devicesRepository.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
        await(accountsRepository.signOut(d1.udid, sessionId))

        val result = await(accountsRepository.find(sessionId))
        assert(result.signedOutAt.isDefined)

      }
    }
  }


}
