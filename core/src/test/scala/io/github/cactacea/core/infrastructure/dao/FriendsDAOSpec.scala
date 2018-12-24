package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.FriendsSortType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class FriendsDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("FriendsDAOSpec1")
    val friendAccount1 = createAccount("FriendsDAOSpec2")
    val friendAccount2 = createAccount("FriendsDAOSpec3")

    // create friends
    execute(friendsDAO.create(sessionAccount.id, friendAccount1.id.toSessionId))
    execute(friendsDAO.create(sessionAccount.id, friendAccount2.id.toSessionId))
    val result1 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount1.id))))).head
    val result2 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount2.id))))).head
    assert(result1.isFriend == true)
    assert(result2.isFriend == true)

  }

  test("delete") {

    val sessionAccount = createAccount("FriendsDAOSpec4")
    val friendAccount1 = createAccount("FriendsDAOSpec5")
    val friendAccount2 = createAccount("FriendsDAOSpec6")
    execute(friendsDAO.create(sessionAccount.id, friendAccount1.id.toSessionId))
    execute(friendsDAO.create(sessionAccount.id, friendAccount2.id.toSessionId))

    // delete friends
    execute(friendsDAO.delete(sessionAccount.id, friendAccount1.id.toSessionId))
    execute(friendsDAO.delete(sessionAccount.id, friendAccount2.id.toSessionId))
    val result1 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount1.id))))).head
    val result2 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount2.id))))).head
    assert(result1.isFriend == false)
    assert(result2.isFriend == false)

  }

  test("exist") {

    val sessionAccount = createAccount("FriendsDAOSpec7")
    val friendAccount1 = createAccount("FriendsDAOSpec8")
    val friendAccount2 = createAccount("FriendsDAOSpec9")
    val friendAccount3 = createAccount("FriendsDAOSpec10")
    execute(friendsDAO.create(sessionAccount.id, friendAccount1.id.toSessionId))
    execute(friendsDAO.create(sessionAccount.id, friendAccount2.id.toSessionId))

    // exist friends
    val result1 = execute(friendsDAO.exist(sessionAccount.id, friendAccount1.id.toSessionId))
    val result2 = execute(friendsDAO.exist(sessionAccount.id, friendAccount2.id.toSessionId))
    val result3 = execute(friendsDAO.exist(sessionAccount.id, friendAccount3.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == false)

  }

  test("find a account's friends") {

    val sessionAccount1 = createAccount("FriendsDAOSpec11")
    val sessionAccount2 = createAccount("FriendsDAOSpec12")
    val sessionAccount3 = createAccount("FriendsDAOSpec13")
    val sessionAccount4 = createAccount("FriendsDAOSpec14")
    val sessionAccount5 = createAccount("FriendsDAOSpec15")
    val sessionAccount6 = createAccount("FriendsDAOSpec16")
    val friendUser = createAccount("FriendsDAOSpec17")

    execute(friendsDAO.create(sessionAccount1.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount2.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount3.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount4.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount5.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount6.id, friendUser.id.toSessionId))

    // find friends top page
    val result1 = execute(friendsDAO.find(friendUser.id, None, 0, 3, sessionAccount1.id.toSessionId))
    assert(result1(0).id == sessionAccount6.id)
    assert(result1(1).id == sessionAccount5.id)
    assert(result1(2).id == sessionAccount4.id)

    // find friends next page
    val result2 = execute(friendsDAO.find(friendUser.id, result1(2).next, 0, 3, sessionAccount1.id.toSessionId))
    assert(result2(0).id == sessionAccount3.id)
    assert(result2(1).id == sessionAccount2.id)
    assert(result2(2).id == sessionAccount1.id)
  }

  test("find session friends sort by friendsAt") {

    val sessionAccount1 = createAccount("FriendsDAOSpec21")
    val sessionAccount2 = createAccount("FriendsDAOSpec22")
    val sessionAccount3 = createAccount("FriendsDAOSpec23")
    val sessionAccount4 = createAccount("FriendsDAOSpec24")
    val sessionAccount5 = createAccount("FriendsDAOSpec25")
    val sessionAccount6 = createAccount("FriendsDAOSpec26")
    val friendUser = createAccount("FriendsDAOSpec27")

    execute(friendsDAO.create(sessionAccount1.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount2.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount3.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount4.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount5.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount6.id, friendUser.id.toSessionId))

    // find friends top page
    val result1 = execute(friendsDAO.find(None, 0, 3, FriendsSortType.friendsAt, friendUser.id.toSessionId))
    assert(result1(0).id == sessionAccount6.id)
    assert(result1(1).id == sessionAccount5.id)
    assert(result1(2).id == sessionAccount4.id)

    // find friends next page
    val result2 = execute(friendsDAO.find(result1(2).next, 0, 3, FriendsSortType.friendsAt, friendUser.id.toSessionId))
    assert(result2(0).id == sessionAccount3.id)
    assert(result2(1).id == sessionAccount2.id)
    assert(result2(2).id == sessionAccount1.id)
  }

  test("find session friends sort by accountName") {

    val sessionAccount1 = createAccount("FriendsDAOSpec31")
    val sessionAccount2 = createAccount("FriendsDAOSpec32")
    val sessionAccount3 = createAccount("FriendsDAOSpec33")
    val sessionAccount4 = createAccount("FriendsDAOSpec34")
    val sessionAccount5 = createAccount("FriendsDAOSpec35")
    val sessionAccount6 = createAccount("FriendsDAOSpec36")
    val friendUser = createAccount("FriendsDAOSpec37")

    execute(friendsDAO.create(sessionAccount1.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount2.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount3.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount4.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount5.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount6.id, friendUser.id.toSessionId))

    // find friends top page
    val result1 = execute(friendsDAO.find(None, 0, 3, FriendsSortType.accountName, friendUser.id.toSessionId))
    assert(result1(0).id == sessionAccount1.id)
    assert(result1(1).id == sessionAccount2.id)
    assert(result1(2).id == sessionAccount3.id)

    // find friends next page
    val result2 = execute(friendsDAO.find(result1(2).next, 0, 3, FriendsSortType.accountName, friendUser.id.toSessionId))
    assert(result2(0).id == sessionAccount4.id)
    assert(result2(1).id == sessionAccount5.id)
    assert(result2(2).id == sessionAccount6.id)
  }


}
