package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.Relationships

class FollowingDAOSpec extends DAOSpec {

  import db._

  val followingDAO: FollowingDAO = injector.instance[FollowingDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]

  test("create") {

    val sessionAccount = createAccount("account0")
    val followAccount1 = createAccount("account1")
    val followAccount2 = createAccount("account2")

    // create following
    Await.result(followingDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.follow == true)
    assert(result2.follow == true)

    assert(Await.result(accountsDAO.find(followAccount1.id.toSessionId)).get.followCount == 1)
    assert(Await.result(accountsDAO.find(followAccount2.id.toSessionId)).get.followCount == 1)

    // delete following
    Await.result(followingDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followingDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))

    assert(Await.result(accountsDAO.find(followAccount1.id.toSessionId)).get.followCount == 0)
    assert(Await.result(accountsDAO.find(followAccount2.id.toSessionId)).get.followCount == 0)

    // create following
    Await.result(followingDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result3 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result4 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result3.follow == true)
    assert(result4.follow == true)

    assert(Await.result(accountsDAO.find(followAccount1.id.toSessionId)).get.followCount == 1)
    assert(Await.result(accountsDAO.find(followAccount2.id.toSessionId)).get.followCount == 1)

  }

  test("delete") {

    val sessionAccount = createAccount("account0")
    val followAccount1 = createAccount("account1")
    val followAccount2 = createAccount("account2")
    Await.result(followingDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    // delete following
    Await.result(followingDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followingDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.follow == false)
    assert(result2.follow == false)

  }

  test("exist") {

    val sessionAccount = createAccount("account0")
    val followAccount1 = createAccount("account1")
    val followAccount2 = createAccount("account2")
    val followAccount3 = createAccount("account3")
    Await.result(followingDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    // exist following
    val result1 = Await.result(followingDAO.exist(sessionAccount.id, followAccount1.id.toSessionId))
    val result2 = Await.result(followingDAO.exist(sessionAccount.id, followAccount2.id.toSessionId))
    val result3 = Await.result(followingDAO.exist(sessionAccount.id, followAccount3.id.toSessionId))
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

    Await.result(followingDAO.create(sessionAccount1.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount2.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount3.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount4.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount5.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount6.id, followUser.id.toSessionId))

    // find following top page
    val result1 = Await.result(followingDAO.findAll(followUser.id, None, None, Some(3), sessionAccount1.id.toSessionId))
    assert(result1(0)._1.id == sessionAccount6.id)
    assert(result1(1)._1.id == sessionAccount5.id)
    assert(result1(2)._1.id == sessionAccount4.id)

    // find following next page
    val result2 = Await.result(followingDAO.findAll(followUser.id, Some(result1(2)._3), None, Some(3), sessionAccount1.id.toSessionId))
    assert(result2(0)._1.id == sessionAccount3.id)
    assert(result2(1)._1.id == sessionAccount2.id)
    assert(result2(2)._1.id == sessionAccount1.id)
  }

}
