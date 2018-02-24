package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.helpers.SessionRepositoryTest

class FollowersRepositorySpec extends SessionRepositoryTest {

  val followersRepository = injector.instance[FollowersRepository]
  val followsRepository = injector.instance[FollowsRepository]

  test("find a user's followers") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account
    val followedUser1 = signUp("followed1 user name", "followed1 user password", "followed1 user udid").account
    val followedUser2 = signUp("followed2 user name", "followed2 user password", "followed2 user udid").account
    val followedUser3 = signUp("followed3 user name", "followed3 user password", "followed3 user udid").account
    val followedUser4 = signUp("followed4 user name", "followed4 user password", "followed4 user udid").account
    val followedUser5 = signUp("followed5 user name", "followed5 user password", "followed5 user udid").account

    Await.result(followsRepository.create(user.id, followedUser1.id.toSessionId))
    Await.result(followsRepository.create(user.id, followedUser2.id.toSessionId))
    Await.result(followsRepository.create(user.id, followedUser3.id.toSessionId))
    Await.result(followsRepository.create(user.id, followedUser4.id.toSessionId))
    Await.result(followsRepository.create(user.id, followedUser5.id.toSessionId))

    val follows1 = Await.result(followersRepository.findAll(user.id, None, None, Some(3), sessionUser.id.toSessionId))
    assert(follows1.size == 3)
    assert(follows1(0).id == followedUser5.id)
    assert(follows1(1).id == followedUser4.id)
    assert(follows1(2).id == followedUser3.id)

    val follows2 = Await.result(followersRepository.findAll(user.id, Some(follows1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(follows2.size == 2)
    assert(follows2(0).id == followedUser2.id)
    assert(follows2(1).id == followedUser1.id)

  }

  test("find session's followers") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val followedUser1 = signUp("followed1 user name", "followed1 user password", "followed1 user udid").account
    val followedUser2 = signUp("followed2 user name", "followed2 user password", "followed2 user udid").account
    val followedUser3 = signUp("followed3 user name", "followed3 user password", "followed3 user udid").account
    val followedUser4 = signUp("followed4 user name", "followed4 user password", "followed4 user udid").account
    val followedUser5 = signUp("followed5 user name", "followed5 user password", "followed5 user udid").account

    Await.result(followsRepository.create(sessionUser.id, followedUser1.id.toSessionId))
    Await.result(followsRepository.create(sessionUser.id, followedUser2.id.toSessionId))
    Await.result(followsRepository.create(sessionUser.id, followedUser3.id.toSessionId))
    Await.result(followsRepository.create(sessionUser.id, followedUser4.id.toSessionId))
    Await.result(followsRepository.create(sessionUser.id, followedUser5.id.toSessionId))

    val follows1 = Await.result(followersRepository.findAll(None, None, Some(3), sessionUser.id.toSessionId))
    assert(follows1.size == 3)
    assert(follows1(0).id == followedUser5.id)
    assert(follows1(1).id == followedUser4.id)
    assert(follows1(2).id == followedUser3.id)

    val follows2 = Await.result(followersRepository.findAll(Some(follows1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(follows2.size == 2)
    assert(follows2(0).id == followedUser2.id)
    assert(follows2(1).id == followedUser1.id)

  }

}
