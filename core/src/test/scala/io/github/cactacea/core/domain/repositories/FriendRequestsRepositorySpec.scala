package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao.{FriendRequestsDAO, FriendRequestsStatusDAO, FriendsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendRequestId}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyRequested, AccountNotFound, CanNotSpecifyMyself, FriendRequestNotFound}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class FriendRequestsRepositorySpec extends RepositorySpec {

  val friendRequestsRepository = injector.instance[FriendRequestsRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val friendRequestsStatusDAO = injector.instance[FriendRequestsStatusDAO]
  val friendRequestsDAO = injector.instance[FriendRequestsDAO]
  val friendsDAO = injector.instance[FriendsDAO]

  test("create friend request a user") {

    val sessionUser = signUp("FriendRequestsRepositorySpec1", "session user password", "session user udid")
    val friendUser = signUp("FriendRequestsRepositorySpec2", "friend user password", "friend user udid")
    execute(friendRequestsRepository.create(friendUser.id, sessionUser.id.toSessionId))

    assert(execute(friendRequestsDAO.exist(friendUser.id, sessionUser.id.toSessionId)) == true)
    assert(execute(friendRequestsStatusDAO.exist(friendUser.id, sessionUser.id.toSessionId)) == true)

  }

  test("create friend request a blocked user") {

    val sessionUser = signUp("FriendRequestsRepositorySpec3", "session user password", "session user udid")
    val blockingUser = signUp("FriendRequestsRepositorySpec4", "blocked user password", "blocked user udid")

    execute(blocksRepository.create(sessionUser.id, blockingUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(friendRequestsRepository.create(blockingUser.id, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("create friend request a friend user") {

    val sessionUser = signUp("FriendRequestsRepositorySpec5", "session user password", "session user udid")
    val friendUser = signUp("FriendRequestsRepositorySpec6", "friend user password", "friend user udid")

    execute(friendRequestsRepository.create(friendUser.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(friendRequestsRepository.create(friendUser.id, sessionUser.id.toSessionId))
    }.error == AccountAlreadyRequested)

  }

  test("create friend request a session user") {

    val sessionUser = signUp("FriendRequestsRepositorySpec7", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      execute(friendRequestsRepository.create(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }



  test("delete friend request") {

    val sessionUser = signUp("FriendRequestsRepositorySpec8", "session user password", "session user udid")
    val friendUser = signUp("FriendRequestsRepositorySpec9", "friend user password", "friend user udid")

    execute(friendRequestsRepository.create(friendUser.id, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.delete(friendUser.id, sessionUser.id.toSessionId))
    // TODO : Check

  }

  test("delete no request friend request ") {

    val sessionUser = signUp("FriendRequestsRepositorySpec10", "session user password", "session user udid")
    val friendUser = signUp("FriendRequestsRepositorySpec11", "friend user password", "friend user udid")

    assert(intercept[CactaceaException] {
      execute(friendRequestsRepository.delete(friendUser.id, sessionUser.id.toSessionId))
    }.error == FriendRequestNotFound)

  }

  test("delete no exist account friend request ") {

    val sessionUser = signUp("FriendRequestsRepositorySpec12", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      execute(friendRequestsRepository.delete(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("delete friend session user") {

    val sessionUser = signUp("FriendRequestsRepositorySpec13", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      execute(friendRequestsRepository.delete(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)


  }


  test("find session's friend requests") {

    val sessionUser = signUp("FriendRequestsRepositorySpec14", "session user password", "session user udid")
    val requestingUser1 = signUp("FriendRequestsRepositorySpec15", "user password 1", "user udid 1")
    val requestingUser2 = signUp("FriendRequestsRepositorySpec16", "user password 2", "user udid 2")
    val requestingUser3 = signUp("FriendRequestsRepositorySpec17", "user password 3", "user udid 3")
    val requestingUser4 = signUp("FriendRequestsRepositorySpec18", "user password 4", "user udid 4")
    val requestingUser5 = signUp("FriendRequestsRepositorySpec19", "user password 5", "user udid 5")

    val request1 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser1.id.toSessionId))
    val request2 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser2.id.toSessionId))
    val request3 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser3.id.toSessionId))
    val request4 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser4.id.toSessionId))
    val request5 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser5.id.toSessionId))

    val requests1 = execute(friendRequestsRepository.find(None, 0, 3, true, sessionUser.id.toSessionId))
    assert(requests1.size == 3)
    assert(requests1(0).id == request5)
    assert(requests1(1).id == request4)
    assert(requests1(2).id == request3)

    val requests2 = execute(friendRequestsRepository.find(requests1(2).next, 0, 3, true, sessionUser.id.toSessionId))
    assert(requests2.size == 2)
    assert(requests2(0).id == request2)
    assert(requests2(1).id == request1)

  }

  test("accept no exist request") {

    val sessionUser = signUp("FriendRequestsRepositorySpec20", "session user password", "session user udid")
    assert(intercept[CactaceaException] {
      execute(friendRequestsRepository.accept(FriendRequestId(0L), sessionUser.id.toSessionId))
    }.error == FriendRequestNotFound)

  }

  test("reject no exist request") {

    val sessionUser = signUp("FriendRequestsRepositorySpec21", "session user password", "session user udid")
    assert(intercept[CactaceaException] {
      execute(friendRequestsRepository.reject(FriendRequestId(0L), sessionUser.id.toSessionId))
    }.error == FriendRequestNotFound)

  }

  test("accept a friend request") {

    val sessionUser = signUp("FriendRequestsRepositorySpec22", "session user password", "session user udid")
    val requestingUser1 = signUp("FriendRequestsRepositorySpec23", "user password 1", "user udid 1")
    val requestingUser2 = signUp("FriendRequestsRepositorySpec24", "user password 2", "user udid 2")
    val requestingUser3 = signUp("FriendRequestsRepositorySpec25", "user password 3", "user udid 3")
    val requestingUser4 = signUp("FriendRequestsRepositorySpec26", "user password 4", "user udid 4")
    val requestingUser5 = signUp("FriendRequestsRepositorySpec27", "user password 5", "user udid 5")

    val requestId1 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser1.id.toSessionId))
    val requestId2 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser2.id.toSessionId))
    val requestId3 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser3.id.toSessionId))
    val requestId4 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser4.id.toSessionId))
    val requestId5 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser5.id.toSessionId))

    execute(friendRequestsRepository.accept(requestId1, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.accept(requestId2, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.accept(requestId3, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.accept(requestId4, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.accept(requestId5, sessionUser.id.toSessionId))

    assert(execute(friendsDAO.exist(requestingUser1.id ,sessionUser.id.toSessionId)) == true)
    assert(execute(friendsDAO.exist(requestingUser2.id ,sessionUser.id.toSessionId)) == true)
    assert(execute(friendsDAO.exist(requestingUser3.id ,sessionUser.id.toSessionId)) == true)
    assert(execute(friendsDAO.exist(requestingUser4.id ,sessionUser.id.toSessionId)) == true)
    assert(execute(friendsDAO.exist(requestingUser5.id ,sessionUser.id.toSessionId)) == true)

    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser1.id.toSessionId)) == true)
    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser2.id.toSessionId)) == true)
    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser3.id.toSessionId)) == true)
    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser4.id.toSessionId)) == true)
    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser5.id.toSessionId)) == true)

    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser1.id.toSessionId)) == false)
    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser2.id.toSessionId)) == false)
    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser3.id.toSessionId)) == false)
    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser4.id.toSessionId)) == false)
    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser5.id.toSessionId)) == false)

  }

  test("reject a friend request") {

    val sessionUser = signUp("FriendRequestsRepositorySpec28", "session user password", "session user udid")
    val requestingUser1 = signUp("FriendRequestsRepositorySpec29", "user password 1", "user udid 1")
    val requestingUser2 = signUp("FriendRequestsRepositorySpec30", "user password 2", "user udid 2")
    val requestingUser3 = signUp("FriendRequestsRepositorySpec31", "user password 3", "user udid 3")
    val requestingUser4 = signUp("FriendRequestsRepositorySpec32", "user password 4", "user udid 4")
    val requestingUser5 = signUp("FriendRequestsRepositorySpec33", "user password 5", "user udid 5")

    val requestId1 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser1.id.toSessionId))
    val requestId2 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser2.id.toSessionId))
    val requestId3 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser3.id.toSessionId))
    val requestId4 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser4.id.toSessionId))
    val requestId5 = execute(friendRequestsRepository.create(sessionUser.id, requestingUser5.id.toSessionId))

    execute(friendRequestsRepository.reject(requestId1, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.reject(requestId2, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.reject(requestId3, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.reject(requestId4, sessionUser.id.toSessionId))
    execute(friendRequestsRepository.reject(requestId5, sessionUser.id.toSessionId))

    assert(execute(friendsDAO.exist(requestingUser1.id ,sessionUser.id.toSessionId)) == false)
    assert(execute(friendsDAO.exist(requestingUser2.id ,sessionUser.id.toSessionId)) == false)
    assert(execute(friendsDAO.exist(requestingUser3.id ,sessionUser.id.toSessionId)) == false)
    assert(execute(friendsDAO.exist(requestingUser4.id ,sessionUser.id.toSessionId)) == false)
    assert(execute(friendsDAO.exist(requestingUser5.id ,sessionUser.id.toSessionId)) == false)

    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser1.id.toSessionId)) == false)
    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser2.id.toSessionId)) == false)
    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser3.id.toSessionId)) == false)
    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser4.id.toSessionId)) == false)
    assert(execute(friendsDAO.exist(sessionUser.id ,requestingUser5.id.toSessionId)) == false)

    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser1.id.toSessionId)) == false)
    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser2.id.toSessionId)) == false)
    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser3.id.toSessionId)) == false)
    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser4.id.toSessionId)) == false)
    assert(execute(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser5.id.toSessionId)) == false)

  }

}
