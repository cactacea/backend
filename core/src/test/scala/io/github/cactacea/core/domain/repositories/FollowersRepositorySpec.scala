package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.RepositorySpec

class FollowersRepositorySpec extends RepositorySpec {

  val followersRepository = injector.instance[FollowersRepository]
  val followsRepository = injector.instance[FollowsRepository]

  test("find a user's followers") {

    val sessionUser = signUp("FollowersRepositorySpec1", "session user password", "session user udid")
    val user = signUp("FollowersRepositorySpec2", "user password", "user udid")
    val followedUser1 = signUp("FollowersRepositorySpec3", "followed1 user password", "followed1 user udid")
    val followedUser2 = signUp("FollowersRepositorySpec4", "followed2 user password", "followed2 user udid")
    val followedUser3 = signUp("FollowersRepositorySpec5", "followed3 user password", "followed3 user udid")
    val followedUser4 = signUp("FollowersRepositorySpec6", "followed4 user password", "followed4 user udid")
    val followedUser5 = signUp("FollowersRepositorySpec7", "followed5 user password", "followed5 user udid")

    execute(followsRepository.create(followedUser1.id, user.id.toSessionId))
    execute(followsRepository.create(followedUser2.id, user.id.toSessionId))
    execute(followsRepository.create(followedUser3.id, user.id.toSessionId))
    execute(followsRepository.create(followedUser4.id, user.id.toSessionId))
    execute(followsRepository.create(followedUser5.id, user.id.toSessionId))

    val follower1 = execute(followersRepository.findAll(user.id, None, None, Some(3), sessionUser.id.toSessionId))
    assert(follower1.size == 3)
    assert(follower1(0).id == followedUser5.id)
    assert(follower1(1).id == followedUser4.id)
    assert(follower1(2).id == followedUser3.id)

    val follower2 = execute(followersRepository.findAll(user.id, Some(follower1(2).next), None, Some(3), sessionUser.id.toSessionId))
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

    execute(followsRepository.create(followedUser1.id, sessionUser.id.toSessionId))
    execute(followsRepository.create(followedUser2.id, sessionUser.id.toSessionId))
    execute(followsRepository.create(followedUser3.id, sessionUser.id.toSessionId))
    execute(followsRepository.create(followedUser4.id, sessionUser.id.toSessionId))
    execute(followsRepository.create(followedUser5.id, sessionUser.id.toSessionId))

    val follower1 = execute(followersRepository.findAll(None, None, Some(3), sessionUser.id.toSessionId))
    assert(follower1.size == 3)
    assert(follower1(0).id == followedUser5.id)
    assert(follower1(1).id == followedUser4.id)
    assert(follower1(2).id == followedUser3.id)

    val follower2 = execute(followersRepository.findAll(Some(follower1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(follower2.size == 2)
    assert(follower2(0).id == followedUser2.id)
    assert(follower2(1).id == followedUser1.id)

  }

}
