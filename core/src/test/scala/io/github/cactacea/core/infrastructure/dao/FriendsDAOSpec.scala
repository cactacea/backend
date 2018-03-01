package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.Relationships

class FriendsDAOSpec extends DAOSpec {

  val friendsDAO: FriendsDAO = injector.instance[FriendsDAO]

  import db._

  test("create") {

    val sessionAccount = this.createAccount(0L)
    val friendAccount1 = this.createAccount(1L)
    val friendAccount2 = this.createAccount(2L)

    // create friends
    Await.result(friendsDAO.create(sessionAccount.id, friendAccount1.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount.id, friendAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(friendAccount2.id))))).head
    assert(result1.friend == true)
    assert(result2.friend == true)

  }

  test("delete") {

    val sessionAccount = this.createAccount(0L)
    val friendAccount1 = this.createAccount(1L)
    val friendAccount2 = this.createAccount(2L)
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

    val sessionAccount = this.createAccount(0L)
    val friendAccount1 = this.createAccount(1L)
    val friendAccount2 = this.createAccount(2L)
    val friendAccount3 = this.createAccount(3L)
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

    val sessionAccount1 = this.createAccount(0L)
    val sessionAccount2 = this.createAccount(1L)
    val sessionAccount3 = this.createAccount(2L)
    val sessionAccount4 = this.createAccount(3L)
    val sessionAccount5 = this.createAccount(4L)
    val sessionAccount6 = this.createAccount(5L)
    val friendUser = this.createAccount(6L)

    Await.result(friendsDAO.create(sessionAccount1.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount2.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount3.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount4.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount5.id, friendUser.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount6.id, friendUser.id.toSessionId))

    // find friends top page
    val result1 = Await.result(friendsDAO.findAll(friendUser.id, None, None, Some(3), sessionAccount1.id.toSessionId))
    assert(result1(0)._1.id == sessionAccount6.id)
    assert(result1(1)._1.id == sessionAccount5.id)
    assert(result1(2)._1.id == sessionAccount4.id)

    // find friends next page
    val result2 = Await.result(friendsDAO.findAll(friendUser.id, Some(result1(2)._1.position), None, Some(3), sessionAccount1.id.toSessionId))
    assert(result2(0)._1.id == sessionAccount3.id)
    assert(result2(1)._1.id == sessionAccount2.id)
    assert(result2(2)._1.id == sessionAccount1.id)
  }

}
