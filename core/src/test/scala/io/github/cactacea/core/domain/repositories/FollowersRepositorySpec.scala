package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.RepositorySpec

class FollowersRepositorySpec extends RepositorySpec {

  val followersRepository = injector.instance[FollowersRepository]
  val followingsRepository = injector.instance[FollowingsRepository]

  test("find a user's followers") {

    val sessionUser = signUp("FollowersRepositorySpec1", "session user password", "session user udid")
    val user = signUp("FollowersRepositorySpec2", "user password", "user udid")
    val followedUser1 = signUp("FollowersRepositorySpec3", "followed1 user password", "followed1 user udid")
    val followedUser2 = signUp("FollowersRepositorySpec4", "followed2 user password", "followed2 user udid")
    val followedUser3 = signUp("FollowersRepositorySpec5", "followed3 user password", "followed3 user udid")
    val followedUser4 = signUp("FollowersRepositorySpec6", "followed4 user password", "followed4 user udid")
    val followedUser5 = signUp("FollowersRepositorySpec7", "followed5 user password", "followed5 user udid")

    execute(followingsRepository.create(user.id, followedUser1.id.toSessionId))
    execute(followingsRepository.create(user.id, followedUser2.id.toSessionId))
    execute(followingsRepository.create(user.id, followedUser3.id.toSessionId))
    execute(followingsRepository.create(user.id, followedUser4.id.toSessionId))
    execute(followingsRepository.create(user.id, followedUser5.id.toSessionId))

    val follower1 = execute(followersRepository.find(user.id, None, 0, 3, sessionUser.id.toSessionId))
    assert(follower1.size == 3)
    assert(follower1(0).id == followedUser5.id)
    assert(follower1(1).id == followedUser4.id)
    assert(follower1(2).id == followedUser3.id)

    val follower2 = execute(followersRepository.find(user.id, follower1(2).next, 0, 3, sessionUser.id.toSessionId))
    assert(follower2.size == 2)
    assert(follower2(0).id == followedUser2.id)
    assert(follower2(1).id == followedUser1.id)

  }

  test("find session's followers") {

    val sessionUser = signUp("FollowersRepositorySpec8", "session user password", "session user udid")
    val followedUser1 = signUp("FollowersRepositorySpec9", "followed1 user password", "followed1 user udid")
    val followedUser2 = signUp("FollowersRepositorySpec10", "followed2 user password", "followed2 user udid")
    val followedUser3 = signUp("FollowersRepositorySpec11", "followed3 user password", "followed3 user udid")
    val followedUser4 = signUp("FollowersRepositorySpec12", "followed4 user password", "followed4 user udid")
    val followedUser5 = signUp("FollowersRepositorySpec13", "followed5 user password", "followed5 user udid")

    execute(followingsRepository.create(sessionUser.id, followedUser1.id.toSessionId))
    execute(followingsRepository.create(sessionUser.id, followedUser2.id.toSessionId))
    execute(followingsRepository.create(sessionUser.id, followedUser3.id.toSessionId))
    execute(followingsRepository.create(sessionUser.id, followedUser4.id.toSessionId))
    execute(followingsRepository.create(sessionUser.id, followedUser5.id.toSessionId))

    val follower1 = execute(followersRepository.find(None, 0, 3, sessionUser.id.toSessionId))
    assert(follower1.size == 3)
    assert(follower1(0).id == followedUser5.id)
    assert(follower1(1).id == followedUser4.id)
    assert(follower1(2).id == followedUser3.id)

    val follower2 = execute(followersRepository.find(follower1(2).next, 0, 3, sessionUser.id.toSessionId))
    assert(follower2.size == 2)
    assert(follower2(0).id == followedUser2.id)
    assert(follower2(1).id == followedUser1.id)

  }

}
