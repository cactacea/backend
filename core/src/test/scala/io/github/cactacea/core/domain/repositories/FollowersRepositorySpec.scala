package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.helpers.RepositorySpec

class FollowersRepositorySpec extends RepositorySpec {

  val followersRepository = injector.instance[FollowersRepository]
  val followingRepository = injector.instance[FollowingRepository]

  test("find a user's followers") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account
    val followedUser1 = signUp("followed1 user name", "followed1 user password", "followed1 user udid").account
    val followedUser2 = signUp("followed2 user name", "followed2 user password", "followed2 user udid").account
    val followedUser3 = signUp("followed3 user name", "followed3 user password", "followed3 user udid").account
    val followedUser4 = signUp("followed4 user name", "followed4 user password", "followed4 user udid").account
    val followedUser5 = signUp("followed5 user name", "followed5 user password", "followed5 user udid").account

    Await.result(followingRepository.create(user.id, followedUser1.id.toSessionId))
    Await.result(followingRepository.create(user.id, followedUser2.id.toSessionId))
    Await.result(followingRepository.create(user.id, followedUser3.id.toSessionId))
    Await.result(followingRepository.create(user.id, followedUser4.id.toSessionId))
    Await.result(followingRepository.create(user.id, followedUser5.id.toSessionId))

    val following1 = Await.result(followersRepository.findAll(user.id, None, None, Some(3), sessionUser.id.toSessionId))
    assert(following1.size == 3)
    assert(following1(0).id == followedUser5.id)
    assert(following1(1).id == followedUser4.id)
    assert(following1(2).id == followedUser3.id)

    val following2 = Await.result(followersRepository.findAll(user.id, Some(following1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(following2.size == 2)
    assert(following2(0).id == followedUser2.id)
    assert(following2(1).id == followedUser1.id)

  }

  test("find session's followers") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val followedUser1 = signUp("followed1 user name", "followed1 user password", "followed1 user udid").account
    val followedUser2 = signUp("followed2 user name", "followed2 user password", "followed2 user udid").account
    val followedUser3 = signUp("followed3 user name", "followed3 user password", "followed3 user udid").account
    val followedUser4 = signUp("followed4 user name", "followed4 user password", "followed4 user udid").account
    val followedUser5 = signUp("followed5 user name", "followed5 user password", "followed5 user udid").account

    Await.result(followingRepository.create(sessionUser.id, followedUser1.id.toSessionId))
    Await.result(followingRepository.create(sessionUser.id, followedUser2.id.toSessionId))
    Await.result(followingRepository.create(sessionUser.id, followedUser3.id.toSessionId))
    Await.result(followingRepository.create(sessionUser.id, followedUser4.id.toSessionId))
    Await.result(followingRepository.create(sessionUser.id, followedUser5.id.toSessionId))

    val following1 = Await.result(followersRepository.findAll(None, None, Some(3), sessionUser.id.toSessionId))
    assert(following1.size == 3)
    assert(following1(0).id == followedUser5.id)
    assert(following1(1).id == followedUser4.id)
    assert(following1(2).id == followedUser3.id)

    val following2 = Await.result(followersRepository.findAll(Some(following1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(following2.size == 2)
    assert(following2(0).id == followedUser2.id)
    assert(following2(1).id == followedUser1.id)

  }

}
