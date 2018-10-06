package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
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
    assert(result1.friend == true)
    assert(result2.friend == true)

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
    assert(result1.friend == false)
    assert(result2.friend == false)

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

  test("findAll") {

    val sessionAccount1 = createAccount("FriendsDAOSpec11")
    val sessionAccount2 = createAccount("FriendsDAOSpec12")
    val sessionAccount3 = createAccount("FriendsDAOSpec13")
    val sessionAccount4 = createAccount("FriendsDAOSpec14")
    val sessionAccount5 = createAccount("FriendsDAOSpec15")
    val sessionAccount6 = createAccount("FriendsDAOSpec16")
    val friendUser = createAccount("FriendsDAOSpec16")

    execute(friendsDAO.create(sessionAccount1.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount2.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount3.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount4.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount5.id, friendUser.id.toSessionId))
    execute(friendsDAO.create(sessionAccount6.id, friendUser.id.toSessionId))

    // find friends top page
    val result1 = execute(friendsDAO.findAll(friendUser.id, None, None, Some(3), sessionAccount1.id.toSessionId))
    val account1 = result1(0)._1
    val account2 = result1(1)._1
    val account3 = result1(2)._1
    val friend3 = result1(2)._3
    assert(account1.id == sessionAccount6.id)
    assert(account2.id == sessionAccount5.id)
    assert(account3.id == sessionAccount4.id)

    // find friends next page
    val result2 = execute(friendsDAO.findAll(friendUser.id, Some(friend3.id.value), None, Some(3), sessionAccount1.id.toSessionId))
    val account4 = result2(0)._1
    val account5 = result2(1)._1
    val account6 = result2(2)._1
    assert(account4.id == sessionAccount3.id)
    assert(account5.id == sessionAccount2.id)
    assert(account6.id == sessionAccount1.id)
  }

}
