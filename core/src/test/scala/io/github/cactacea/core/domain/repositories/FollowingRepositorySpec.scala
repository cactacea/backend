package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.identifiers.AccountId
import io.github.cactacea.core.util.responses.CactaceaError.{AccountAlreadyFollowed, AccountNotFollowed, AccountNotFound, CanNotSpecifyMyself}
import io.github.cactacea.core.util.exceptions.CactaceaException

class FollowingRepositorySpec extends RepositorySpec {

  val followingRepository = injector.instance[FollowingRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val accountsRepository = injector.instance[AccountsRepository]

  test("follow a user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val followedUser = signUp("followed user name", "followed user password", "followed user udid").account
    Await.result(followingRepository.create(followedUser.id, sessionUser.id.toSessionId))
    val result = Await.result(followingRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)
    val resultFollowedUser = result(0)
    assert(followedUser.id == resultFollowedUser.id)

    val account1 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account1.followCount == Some(1L))

    val account2 = Await.result(accountsRepository.find(followedUser.id.toSessionId))
    assert(account2.followerCount == Some(1L))

  }

  test("follow a blocked user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val blockedUser = signUp("blocked user name", "blocked user password", "blocked user udid").account

    Await.result(blocksRepository.create(blockedUser.id, sessionUser.id.toSessionId))
    val session = signUp("session name", "session password", "udid").account
    assert(intercept[CactaceaException] {
      Await.result(followingRepository.create(blockedUser.id, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("follow a followed user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account

    Await.result(followingRepository.create(user.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.create(user.id, sessionUser.id.toSessionId))
    }.error == AccountAlreadyFollowed)

  }

  test("follow a session user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.create(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

  test("delete follow") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account

    Await.result(followingRepository.create(user.id, sessionUser.id.toSessionId))

    val account1 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account1.followCount == Some(1L))

    Await.result(followingRepository.delete(user.id, sessionUser.id.toSessionId))

    val account2 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account2.followCount == Some(0L))

  }

  test("delete follow no followed user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.delete(user.id, sessionUser.id.toSessionId))
    }.error == AccountNotFollowed)

  }

  test("delete no exist account follow") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.delete(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("delete follow session user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(followingRepository.delete(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

  test("find session's following") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val followedUser1 = signUp("user name 1", "user password 1", "user udid 1").account
    val followedUser2 = signUp("user name 2", "user password 2", "user udid 2").account
    val followedUser3 = signUp("user name 3", "user password 3", "user udid 3").account
    val followedUser4 = signUp("user name 4", "user password 4", "user udid 4").account
    val followedUser5 = signUp("user name 5", "user password 5", "user udid 5").account

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

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account
    val followedUser1 = signUp("user name 1", "user password 1", "user udid 1").account
    val followedUser2 = signUp("user name 2", "user password 2", "user udid 2").account
    val followedUser3 = signUp("user name 3", "user password 3", "user udid 3").account
    val followedUser4 = signUp("user name 4", "user password 4", "user udid 4").account
    val followedUser5 = signUp("user name 5", "user password 5", "user udid 5").account

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