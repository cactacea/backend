package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class FollowsDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("FollowsDAOSpec1")
    val followAccount1 = createAccount("FollowsDAOSpec2")
    val followAccount2 = createAccount("FollowsDAOSpec3")

    // create follower
    execute(followsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    execute(followsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.follow == true)
    assert(result2.follow == true)

    assert(execute(accountsDAO.find(followAccount1.id.toSessionId)).get.followCount == 1)
    assert(execute(accountsDAO.find(followAccount2.id.toSessionId)).get.followCount == 1)

    // delete follower
    execute(followsDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    execute(followsDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))

    assert(execute(accountsDAO.find(followAccount1.id.toSessionId)).get.followCount == 0)
    assert(execute(accountsDAO.find(followAccount2.id.toSessionId)).get.followCount == 0)

    // create follower
    execute(followsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    execute(followsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result3 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result4 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result3.follow == true)
    assert(result4.follow == true)

    assert(execute(accountsDAO.find(followAccount1.id.toSessionId)).get.followCount == 1)
    assert(execute(accountsDAO.find(followAccount2.id.toSessionId)).get.followCount == 1)

  }

  test("delete") {

    val sessionAccount = createAccount("FollowsDAOSpec4")
    val followAccount1 = createAccount("FollowsDAOSpec5")
    val followAccount2 = createAccount("FollowsDAOSpec6")
    execute(followsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    execute(followsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    // delete follower
    execute(followsDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    execute(followsDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.follow == false)
    assert(result2.follow == false)

  }

  test("exist") {

    val sessionAccount = createAccount("FollowsDAOSpec7")
    val followAccount1 = createAccount("FollowsDAOSpec8")
    val followAccount2 = createAccount("FollowsDAOSpec9")
    val followAccount3 = createAccount("FollowsDAOSpec10")
    execute(followsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    execute(followsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    // exist follower
    val result1 = execute(followsDAO.exist(sessionAccount.id, followAccount1.id.toSessionId))
    val result2 = execute(followsDAO.exist(sessionAccount.id, followAccount2.id.toSessionId))
    val result3 = execute(followsDAO.exist(sessionAccount.id, followAccount3.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == false)

  }

  test("find all") {

    val sessionAccount1 = createAccount("FollowsDAOSpec11")
    val sessionAccount2 = createAccount("FollowsDAOSpec12")
    val sessionAccount3 = createAccount("FollowsDAOSpec13")
    val sessionAccount4 = createAccount("FollowsDAOSpec14")
    val sessionAccount5 = createAccount("FollowsDAOSpec15")
    val sessionAccount6 = createAccount("FollowsDAOSpec16")
    val followUser = createAccount("FollowsDAOSpec17")

    execute(followsDAO.create(sessionAccount1.id, followUser.id.toSessionId))
    execute(followsDAO.create(sessionAccount2.id, followUser.id.toSessionId))
    execute(followsDAO.create(sessionAccount3.id, followUser.id.toSessionId))
    execute(followsDAO.create(sessionAccount4.id, followUser.id.toSessionId))
    execute(followsDAO.create(sessionAccount5.id, followUser.id.toSessionId))
    execute(followsDAO.create(sessionAccount6.id, followUser.id.toSessionId))

    // find follower top page
    val result1 = execute(followsDAO.find(followUser.id, None, 0, 3, sessionAccount1.id.toSessionId))
    val account1 = result1(0)
    val account2 = result1(1)
    val account3 = result1(2)
    assert(account1.id == sessionAccount6.id)
    assert(account2.id == sessionAccount5.id)
    assert(account3.id == sessionAccount4.id)

    // find follower next page
    val result2 = execute(followsDAO.find(followUser.id, account3.next, 0, 3, sessionAccount1.id.toSessionId))
    val account4 = result2(0)
    val account5 = result2(1)
    val account6 = result2(2)
    assert(account4.id == sessionAccount3.id)
    assert(account5.id == sessionAccount2.id)
    assert(account6.id == sessionAccount1.id)

  }

}
