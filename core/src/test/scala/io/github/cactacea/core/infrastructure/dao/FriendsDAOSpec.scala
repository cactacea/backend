package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class FriendsDAOSpec extends DAOSpec {

  val friendsDAO: FriendsDAO = injector.instance[FriendsDAO]

  import db._

  test("create") {

    val sessionAccount = createAccount("account0")
    val friendAccount1 = createAccount("account1")
    val friendAccount2 = createAccount("account2")

    // create friends
    Await.result(friendsDAO.create(sessionAccount.id, friendAccount1.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount.id, friendAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount2.id))))).head
    assert(result1.friend == true)
    assert(result2.friend == true)

  }

  test("delete") {

    val sessionAccount = createAccount("account0")
    val friendAccount1 = createAccount("account1")
    val friendAccount2 = createAccount("account2")
    Await.result(friendsDAO.create(sessionAccount.id, friendAccount1.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount.id, friendAccount2.id.toSessionId))

    // delete friends
    Await.result(friendsDAO.delete(sessionAccount.id, friendAccount1.id.toSessionId))
    Await.result(friendsDAO.delete(sessionAccount.id, friendAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount2.id))))).head
    assert(result1.friend == false)
    assert(result2.friend == false)

  }

  test("exist") {

    val sessionAccount = createAccount("account0")
    val friendAccount1 = createAccount("account1")
    val friendAccount2 = createAccount("account2")
    val friendAccount3 = createAccount("account3")
    Await.result(friendsDAO.create(sessionAccount.id, friendAccount1.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount.id, friendAccount2.id.toSessionId))

    // exist friends
    val result1 = Await.result(friendsDAO.exist(sessionAccount.id, friendAccount1.id.toSessionId))
    val result2 = Await.result(friendsDAO.exist(sessionAccount.id, friendAccount2.id.toSessionId))
    val result3 = Await.result(friendsDAO.exist(sessionAccount.id, friendAccount3.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == false)

  }

  test("findAll") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")
    val sessionAccount4 = createAccount("account3")
    val sessionAccount5 = createAccount("account4")
    val sessionAccount6 = createAccount("account5")
    val friendUser = createAccount("account6")

    Await.result(friendsDAO.create(sessionAccount1.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount2.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount3.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount4.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount5.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount6.id, friendUser.id.toSessionId))

    // find friends top page
    val result1 = Await.result(friendsDAO.findAll(friendUser.id, None, None, Some(3), sessionAccount1.id.toSessionId))
    val account1 = result1(0)._1
    val account2 = result1(1)._1
    val account3 = result1(2)._1
    val friend3 = result1(2)._3
    assert(account1.id == sessionAccount6.id)
    assert(account2.id == sessionAccount5.id)
    assert(account3.id == sessionAccount4.id)

    // find friends next page
    val result2 = Await.result(friendsDAO.findAll(friendUser.id, Some(friend3.friendedAt), None, Some(3), sessionAccount1.id.toSessionId))
    val account4 = result2(0)._1
    val account5 = result2(1)._1
    val account6 = result2(2)._1
    assert(account4.id == sessionAccount3.id)
    assert(account5.id == sessionAccount2.id)
    assert(account6.id == sessionAccount1.id)
  }

}
