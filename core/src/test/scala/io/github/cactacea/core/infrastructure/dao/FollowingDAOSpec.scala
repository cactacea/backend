package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class FollowingDAOSpec extends DAOSpec {

  import db._

  val followingDAO: FollowingDAO = injector.instance[FollowingDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]

  test("create") {

    val sessionAccount = createAccount("FollowingDAOSpec1")
    val followAccount1 = createAccount("FollowingDAOSpec2")
    val followAccount2 = createAccount("FollowingDAOSpec3")

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

    val sessionAccount = createAccount("FollowingDAOSpec4")
    val followAccount1 = createAccount("FollowingDAOSpec5")
    val followAccount2 = createAccount("FollowingDAOSpec6")
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

    val sessionAccount = createAccount("FollowingDAOSpec7")
    val followAccount1 = createAccount("FollowingDAOSpec8")
    val followAccount2 = createAccount("FollowingDAOSpec9")
    val followAccount3 = createAccount("FollowingDAOSpec10")
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

    val sessionAccount1 = createAccount("FollowingDAOSpec11")
    val sessionAccount2 = createAccount("FollowingDAOSpec12")
    val sessionAccount3 = createAccount("FollowingDAOSpec13")
    val sessionAccount4 = createAccount("FollowingDAOSpec14")
    val sessionAccount5 = createAccount("FollowingDAOSpec15")
    val sessionAccount6 = createAccount("FollowingDAOSpec16")
    val followUser = createAccount("FollowingDAOSpec17")

    Await.result(followingDAO.create(sessionAccount1.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount2.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount3.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount4.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount5.id, followUser.id.toSessionId))
    Await.result(followingDAO.create(sessionAccount6.id, followUser.id.toSessionId))

    // find following top page
    val result1 = Await.result(followingDAO.findAll(followUser.id, None, None, Some(3), sessionAccount1.id.toSessionId))
    val account1 = result1(0)._1
    val account2 = result1(1)._1
    val account3 = result1(2)._1
    val following3 = result1(2)._3
    assert(account1.id == sessionAccount6.id)
    assert(account2.id == sessionAccount5.id)
    assert(account3.id == sessionAccount4.id)

    // find following next page
    val result2 = Await.result(followingDAO.findAll(followUser.id, Some(following3.followedAt), None, Some(3), sessionAccount1.id.toSessionId))
    val account4 = result2(0)._1
    val account5 = result2(1)._1
    val account6 = result2(2)._1
    assert(account4.id == sessionAccount3.id)
    assert(account5.id == sessionAccount2.id)
    assert(account6.id == sessionAccount1.id)

  }

}
