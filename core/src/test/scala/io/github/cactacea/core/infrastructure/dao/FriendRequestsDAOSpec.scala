package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.core.specs.DAOSpec

class FriendRequestsDAOSpec extends DAOSpec {

  val friendRequestsDAO: FriendRequestsDAO = injector.instance[FriendRequestsDAO]

  test("create") {

    val sessionAccount = this.createAccount(0L)
    val requestedAccount1 = this.createAccount(1L)
    val requestedAccount2 = this.createAccount(2L)
    val requestedAccount3 = this.createAccount(3L)
    val requestedAccount4 = this.createAccount(4L)
    val requestedAccount5 = this.createAccount(5L)

    val friendRequestId1 = Await.result(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    val friendRequestId2 = Await.result(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    val friendRequestId3 = Await.result(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    val friendRequestId4 = Await.result(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    val friendRequestId5 = Await.result(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    assert(Await.result(friendRequestsDAO.find(friendRequestId1, requestedAccount1.id.toSessionId)).isDefined == true)
    assert(Await.result(friendRequestsDAO.find(friendRequestId2, requestedAccount2.id.toSessionId)).isDefined == true)
    assert(Await.result(friendRequestsDAO.find(friendRequestId3, requestedAccount3.id.toSessionId)).isDefined == true)
    assert(Await.result(friendRequestsDAO.find(friendRequestId4, requestedAccount4.id.toSessionId)).isDefined == true)
    assert(Await.result(friendRequestsDAO.find(friendRequestId5, requestedAccount5.id.toSessionId)).isDefined == true)

    Await.result(friendRequestsDAO.delete(requestedAccount1.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount2.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount3.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount4.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount5.id, sessionAccount.id.toSessionId))

    val friendRequestId6 = Await.result(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    val friendRequestId7 = Await.result(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    val friendRequestId8 = Await.result(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    val friendRequestId9 = Await.result(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    val friendRequestId10 = Await.result(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    assert(Await.result(friendRequestsDAO.exist(requestedAccount1.id, sessionAccount.id.toSessionId)) == true)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount2.id, sessionAccount.id.toSessionId)) == true)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount3.id, sessionAccount.id.toSessionId)) == true)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount4.id, sessionAccount.id.toSessionId)) == true)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount5.id, sessionAccount.id.toSessionId)) == true)

  }

  test("delete") {

    val sessionAccount = this.createAccount(0L)
    val requestedAccount1 = this.createAccount(1L)
    val requestedAccount2 = this.createAccount(2L)
    val requestedAccount3 = this.createAccount(3L)
    val requestedAccount4 = this.createAccount(4L)
    val requestedAccount5 = this.createAccount(5L)

    val friendRequestId1 = Await.result(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    val friendRequestId2 = Await.result(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    val friendRequestId3 = Await.result(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    val friendRequestId4 = Await.result(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    val friendRequestId5 = Await.result(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    // delete requests
    Await.result(friendRequestsDAO.delete(requestedAccount1.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount2.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount3.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount4.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount5.id, sessionAccount.id.toSessionId))
    assert(Await.result(friendRequestsDAO.exist(requestedAccount1.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount2.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount3.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount4.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount5.id, sessionAccount.id.toSessionId)) == false)

  }

  test("exist") {

    val sessionAccount = this.createAccount(0L)
    val requestedAccount1 = this.createAccount(1L)
    val requestedAccount2 = this.createAccount(2L)
    val requestedAccount3 = this.createAccount(3L)
    val requestedAccount4 = this.createAccount(4L)
    val requestedAccount5 = this.createAccount(5L)

    Await.result(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    // exist for found
    assert(Await.result(friendRequestsDAO.exist(requestedAccount1.id, sessionAccount.id.toSessionId)) == true)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount2.id, sessionAccount.id.toSessionId)) == true)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount3.id, sessionAccount.id.toSessionId)) == true)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount4.id, sessionAccount.id.toSessionId)) == true)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount5.id, sessionAccount.id.toSessionId)) == true)
    Await.result(friendRequestsDAO.delete(requestedAccount1.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount2.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount3.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount4.id, sessionAccount.id.toSessionId))
    Await.result(friendRequestsDAO.delete(requestedAccount5.id, sessionAccount.id.toSessionId))

    // exit for not found
    assert(Await.result(friendRequestsDAO.exist(requestedAccount1.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount2.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount3.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount4.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(friendRequestsDAO.exist(requestedAccount5.id, sessionAccount.id.toSessionId)) == false)

  }

  test("find") {

    val sessionAccount = this.createAccount(0L)
    val sessionAccount2 = this.createAccount(1L)
    val requestedAccount1 = this.createAccount(2L)
    val requestedAccount2 = this.createAccount(3L)
    val requestedAccount3 = this.createAccount(4L)
    val requestedAccount4 = this.createAccount(5L)
    val requestedAccount5 = this.createAccount(6L)

    val friendRequestId1 = Await.result(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    val friendRequestId2 = Await.result(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    val friendRequestId3 = Await.result(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    val friendRequestId4 = Await.result(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    val friendRequestId5 = Await.result(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    // find requests
    val friendRequested1 = Await.result(friendRequestsDAO.find(friendRequestId1, requestedAccount1.id.toSessionId)).get
    val friendRequested2 = Await.result(friendRequestsDAO.find(friendRequestId2, requestedAccount2.id.toSessionId)).get
    val friendRequested3 = Await.result(friendRequestsDAO.find(friendRequestId3, requestedAccount3.id.toSessionId)).get
    val friendRequested4 = Await.result(friendRequestsDAO.find(friendRequestId4, requestedAccount4.id.toSessionId)).get
    val friendRequested5 = Await.result(friendRequestsDAO.find(friendRequestId5, requestedAccount5.id.toSessionId)).get
    assert((friendRequested1.accountId, friendRequested1.by, friendRequested1.requestStatus) == (requestedAccount1.id, sessionAccount.id, FriendRequestStatusType.noresponsed.toValue))
    assert((friendRequested2.accountId, friendRequested2.by, friendRequested2.requestStatus) == (requestedAccount2.id, sessionAccount.id, FriendRequestStatusType.noresponsed.toValue))
    assert((friendRequested3.accountId, friendRequested3.by, friendRequested3.requestStatus) == (requestedAccount3.id, sessionAccount.id, FriendRequestStatusType.noresponsed.toValue))
    assert((friendRequested4.accountId, friendRequested4.by, friendRequested4.requestStatus) == (requestedAccount4.id, sessionAccount.id, FriendRequestStatusType.noresponsed.toValue))
    assert((friendRequested5.accountId, friendRequested5.by, friendRequested5.requestStatus) == (requestedAccount5.id, sessionAccount.id, FriendRequestStatusType.noresponsed.toValue))

    // requests not found
    val friendRequested1NotFound = Await.result(friendRequestsDAO.find(friendRequestId1, sessionAccount2.id.toSessionId))
    val friendRequested2NotFound = Await.result(friendRequestsDAO.find(friendRequestId2, sessionAccount2.id.toSessionId))
    val friendRequested3NotFound = Await.result(friendRequestsDAO.find(friendRequestId3, sessionAccount2.id.toSessionId))
    val friendRequested4NotFound = Await.result(friendRequestsDAO.find(friendRequestId4, sessionAccount2.id.toSessionId))
    val friendRequested5NotFound = Await.result(friendRequestsDAO.find(friendRequestId5, sessionAccount2.id.toSessionId))
    assert(friendRequested1NotFound.isEmpty)
    assert(friendRequested2NotFound.isEmpty)
    assert(friendRequested3NotFound.isEmpty)
    assert(friendRequested4NotFound.isEmpty)
    assert(friendRequested5NotFound.isEmpty)
  }

  test("findAll") {

    val sessionAccount = this.createAccount(0L)
    val requestedAccount1 = this.createAccount(1L)
    val requestedAccount2 = this.createAccount(2L)
    val requestedAccount3 = this.createAccount(3L)
    val requestedAccount4 = this.createAccount(4L)
    val requestedAccount5 = this.createAccount(5L)

    Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount1.id.toSessionId))
    Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount2.id.toSessionId))
    Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount3.id.toSessionId))
    Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount4.id.toSessionId))
    Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount5.id.toSessionId))

    // findAll top page
    val result1 = Await.result(friendRequestsDAO.findAll(None, None, Some(3), sessionAccount.id.toSessionId))
    val friendRequested1 = result1(0)._1
    val friendRequested2 = result1(1)._1
    val friendRequested3 = result1(2)._1
    assert(result1.size == 3)
    assert((friendRequested1.accountId, friendRequested1.by, friendRequested1.requestStatus) == (sessionAccount.id, requestedAccount5.id, FriendRequestStatusType.noresponsed.toValue))
    assert((friendRequested2.accountId, friendRequested2.by, friendRequested2.requestStatus) == (sessionAccount.id, requestedAccount4.id, FriendRequestStatusType.noresponsed.toValue))
    assert((friendRequested3.accountId, friendRequested3.by, friendRequested3.requestStatus) == (sessionAccount.id, requestedAccount3.id, FriendRequestStatusType.noresponsed.toValue))

    // findALl next page
    val result2 = Await.result(friendRequestsDAO.findAll(Some(friendRequested3.requestedAt), None, Some(3), sessionAccount.id.toSessionId))
    assert(result2.size == 2)
    val friendRequested4 = result2(0)._1
    val friendRequested5 = result2(1)._1
    assert((friendRequested4.accountId, friendRequested4.by, friendRequested4.requestStatus) == (sessionAccount.id, requestedAccount2.id, FriendRequestStatusType.noresponsed.toValue))
    assert((friendRequested5.accountId, friendRequested5.by, friendRequested5.requestStatus) == (sessionAccount.id, requestedAccount1.id, FriendRequestStatusType.noresponsed.toValue))

  }

  test("update") {

    val sessionAccount = this.createAccount(0L)
    val requestedAccount1 = this.createAccount(1L)
    val requestedAccount2 = this.createAccount(2L)
    val requestedAccount3 = this.createAccount(3L)
    val requestedAccount4 = this.createAccount(4L)
    val requestedAccount5 = this.createAccount(5L)

    val friendRequestId1 = Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount1.id.toSessionId))
    val friendRequestId2 = Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount2.id.toSessionId))
    val friendRequestId3 = Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount3.id.toSessionId))
    val friendRequestId4 = Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount4.id.toSessionId))
    val friendRequestId5 = Await.result(friendRequestsDAO.create(sessionAccount.id, requestedAccount5.id.toSessionId))

    // update friend requests
    val updateResult1 = Await.result(friendRequestsDAO.update(friendRequestId1, FriendRequestStatusType.rejected, sessionAccount.id.toSessionId))
    val updateResult2 = Await.result(friendRequestsDAO.update(friendRequestId2, FriendRequestStatusType.accepted, sessionAccount.id.toSessionId))
    val updateResult3 = Await.result(friendRequestsDAO.update(friendRequestId3, FriendRequestStatusType.rejected, sessionAccount.id.toSessionId))
    val updateResult4 = Await.result(friendRequestsDAO.update(friendRequestId4, FriendRequestStatusType.accepted, sessionAccount.id.toSessionId))
    val updateResult5 = Await.result(friendRequestsDAO.update(friendRequestId5, FriendRequestStatusType.rejected, sessionAccount.id.toSessionId))
    assert(updateResult1 == true)
    assert(updateResult2 == true)
    assert(updateResult3 == true)
    assert(updateResult4 == true)
    assert(updateResult5 == true)

    // find requests
    val friendRequested1 = Await.result(friendRequestsDAO.find(friendRequestId1, sessionAccount.id.toSessionId)).get
    val friendRequested2 = Await.result(friendRequestsDAO.find(friendRequestId2, sessionAccount.id.toSessionId)).get
    val friendRequested3 = Await.result(friendRequestsDAO.find(friendRequestId3, sessionAccount.id.toSessionId)).get
    val friendRequested4 = Await.result(friendRequestsDAO.find(friendRequestId4, sessionAccount.id.toSessionId)).get
    val friendRequested5 = Await.result(friendRequestsDAO.find(friendRequestId5, sessionAccount.id.toSessionId)).get
    assert((friendRequested1.accountId, friendRequested1.by, friendRequested1.requestStatus) == (sessionAccount.id, requestedAccount1.id, FriendRequestStatusType.rejected.toValue))
    assert((friendRequested2.accountId, friendRequested2.by, friendRequested2.requestStatus) == (sessionAccount.id, requestedAccount2.id, FriendRequestStatusType.accepted.toValue))
    assert((friendRequested3.accountId, friendRequested3.by, friendRequested3.requestStatus) == (sessionAccount.id, requestedAccount3.id, FriendRequestStatusType.rejected.toValue))
    assert((friendRequested4.accountId, friendRequested4.by, friendRequested4.requestStatus) == (sessionAccount.id, requestedAccount4.id, FriendRequestStatusType.accepted.toValue))
    assert((friendRequested5.accountId, friendRequested5.by, friendRequested5.requestStatus) == (sessionAccount.id, requestedAccount5.id, FriendRequestStatusType.rejected.toValue))

  }

}
