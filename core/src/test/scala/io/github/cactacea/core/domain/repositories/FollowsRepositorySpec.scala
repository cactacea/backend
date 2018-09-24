package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyFollowed, AccountNotFollowed, AccountNotFound, CanNotSpecifyMyself}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class FollowsRepositorySpec extends RepositorySpec {

  val followerRepository = injector.instance[FollowsRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val accountsRepository = injector.instance[AccountsRepository]

  test("follows a user") {

    val sessionUser = signUp("FollowRepositorySpec1", "session user password", "session user udid")
    val followedUser = signUp("FollowRepositorySpec2", "followed user password", "followed user udid")

    Await.result(followerRepository.create(followedUser.id, sessionUser.id.toSessionId))

    val result = Await.result(followerRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)

    val resultFollowedUser = result(0)
    assert(followedUser.id == resultFollowedUser.id)

    val account1 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account1.followCount == Some(1L))

    val account2 = Await.result(accountsRepository.find(followedUser.id.toSessionId))
    assert(account2.followerCount == Some(0L))

  }

  test("follows a blocked user") {

    val sessionUser = signUp("FollowRepositorySpec3", "session user password", "session user udid")
    val blockingUser = signUp("FollowRepositorySpec4", "blocked user password", "blocked user udid")

    Await.result(blocksRepository.create(sessionUser.id, blockingUser.id.toSessionId))
    val session = signUp("FollowRepositorySpec4-2", "session password", "udid")
    assert(intercept[CactaceaException] {
      Await.result(followerRepository.create(blockingUser.id, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("follows a followed user") {

    val sessionUser = signUp("FollowRepositorySpec5", "session user password", "session user udid")
    val user = signUp("FollowRepositorySpec6", "user password", "user udid")

    Await.result(followerRepository.create(user.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(followerRepository.create(user.id, sessionUser.id.toSessionId))
    }.error == AccountAlreadyFollowed)

  }

  test("follows a session user") {

    val sessionUser = signUp("FollowRepositorySpec7", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      Await.result(followerRepository.create(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

  test("delete follows") {

    val sessionUser = signUp("FollowRepositorySpec8", "session user password", "session user udid")
    val user = signUp("FollowRepositorySpec9", "user password", "user udid")

    Await.result(followerRepository.create(user.id, sessionUser.id.toSessionId))

    val account1 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account1.followCount == Some(1L))

    Await.result(followerRepository.delete(user.id, sessionUser.id.toSessionId))

    val account2 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account2.followCount == Some(0L))

  }

  test("delete follows no followed user") {

    val sessionUser = signUp("FollowRepositorySpec10", "session user password", "session user udid")
    val user = signUp("FollowRepositorySpec11", "user password", "user udid")

    assert(intercept[CactaceaException] {
      Await.result(followerRepository.delete(user.id, sessionUser.id.toSessionId))
    }.error == AccountNotFollowed)

  }

  test("delete no exist account follows") {

    val sessionUser = signUp("FollowRepositorySpec12", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      Await.result(followerRepository.delete(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("delete follows session user") {

    val sessionUser = signUp("FollowRepositorySpec13", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      Await.result(followerRepository.delete(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

  test("find session's follower") {

    val sessionUser = signUp("FollowRepositorySpec14", "session user password", "session user udid")
    val followedUser1 = signUp("FollowRepositorySpec15", "user password 1", "user udid 1")
    val followedUser2 = signUp("FollowRepositorySpec16", "user password 2", "user udid 2")
    val followedUser3 = signUp("FollowRepositorySpec17", "user password 3", "user udid 3")
    val followedUser4 = signUp("FollowRepositorySpec18", "user password 4", "user udid 4")
    val followedUser5 = signUp("FollowRepositorySpec19", "user password 5", "user udid 5")

    Await.result(followerRepository.create(followedUser1.id, sessionUser.id.toSessionId))
    Await.result(followerRepository.create(followedUser2.id, sessionUser.id.toSessionId))
    Await.result(followerRepository.create(followedUser3.id, sessionUser.id.toSessionId))
    Await.result(followerRepository.create(followedUser4.id, sessionUser.id.toSessionId))
    Await.result(followerRepository.create(followedUser5.id, sessionUser.id.toSessionId))

    val follower1 = Await.result(followerRepository.findAll(None, None, Some(3), sessionUser.id.toSessionId))
    assert(follower1.size == 3)
    assert(follower1(0).id == followedUser5.id)
    assert(follower1(1).id == followedUser4.id)
    assert(follower1(2).id == followedUser3.id)

    val follower2 = Await.result(followerRepository.findAll(Some(follower1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(follower2.size == 2)
    assert(follower2(0).id == followedUser2.id)
    assert(follower2(1).id == followedUser1.id)

  }

  test("find a user's follower") {

    val sessionUser = signUp("FollowRepositorySpec20", "session user password", "session user udid")
    val user = signUp("FollowRepositorySpec21", "user password", "user udid")
    val followedUser1 = signUp("FollowRepositorySpec22", "user password 1", "user udid 1")
    val followedUser2 = signUp("FollowRepositorySpec23", "user password 2", "user udid 2")
    val followedUser3 = signUp("FollowRepositorySpec24", "user password 3", "user udid 3")
    val followedUser4 = signUp("FollowRepositorySpec25", "user password 4", "user udid 4")
    val followedUser5 = signUp("FollowRepositorySpec26", "user password 5", "user udid 5")

    Await.result(followerRepository.create(followedUser1.id, user.id.toSessionId))
    Await.result(followerRepository.create(followedUser2.id, user.id.toSessionId))
    Await.result(followerRepository.create(followedUser3.id, user.id.toSessionId))
    Await.result(followerRepository.create(followedUser4.id, user.id.toSessionId))
    Await.result(followerRepository.create(followedUser5.id, user.id.toSessionId))

    val follower1 = Await.result(followerRepository.findAll(user.id, None, None, Some(3), sessionUser.id.toSessionId))
    assert(follower1.size == 3)
    assert(follower1(0).id == followedUser5.id)
    assert(follower1(1).id == followedUser4.id)
    assert(follower1(2).id == followedUser3.id)

    val follower2 = Await.result(followerRepository.findAll(user.id, Some(follower1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(follower2.size == 2)
    assert(follower2(0).id == followedUser2.id)
    assert(follower2(1).id == followedUser1.id)

  }


}