package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyFollowed, AccountNotFollowed, AccountNotFound, CanNotSpecifyMyself}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class FollowingRepositorySpec extends RepositorySpec {

  val followingRepository = injector.instance[FollowingRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val accountsRepository = injector.instance[AccountsRepository]

  test("follow a user") {

    val sessionUser = signUp("FollowingRepositorySpec1", "session user password", "session user udid")
    val followedUser = signUp("FollowingRepositorySpec2", "followed user password", "followed user udid")

    Await.result(followingRepository.create(followedUser.id, sessionUser.id.toSessionId))

    val result = Await.result(followingRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)

    val resultFollowedUser = result(0)
    assert(followedUser.id == resultFollowedUser.id)

    val account1 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account1.followCount == Some(1L))

    val account2 = Await.result(accountsRepository.find(followedUser.id.toSessionId))
    assert(account2.followerCount == Some(0L))

  }

  test("follow a blocked user") {

    val sessionUser = signUp("FollowingRepositorySpec3", "session user password", "session user udid")
    val blockedUser = signUp("FollowingRepositorySpec4", "blocked user password", "blocked user udid")

    Await.result(blocksRepository.create(blockedUser.id, sessionUser.id.toSessionId))
    val session = signUp("FollowingRepositorySpec4-2", "session password", "udid")
    assert(intercept[CactaceaException] {
      Await.result(followingRepository.create(blockedUser.id, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("follow a followed user") {

    val sessionUser = signUp("FollowingRepositorySpec5", "session user password", "session user udid")
    val user = signUp("FollowingRepositorySpec6", "user password", "user udid")

    Await.result(followingRepository.create(user.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.create(user.id, sessionUser.id.toSessionId))
    }.error == AccountAlreadyFollowed)

  }

  test("follow a session user") {

    val sessionUser = signUp("FollowingRepositorySpec7", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.create(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

  test("delete follow") {

    val sessionUser = signUp("FollowingRepositorySpec8", "session user password", "session user udid")
    val user = signUp("FollowingRepositorySpec9", "user password", "user udid")

    Await.result(followingRepository.create(user.id, sessionUser.id.toSessionId))

    val account1 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account1.followCount == Some(1L))

    Await.result(followingRepository.delete(user.id, sessionUser.id.toSessionId))

    val account2 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account2.followCount == Some(0L))

  }

  test("delete follow no followed user") {

    val sessionUser = signUp("FollowingRepositorySpec10", "session user password", "session user udid")
    val user = signUp("FollowingRepositorySpec11", "user password", "user udid")

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.delete(user.id, sessionUser.id.toSessionId))
    }.error == AccountNotFollowed)

  }

  test("delete no exist account follow") {

    val sessionUser = signUp("FollowingRepositorySpec12", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.delete(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("delete follow session user") {

    val sessionUser = signUp("FollowingRepositorySpec13", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.delete(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

  test("find session's following") {

    val sessionUser = signUp("FollowingRepositorySpec14", "session user password", "session user udid")
    val followedUser1 = signUp("FollowingRepositorySpec15", "user password 1", "user udid 1")
    val followedUser2 = signUp("FollowingRepositorySpec16", "user password 2", "user udid 2")
    val followedUser3 = signUp("FollowingRepositorySpec17", "user password 3", "user udid 3")
    val followedUser4 = signUp("FollowingRepositorySpec18", "user password 4", "user udid 4")
    val followedUser5 = signUp("FollowingRepositorySpec19", "user password 5", "user udid 5")

    Await.result(followingRepository.create(followedUser1.id, sessionUser.id.toSessionId))
    Await.result(followingRepository.create(followedUser2.id, sessionUser.id.toSessionId))
    Await.result(followingRepository.create(followedUser3.id, sessionUser.id.toSessionId))
    Await.result(followingRepository.create(followedUser4.id, sessionUser.id.toSessionId))
    Await.result(followingRepository.create(followedUser5.id, sessionUser.id.toSessionId))

    val following1 = Await.result(followingRepository.findAll(None, None, Some(3), sessionUser.id.toSessionId))
    assert(following1.size == 3)
    assert(following1(0).id == followedUser5.id)
    assert(following1(1).id == followedUser4.id)
    assert(following1(2).id == followedUser3.id)

    val following2 = Await.result(followingRepository.findAll(Some(following1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(following2.size == 2)
    assert(following2(0).id == followedUser2.id)
    assert(following2(1).id == followedUser1.id)

  }

  test("find a user's following") {

    val sessionUser = signUp("FollowingRepositorySpec20", "session user password", "session user udid")
    val user = signUp("FollowingRepositorySpec21", "user password", "user udid")
    val followedUser1 = signUp("FollowingRepositorySpec22", "user password 1", "user udid 1")
    val followedUser2 = signUp("FollowingRepositorySpec23", "user password 2", "user udid 2")
    val followedUser3 = signUp("FollowingRepositorySpec24", "user password 3", "user udid 3")
    val followedUser4 = signUp("FollowingRepositorySpec25", "user password 4", "user udid 4")
    val followedUser5 = signUp("FollowingRepositorySpec26", "user password 5", "user udid 5")

    Await.result(followingRepository.create(followedUser1.id, user.id.toSessionId))
    Await.result(followingRepository.create(followedUser2.id, user.id.toSessionId))
    Await.result(followingRepository.create(followedUser3.id, user.id.toSessionId))
    Await.result(followingRepository.create(followedUser4.id, user.id.toSessionId))
    Await.result(followingRepository.create(followedUser5.id, user.id.toSessionId))

    val following1 = Await.result(followingRepository.findAll(user.id, None, None, Some(3), sessionUser.id.toSessionId))
    assert(following1.size == 3)
    assert(following1(0).id == followedUser5.id)
    assert(following1(1).id == followedUser4.id)
    assert(following1(2).id == followedUser3.id)

    val following2 = Await.result(followingRepository.findAll(user.id, Some(following1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(following2.size == 2)
    assert(following2(0).id == followedUser2.id)
    assert(following2(1).id == followedUser1.id)

  }


}