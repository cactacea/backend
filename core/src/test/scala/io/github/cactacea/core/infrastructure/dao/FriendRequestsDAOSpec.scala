package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.backend.core.helpers.DAOSpec

class FriendRequestsDAOSpec extends DAOSpec {

  test("create") {

    val sessionAccount = createAccount("FriendRequestsDAOSpec1")
    val requestedAccount1 = createAccount("FriendRequestsDAOSpec2")
    val requestedAccount2 = createAccount("FriendRequestsDAOSpec3")
    val requestedAccount3 = createAccount("FriendRequestsDAOSpec4")
    val requestedAccount4 = createAccount("FriendRequestsDAOSpec5")
    val requestedAccount5 = createAccount("FriendRequestsDAOSpec6")

    val friendRequestId1 = execute(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    val friendRequestId2 = execute(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    val friendRequestId3 = execute(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    val friendRequestId4 = execute(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    val friendRequestId5 = execute(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    assert(execute(friendRequestsDAO.find(friendRequestId1, requestedAccount1.id.toSessionId)).isDefined == true)
    assert(execute(friendRequestsDAO.find(friendRequestId2, requestedAccount2.id.toSessionId)).isDefined == true)
    assert(execute(friendRequestsDAO.find(friendRequestId3, requestedAccount3.id.toSessionId)).isDefined == true)
    assert(execute(friendRequestsDAO.find(friendRequestId4, requestedAccount4.id.toSessionId)).isDefined == true)
    assert(execute(friendRequestsDAO.find(friendRequestId5, requestedAccount5.id.toSessionId)).isDefined == true)

    execute(friendRequestsDAO.delete(requestedAccount1.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount2.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount3.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount4.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount5.id, sessionAccount.id.toSessionId))

    val friendRequestId6 = execute(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    val friendRequestId7 = execute(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    val friendRequestId8 = execute(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    val friendRequestId9 = execute(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    val friendRequestId10 = execute(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    assert(execute(friendRequestsDAO.exist(requestedAccount1.id, sessionAccount.id.toSessionId)) == true)
    assert(execute(friendRequestsDAO.exist(requestedAccount2.id, sessionAccount.id.toSessionId)) == true)
    assert(execute(friendRequestsDAO.exist(requestedAccount3.id, sessionAccount.id.toSessionId)) == true)
    assert(execute(friendRequestsDAO.exist(requestedAccount4.id, sessionAccount.id.toSessionId)) == true)
    assert(execute(friendRequestsDAO.exist(requestedAccount5.id, sessionAccount.id.toSessionId)) == true)

  }

  test("delete") {

    val sessionAccount = createAccount("FriendRequestsDAOSpec7")
    val requestedAccount1 = createAccount("FriendRequestsDAOSpec8")
    val requestedAccount2 = createAccount("FriendRequestsDAOSpec9")
    val requestedAccount3 = createAccount("FriendRequestsDAOSpec10")
    val requestedAccount4 = createAccount("FriendRequestsDAOSpec11")
    val requestedAccount5 = createAccount("FriendRequestsDAOSpec12")

    val friendRequestId1 = execute(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    val friendRequestId2 = execute(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    val friendRequestId3 = execute(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    val friendRequestId4 = execute(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    val friendRequestId5 = execute(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    // delete requests
    execute(friendRequestsDAO.delete(requestedAccount1.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount2.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount3.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount4.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount5.id, sessionAccount.id.toSessionId))
    assert(execute(friendRequestsDAO.exist(requestedAccount1.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(friendRequestsDAO.exist(requestedAccount2.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(friendRequestsDAO.exist(requestedAccount3.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(friendRequestsDAO.exist(requestedAccount4.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(friendRequestsDAO.exist(requestedAccount5.id, sessionAccount.id.toSessionId)) == false)

  }

  test("exist") {

    val sessionAccount = createAccount("FriendRequestsDAOSpec13")
    val requestedAccount1 = createAccount("FriendRequestsDAOSpec14")
    val requestedAccount2 = createAccount("FriendRequestsDAOSpec15")
    val requestedAccount3 = createAccount("FriendRequestsDAOSpec16")
    val requestedAccount4 = createAccount("FriendRequestsDAOSpec17")
    val requestedAccount5 = createAccount("FriendRequestsDAOSpec18")

    execute(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    // exist for found
    assert(execute(friendRequestsDAO.exist(requestedAccount1.id, sessionAccount.id.toSessionId)) == true)
    assert(execute(friendRequestsDAO.exist(requestedAccount2.id, sessionAccount.id.toSessionId)) == true)
    assert(execute(friendRequestsDAO.exist(requestedAccount3.id, sessionAccount.id.toSessionId)) == true)
    assert(execute(friendRequestsDAO.exist(requestedAccount4.id, sessionAccount.id.toSessionId)) == true)
    assert(execute(friendRequestsDAO.exist(requestedAccount5.id, sessionAccount.id.toSessionId)) == true)
    execute(friendRequestsDAO.delete(requestedAccount1.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount2.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount3.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount4.id, sessionAccount.id.toSessionId))
    execute(friendRequestsDAO.delete(requestedAccount5.id, sessionAccount.id.toSessionId))

    // exit for not found
    assert(execute(friendRequestsDAO.exist(requestedAccount1.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(friendRequestsDAO.exist(requestedAccount2.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(friendRequestsDAO.exist(requestedAccount3.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(friendRequestsDAO.exist(requestedAccount4.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(friendRequestsDAO.exist(requestedAccount5.id, sessionAccount.id.toSessionId)) == false)

  }

  test("find") {

    val sessionAccount = createAccount("FriendRequestsDAOSpec19")
    val sessionAccount2 = createAccount("FriendRequestsDAOSpec20")
    val requestedAccount1 = createAccount("FriendRequestsDAOSpec21")
    val requestedAccount2 = createAccount("FriendRequestsDAOSpec22")
    val requestedAccount3 = createAccount("FriendRequestsDAOSpec23")
    val requestedAccount4 = createAccount("FriendRequestsDAOSpec24")
    val requestedAccount5 = createAccount("FriendRequestsDAOSpec25")

    val friendRequestId1 = execute(friendRequestsDAO.create(requestedAccount1.id, sessionAccount.id.toSessionId))
    val friendRequestId2 = execute(friendRequestsDAO.create(requestedAccount2.id, sessionAccount.id.toSessionId))
    val friendRequestId3 = execute(friendRequestsDAO.create(requestedAccount3.id, sessionAccount.id.toSessionId))
    val friendRequestId4 = execute(friendRequestsDAO.create(requestedAccount4.id, sessionAccount.id.toSessionId))
    val friendRequestId5 = execute(friendRequestsDAO.create(requestedAccount5.id, sessionAccount.id.toSessionId))

    // find requests
    val friendRequested1 = execute(friendRequestsDAO.find(friendRequestId1, requestedAccount1.id.toSessionId)).get
    val friendRequested2 = execute(friendRequestsDAO.find(friendRequestId2, requestedAccount2.id.toSessionId)).get
    val friendRequested3 = execute(friendRequestsDAO.find(friendRequestId3, requestedAccount3.id.toSessionId)).get
    val friendRequested4 = execute(friendRequestsDAO.find(friendRequestId4, requestedAccount4.id.toSessionId)).get
    val friendRequested5 = execute(friendRequestsDAO.find(friendRequestId5, requestedAccount5.id.toSessionId)).get
    assert((friendRequested1.accountId, friendRequested1.by, friendRequested1.requestStatus) == (requestedAccount1.id, sessionAccount.id, FriendRequestStatusType.noResponded))
    assert((friendRequested2.accountId, friendRequested2.by, friendRequested2.requestStatus) == (requestedAccount2.id, sessionAccount.id, FriendRequestStatusType.noResponded))
    assert((friendRequested3.accountId, friendRequested3.by, friendRequested3.requestStatus) == (requestedAccount3.id, sessionAccount.id, FriendRequestStatusType.noResponded))
    assert((friendRequested4.accountId, friendRequested4.by, friendRequested4.requestStatus) == (requestedAccount4.id, sessionAccount.id, FriendRequestStatusType.noResponded))
    assert((friendRequested5.accountId, friendRequested5.by, friendRequested5.requestStatus) == (requestedAccount5.id, sessionAccount.id, FriendRequestStatusType.noResponded))

    // requests not found
    val friendRequested1NotFound = execute(friendRequestsDAO.find(friendRequestId1, sessionAccount2.id.toSessionId))
    val friendRequested2NotFound = execute(friendRequestsDAO.find(friendRequestId2, sessionAccount2.id.toSessionId))
    val friendRequested3NotFound = execute(friendRequestsDAO.find(friendRequestId3, sessionAccount2.id.toSessionId))
    val friendRequested4NotFound = execute(friendRequestsDAO.find(friendRequestId4, sessionAccount2.id.toSessionId))
    val friendRequested5NotFound = execute(friendRequestsDAO.find(friendRequestId5, sessionAccount2.id.toSessionId))
    assert(friendRequested1NotFound.isEmpty)
    assert(friendRequested2NotFound.isEmpty)
    assert(friendRequested3NotFound.isEmpty)
    assert(friendRequested4NotFound.isEmpty)
    assert(friendRequested5NotFound.isEmpty)
  }

  test("findAll") {

    val sessionAccount = createAccount("FriendRequestsDAOSpec26")
    val requestedAccount1 = createAccount("FriendRequestsDAOSpec27")
    val requestedAccount2 = createAccount("FriendRequestsDAOSpec28")
    val requestedAccount3 = createAccount("FriendRequestsDAOSpec29")
    val requestedAccount4 = createAccount("FriendRequestsDAOSpec30")
    val requestedAccount5 = createAccount("FriendRequestsDAOSpec31")

    execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount1.id.toSessionId))
    execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount2.id.toSessionId))
    execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount3.id.toSessionId))
    execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount4.id.toSessionId))
    execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount5.id.toSessionId))

    // findAll top page
    val result1 = execute(friendRequestsDAO.findAll(None, None, Some(3), true, sessionAccount.id.toSessionId))
    val friendRequested1 = result1(0)._1
    val friendRequested2 = result1(1)._1
    val friendRequested3 = result1(2)._1
    assert(result1.size == 3)
    assert((friendRequested1.accountId, friendRequested1.by, friendRequested1.requestStatus) == (sessionAccount.id, requestedAccount5.id, FriendRequestStatusType.noResponded))
    assert((friendRequested2.accountId, friendRequested2.by, friendRequested2.requestStatus) == (sessionAccount.id, requestedAccount4.id, FriendRequestStatusType.noResponded))
    assert((friendRequested3.accountId, friendRequested3.by, friendRequested3.requestStatus) == (sessionAccount.id, requestedAccount3.id, FriendRequestStatusType.noResponded))

    // findALl next page
    val result2 = execute(friendRequestsDAO.findAll(Some(friendRequested3.id.value), None, Some(3), true, sessionAccount.id.toSessionId))
    assert(result2.size == 2)
    val friendRequested4 = result2(0)._1
    val friendRequested5 = result2(1)._1
    assert((friendRequested4.accountId, friendRequested4.by, friendRequested4.requestStatus) == (sessionAccount.id, requestedAccount2.id, FriendRequestStatusType.noResponded))
    assert((friendRequested5.accountId, friendRequested5.by, friendRequested5.requestStatus) == (sessionAccount.id, requestedAccount1.id, FriendRequestStatusType.noResponded))

  }

  test("update") {

    val sessionAccount = createAccount("FriendRequestsDAOSpec32")
    val requestedAccount1 = createAccount("FriendRequestsDAOSpec33")
    val requestedAccount2 = createAccount("FriendRequestsDAOSpec34")
    val requestedAccount3 = createAccount("FriendRequestsDAOSpec35")
    val requestedAccount4 = createAccount("FriendRequestsDAOSpec36")
    val requestedAccount5 = createAccount("FriendRequestsDAOSpec37")

    val friendRequestId1 = execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount1.id.toSessionId))
    val friendRequestId2 = execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount2.id.toSessionId))
    val friendRequestId3 = execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount3.id.toSessionId))
    val friendRequestId4 = execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount4.id.toSessionId))
    val friendRequestId5 = execute(friendRequestsDAO.create(sessionAccount.id, requestedAccount5.id.toSessionId))

    // update friend requests
    val updateResult1 = execute(friendRequestsDAO.update(friendRequestId1, FriendRequestStatusType.rejected, sessionAccount.id.toSessionId))
    val updateResult2 = execute(friendRequestsDAO.update(friendRequestId2, FriendRequestStatusType.accepted, sessionAccount.id.toSessionId))
    val updateResult3 = execute(friendRequestsDAO.update(friendRequestId3, FriendRequestStatusType.rejected, sessionAccount.id.toSessionId))
    val updateResult4 = execute(friendRequestsDAO.update(friendRequestId4, FriendRequestStatusType.accepted, sessionAccount.id.toSessionId))
    val updateResult5 = execute(friendRequestsDAO.update(friendRequestId5, FriendRequestStatusType.rejected, sessionAccount.id.toSessionId))
    assert(updateResult1 == true)
    assert(updateResult2 == true)
    assert(updateResult3 == true)
    assert(updateResult4 == true)
    assert(updateResult5 == true)

    // find requests
    val friendRequested1 = execute(friendRequestsDAO.find(friendRequestId1, sessionAccount.id.toSessionId)).get
    val friendRequested2 = execute(friendRequestsDAO.find(friendRequestId2, sessionAccount.id.toSessionId)).get
    val friendRequested3 = execute(friendRequestsDAO.find(friendRequestId3, sessionAccount.id.toSessionId)).get
    val friendRequested4 = execute(friendRequestsDAO.find(friendRequestId4, sessionAccount.id.toSessionId)).get
    val friendRequested5 = execute(friendRequestsDAO.find(friendRequestId5, sessionAccount.id.toSessionId)).get
    assert((friendRequested1.accountId, friendRequested1.by, friendRequested1.requestStatus) == (sessionAccount.id, requestedAccount1.id, FriendRequestStatusType.rejected))
    assert((friendRequested2.accountId, friendRequested2.by, friendRequested2.requestStatus) == (sessionAccount.id, requestedAccount2.id, FriendRequestStatusType.accepted))
    assert((friendRequested3.accountId, friendRequested3.by, friendRequested3.requestStatus) == (sessionAccount.id, requestedAccount3.id, FriendRequestStatusType.rejected))
    assert((friendRequested4.accountId, friendRequested4.by, friendRequested4.requestStatus) == (sessionAccount.id, requestedAccount4.id, FriendRequestStatusType.accepted))
    assert((friendRequested5.accountId, friendRequested5.by, friendRequested5.requestStatus) == (sessionAccount.id, requestedAccount5.id, FriendRequestStatusType.rejected))

  }

}
