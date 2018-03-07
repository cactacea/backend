package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.Relationships

class FollowsDAOSpec extends DAOSpec {

  import db._

  val followsDAO: FollowsDAO = injector.instance[FollowsDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]

  test("create") {

    val sessionAccount = createAccount("account0")
    val followAccount1 = createAccount("account1")
    val followAccount2 = createAccount("account2")

    // create follows
    Await.result(followsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.followed == true)
    assert(result2.followed == true)

    assert(Await.result(accountsDAO.find(followAccount1.id.toSessionId)).get.followCount == 1)
    assert(Await.result(accountsDAO.find(followAccount2.id.toSessionId)).get.followCount == 1)

    // delete follows
    Await.result(followsDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followsDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))

    assert(Await.result(accountsDAO.find(followAccount1.id.toSessionId)).get.followCount == 0)
    assert(Await.result(accountsDAO.find(followAccount2.id.toSessionId)).get.followCount == 0)

    // create follows
    Await.result(followsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result3 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result4 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result3.followed == true)
    assert(result4.followed == true)

    assert(Await.result(accountsDAO.find(followAccount1.id.toSessionId)).get.followCount == 1)
    assert(Await.result(accountsDAO.find(followAccount2.id.toSessionId)).get.followCount == 1)

  }

  test("delete") {

    val sessionAccount = createAccount("account0")
    val followAccount1 = createAccount("account1")
    val followAccount2 = createAccount("account2")
    Await.result(followsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    // delete follows
    Await.result(followsDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followsDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.followed == false)
    assert(result2.followed == false)

  }

  test("exist") {

    val sessionAccount = createAccount("account0")
    val followAccount1 = createAccount("account1")
    val followAccount2 = createAccount("account2")
    val followAccount3 = createAccount("account3")
    Await.result(followsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    // exist follows
    val result1 = Await.result(followsDAO.exist(sessionAccount.id, followAccount1.id.toSessionId))
    val result2 = Await.result(followsDAO.exist(sessionAccount.id, followAccount2.id.toSessionId))
    val result3 = Await.result(followsDAO.exist(sessionAccount.id, followAccount3.id.toSessionId))
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
    val followUser = createAccount("account6")

    Await.result(followsDAO.create(sessionAccount1.id, followUser.id.toSessionId))
    Await.result(followsDAO.create(sessionAccount2.id, followUser.id.toSessionId))
    Await.result(followsDAO.create(sessionAccount3.id, followUser.id.toSessionId))
    Await.result(followsDAO.create(sessionAccount4.id, followUser.id.toSessionId))
    Await.result(followsDAO.create(sessionAccount5.id, followUser.id.toSessionId))
    Await.result(followsDAO.create(sessionAccount6.id, followUser.id.toSessionId))

    // find follows top page
    val result1 = Await.result(followsDAO.findAll(followUser.id, None, None, Some(3), sessionAccount1.id.toSessionId))
    assert(result1(0)._1.id == sessionAccount6.id)
    assert(result1(1)._1.id == sessionAccount5.id)
    assert(result1(2)._1.id == sessionAccount4.id)

    // find follows next page
    val result2 = Await.result(followsDAO.findAll(followUser.id, Some(result1(2)._1.position), None, Some(3), sessionAccount1.id.toSessionId))
    assert(result2(0)._1.id == sessionAccount3.id)
    assert(result2(1)._1.id == sessionAccount2.id)
    assert(result2(2)._1.id == sessionAccount1.id)
  }

}
