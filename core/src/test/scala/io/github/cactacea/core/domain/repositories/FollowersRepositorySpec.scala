package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec

class FollowersRepositorySpec extends RepositorySpec {

  val followersRepository = injector.instance[FollowersRepository]
  val followingRepository = injector.instance[FollowingRepository]

  test("find a user's followers") {

    val sessionUser = signUp("FollowersRepositorySpec1", "session user password", "session user udid")
    val user = signUp("FollowersRepositorySpec2", "user password", "user udid")
    val followedUser1 = signUp("FollowersRepositorySpec3", "followed1 user password", "followed1 user udid")
    val followedUser2 = signUp("FollowersRepositorySpec4", "followed2 user password", "followed2 user udid")
    val followedUser3 = signUp("FollowersRepositorySpec5", "followed3 user password", "followed3 user udid")
    val followedUser4 = signUp("FollowersRepositorySpec6", "followed4 user password", "followed4 user udid")
    val followedUser5 = signUp("FollowersRepositorySpec7", "followed5 user password", "followed5 user udid")

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

    val sessionUser = signUp("FollowersRepositorySpec8", "session user password", "session user udid")
    val followedUser1 = signUp("FollowersRepositorySpec9", "followed1 user password", "followed1 user udid")
    val followedUser2 = signUp("FollowersRepositorySpec10", "followed2 user password", "followed2 user udid")
    val followedUser3 = signUp("FollowersRepositorySpec11", "followed3 user password", "followed3 user udid")
    val followedUser4 = signUp("FollowersRepositorySpec12", "followed4 user password", "followed4 user udid")
    val followedUser5 = signUp("FollowersRepositorySpec13", "followed5 user password", "followed5 user udid")

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
