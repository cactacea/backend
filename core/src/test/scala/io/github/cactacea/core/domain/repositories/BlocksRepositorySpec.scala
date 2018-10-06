package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyBlocked, AccountNotBlocked, AccountNotFound, CanNotSpecifyMyself}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class BlocksRepositorySpec extends RepositorySpec {

  val friendRequestsRepository = injector.instance[FriendRequestsRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val followerRepository = injector.instance[FollowsRepository]
  val followersRepository = injector.instance[FollowersRepository]
  val friendsRepository = injector.instance[FriendsRepository]
  val mutesRepository = injector.instance[MutesRepository]
  val blocksDAO = injector.instance[BlocksDAO]
  val followsDAO = injector.instance[FollowsDAO]
  val followersDAO = injector.instance[FollowersDAO]
  val friendsDAO = injector.instance[FriendsDAO]
  val mutesDAO = injector.instance[MutesDAO]
  val friendRequestsDAO = injector.instance[FriendRequestsDAO]

  test("block a user") {

    val sessionUser = signUp("BlocksRepositorySpec1", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec2", "blocked user password", "blocked user udid")

    execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))
    assert(execute(blocksDAO.exist(user.id, sessionUser.id.toSessionId)) == true)

  }

  test("block a follower") {

    val sessionUser = signUp("BlocksRepositorySpec3", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec4", "user password", "user udid")

    execute(followerRepository.create(sessionUser.id, user.id.toSessionId))
    execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))

    val result = execute(blocksRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)
    assert(result(0).id == user.id)

    assert(execute(followsDAO.exist(sessionUser.id, user.id.toSessionId)) == false)

  }

  test("block a follows") {

    val sessionUser = signUp("BlocksRepositorySpec5", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec6", "user password", "user udid")

    execute(followerRepository.create(user.id, sessionUser.id.toSessionId))

    execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))

    val result = execute(blocksRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)
    assert(result(0).id == user.id)

    assert(execute(followsDAO.exist(user.id, sessionUser.id.toSessionId)) == false)

  }

  test("block a friend") {

    val sessionUser = signUp("BlocksRepositorySpec7", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec8", "user password", "user udid")

    execute(friendsRepository.create(user.id, sessionUser.id.toSessionId))

    execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))

    val result = execute(blocksRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)
    assert(result(0).id == user.id)

    assert(execute(friendsDAO.exist(user.id, sessionUser.id.toSessionId)) == false)

  }

  test("block a mute") {

    val sessionUser = signUp("BlocksRepositorySpec9", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec10", "user password", "user udid")

    execute(mutesRepository.create(user.id, sessionUser.id.toSessionId))

    execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))

    val result = execute(blocksRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)
    assert(result(0).id == user.id)

    assert(execute(mutesDAO.exist(user.id, sessionUser.id.toSessionId)) == false)

  }

  test("block a muter") {

    val sessionUser = signUp("BlocksRepositorySpec11", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec12", "user password", "user udid")

    execute(mutesRepository.create(sessionUser.id, user.id.toSessionId))

    execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))

    val result = execute(blocksRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)
    assert(result(0).id == user.id)

    assert(execute(mutesDAO.exist(sessionUser.id, user.id.toSessionId)) == false)

  }

  test("block a friend request user") {

    val sessionUser = signUp("BlocksRepositorySpec13", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec14", "blocked user password", "blocked user udid")

    execute(friendRequestsRepository.create(user.id, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.create(sessionUser.id, user.id.toSessionId))

    execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))

    val result = execute(blocksRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)
    assert(result(0).id == user.id)

    execute(friendRequestsDAO.exist(user.id, sessionUser.id.toSessionId))
    execute(friendRequestsDAO.exist(sessionUser.id, user.id.toSessionId))

  }

  test("block a feed like user") (pending)

  test("unblock a user") {

    val sessionUser = signUp("BlocksRepositorySpec15", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec16", "blocked user password", "blocked user udid")

    execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))
    assert(execute(blocksDAO.exist(user.id, sessionUser.id.toSessionId)) == true)
    execute(blocksRepository.delete(user.id, sessionUser.id.toSessionId))
    assert(execute(blocksDAO.exist(user.id, sessionUser.id.toSessionId)) == false)

  }

  test("unblock a no exist user") {

    val sessionUser = signUp("BlocksRepositorySpec16-2", "session user password", "session udid")

    assert(intercept[CactaceaException] {
      execute(blocksRepository.create(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("unblock a no blocking user") {

    val sessionUser = signUp("BlocksRepositorySpec17", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec18", "blocked user password", "blocked user udid")

    assert(intercept[CactaceaException] {
      execute(blocksRepository.delete(user.id, sessionUser.id.toSessionId))
    }.error == AccountNotBlocked)

  }

  test("find session's blocks") {

    val sessionUser = signUp("BlocksRepositorySpec19", "session user password", "session udid")
    val user1 = signUp("BlocksRepositorySpec20", "blocked user password 1", "blocked user udid 1")
    val user2 = signUp("BlocksRepositorySpec21", "blocked user password 2", "blocked user udid 2")
    val user3 = signUp("BlocksRepositorySpec22", "blocked user password 3", "blocked user udid 3")
    val user4 = signUp("BlocksRepositorySpec23", "blocked user password 4", "blocked user udid 4")
    val user5 = signUp("BlocksRepositorySpec24", "blocked user password 5", "blocked user udid 5")

    execute(blocksRepository.create(user1.id, sessionUser.id.toSessionId))
    execute(blocksRepository.create(user2.id, sessionUser.id.toSessionId))
    execute(blocksRepository.create(user3.id, sessionUser.id.toSessionId))
    execute(blocksRepository.create(user4.id, sessionUser.id.toSessionId))
    execute(blocksRepository.create(user5.id, sessionUser.id.toSessionId))

    val results1 = execute(blocksRepository.findAll(None, None, Some(3), sessionUser.id.toSessionId))
    assert(results1.size == 3)
    assert(results1(0).id == user5.id)
    assert(results1(1).id == user4.id)
    assert(results1(2).id == user3.id)

    val results2 = execute(blocksRepository.findAll(Some(results1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(results2.size == 2)
    assert(results2(0).id == user2.id)
    assert(results2(1).id == user1.id)

  }

  test("block no exist user") {

    val sessionUser = signUp("BlocksRepositorySpec25", "session user password", "session udid")

    assert(intercept[CactaceaException] {
      execute(blocksRepository.create(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("unblock no exist user") {

    val sessionUser = signUp("BlocksRepositorySpec26", "session user password", "session udid")

    assert(intercept[CactaceaException] {
      execute(blocksRepository.delete(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("block blocked user") {

    val sessionUser = signUp("BlocksRepositorySpec27", "session user password", "session udid")
    val user = signUp("BlocksRepositorySpec28", "blocked user password", "blocked user udid")

    execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))
    assert(execute(blocksDAO.exist(user.id, sessionUser.id.toSessionId)) == true)

    assert(intercept[CactaceaException] {
      execute(blocksRepository.create(user.id, sessionUser.id.toSessionId))
    }.error == AccountAlreadyBlocked)

  }

  test("block session user") {

    val sessionUser = signUp("BlocksRepositorySpec29", "session user password", "session udid")

    assert(intercept[CactaceaException] {
      execute(blocksRepository.create(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

  test("delete block session user") {

    val sessionUser = signUp("BlocksRepositorySpec30", "session user password", "session udid")

    assert(intercept[CactaceaException] {
      execute(blocksRepository.delete(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }

}