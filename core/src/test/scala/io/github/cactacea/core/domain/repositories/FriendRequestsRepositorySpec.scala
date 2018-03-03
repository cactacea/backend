package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.dao.{FriendRequestsDAO, FriendRequestsStatusDAO, FriendsDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FriendRequestId}
import io.github.cactacea.core.util.responses.CactaceaError.{AccountAlreadyRequested, AccountNotFound, CanNotSpecifyMyself, FriendRequestNotFound}
import io.github.cactacea.core.util.exceptions.CactaceaException

class FriendRequestsRepositorySpec extends RepositorySpec {

  val friendRequestsRepository = injector.instance[FriendRequestsRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val friendRequestsStatusDAO = injector.instance[FriendRequestsStatusDAO]
  val friendRequestsDAO = injector.instance[FriendRequestsDAO]
  val friendsDAO = injector.instance[FriendsDAO]

  test("create friend request a user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val friendUser = signUp("friend user name", "friend user password", "friend user udid").account
    Await.result(friendRequestsRepository.create(friendUser.id, sessionUser.id.toSessionId))

    assert(Await.result(friendRequestsDAO.exist(friendUser.id, sessionUser.id.toSessionId)) == true)
    assert(Await.result(friendRequestsStatusDAO.exist(friendUser.id, sessionUser.id.toSessionId)) == true)

  }

  test("create friend request a blocked user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val blockedUser = signUp("blocked user name", "blocked user password", "blocked user udid").account

    Await.result(blocksRepository.create(blockedUser.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(friendRequestsRepository.create(blockedUser.id, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("create friend request a friend user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val friendUser = signUp("friend user name", "friend user password", "friend user udid").account

    Await.result(friendRequestsRepository.create(friendUser.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(friendRequestsRepository.create(friendUser.id, sessionUser.id.toSessionId))
    }.error == AccountAlreadyRequested)

  }

  test("create friend request a session user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(friendRequestsRepository.create(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)

  }



  test("delete friend request") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val friendUser = signUp("friend user name", "friend user password", "friend user udid").account

    Await.result(friendRequestsRepository.create(friendUser.id, sessionUser.id.toSessionId))
    val result = Await.result(friendRequestsRepository.delete(friendUser.id, sessionUser.id.toSessionId))
    // TODO : Check

  }

  test("delete no request friend request ") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val friendUser = signUp("friend user name", "friend user password", "friend user udid").account

    assert(intercept[CactaceaException] {
      Await.result(friendRequestsRepository.delete(friendUser.id, sessionUser.id.toSessionId))
    }.error == FriendRequestNotFound)

  }

  test("delete no exist account friend request ") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(friendRequestsRepository.delete(AccountId(0L), sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("delete friend session user") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(friendRequestsRepository.delete(sessionUser.id, sessionUser.id.toSessionId))
    }.error == CanNotSpecifyMyself)


  }


  test("find session's friend requests") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val requestingUser1 = signUp("user name 1", "user password 1", "user udid 1").account
    val requestingUser2 = signUp("user name 2", "user password 2", "user udid 2").account
    val requestingUser3 = signUp("user name 3", "user password 3", "user udid 3").account
    val requestingUser4 = signUp("user name 4", "user password 4", "user udid 4").account
    val requestingUser5 = signUp("user name 5", "user password 5", "user udid 5").account

    Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser1.id.toSessionId))
    Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser2.id.toSessionId))
    Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser3.id.toSessionId))
    Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser4.id.toSessionId))
    Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser5.id.toSessionId))

    val requests1 = Await.result(friendRequestsRepository.findAll(None, None, Some(3), true, sessionUser.id.toSessionId))
    assert(requests1.size == 3)
    assert(requests1(0).account.id == requestingUser5.id)
    assert(requests1(0).account.id == requestingUser5.id)
    assert(requests1(1).account.id == requestingUser4.id)
    assert(requests1(2).account.id == requestingUser3.id)

    val requests2 = Await.result(friendRequestsRepository.findAll(Some(requests1(2).next), None, Some(3), true, sessionUser.id.toSessionId))
    assert(requests2.size == 2)
    assert(requests2(0).account.id == requestingUser2.id)
    assert(requests2(1).account.id == requestingUser1.id)

  }

  test("accept no exist request") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    assert(intercept[CactaceaException] {
      Await.result(friendRequestsRepository.accept(FriendRequestId(0L), sessionUser.id.toSessionId))
    }.error == FriendRequestNotFound)

  }

  test("reject no exist request") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    assert(intercept[CactaceaException] {
      Await.result(friendRequestsRepository.reject(FriendRequestId(0L), sessionUser.id.toSessionId))
    }.error == FriendRequestNotFound)

  }

  test("accept a friend request") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val requestingUser1 = signUp("user name 1", "user password 1", "user udid 1").account
    val requestingUser2 = signUp("user name 2", "user password 2", "user udid 2").account
    val requestingUser3 = signUp("user name 3", "user password 3", "user udid 3").account
    val requestingUser4 = signUp("user name 4", "user password 4", "user udid 4").account
    val requestingUser5 = signUp("user name 5", "user password 5", "user udid 5").account

    val requestId1 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser1.id.toSessionId))
    val requestId2 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser2.id.toSessionId))
    val requestId3 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser3.id.toSessionId))
    val requestId4 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser4.id.toSessionId))
    val requestId5 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser5.id.toSessionId))

    Await.result(friendRequestsRepository.accept(requestId1, sessionUser.id.toSessionId))
    Await.result(friendRequestsRepository.accept(requestId2, sessionUser.id.toSessionId))
    Await.result(friendRequestsRepository.accept(requestId3, sessionUser.id.toSessionId))
    Await.result(friendRequestsRepository.accept(requestId4, sessionUser.id.toSessionId))
    Await.result(friendRequestsRepository.accept(requestId5, sessionUser.id.toSessionId))

    assert(Await.result(friendsDAO.exist(requestingUser1.id ,sessionUser.id.toSessionId)) == true)
    assert(Await.result(friendsDAO.exist(requestingUser2.id ,sessionUser.id.toSessionId)) == true)
    assert(Await.result(friendsDAO.exist(requestingUser3.id ,sessionUser.id.toSessionId)) == true)
    assert(Await.result(friendsDAO.exist(requestingUser4.id ,sessionUser.id.toSessionId)) == true)
    assert(Await.result(friendsDAO.exist(requestingUser5.id ,sessionUser.id.toSessionId)) == true)

    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser1.id.toSessionId)) == true)
    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser2.id.toSessionId)) == true)
    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser3.id.toSessionId)) == true)
    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser4.id.toSessionId)) == true)
    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser5.id.toSessionId)) == true)

    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser1.id.toSessionId)) == false)
    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser2.id.toSessionId)) == false)
    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser3.id.toSessionId)) == false)
    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser4.id.toSessionId)) == false)
    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser5.id.toSessionId)) == false)

  }

  test("reject a friend request") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val requestingUser1 = signUp("user name 1", "user password 1", "user udid 1").account
    val requestingUser2 = signUp("user name 2", "user password 2", "user udid 2").account
    val requestingUser3 = signUp("user name 3", "user password 3", "user udid 3").account
    val requestingUser4 = signUp("user name 4", "user password 4", "user udid 4").account
    val requestingUser5 = signUp("user name 5", "user password 5", "user udid 5").account

    val requestId1 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser1.id.toSessionId))
    val requestId2 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser2.id.toSessionId))
    val requestId3 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser3.id.toSessionId))
    val requestId4 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser4.id.toSessionId))
    val requestId5 = Await.result(friendRequestsRepository.create(sessionUser.id, requestingUser5.id.toSessionId))

    Await.result(friendRequestsRepository.reject(requestId1, sessionUser.id.toSessionId))
    Await.result(friendRequestsRepository.reject(requestId2, sessionUser.id.toSessionId))
    Await.result(friendRequestsRepository.reject(requestId3, sessionUser.id.toSessionId))
    Await.result(friendRequestsRepository.reject(requestId4, sessionUser.id.toSessionId))
    Await.result(friendRequestsRepository.reject(requestId5, sessionUser.id.toSessionId))

    assert(Await.result(friendsDAO.exist(requestingUser1.id ,sessionUser.id.toSessionId)) == false)
    assert(Await.result(friendsDAO.exist(requestingUser2.id ,sessionUser.id.toSessionId)) == false)
    assert(Await.result(friendsDAO.exist(requestingUser3.id ,sessionUser.id.toSessionId)) == false)
    assert(Await.result(friendsDAO.exist(requestingUser4.id ,sessionUser.id.toSessionId)) == false)
    assert(Await.result(friendsDAO.exist(requestingUser5.id ,sessionUser.id.toSessionId)) == false)

    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser1.id.toSessionId)) == false)
    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser2.id.toSessionId)) == false)
    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser3.id.toSessionId)) == false)
    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser4.id.toSessionId)) == false)
    assert(Await.result(friendsDAO.exist(sessionUser.id ,requestingUser5.id.toSessionId)) == false)

    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser1.id.toSessionId)) == false)
    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser2.id.toSessionId)) == false)
    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser3.id.toSessionId)) == false)
    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser4.id.toSessionId)) == false)
    assert(Await.result(friendRequestsStatusDAO.exist(sessionUser.id ,requestingUser5.id.toSessionId)) == false)

  }

}
