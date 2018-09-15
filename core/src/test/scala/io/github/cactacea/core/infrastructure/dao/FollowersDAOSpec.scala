package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class FollowersDAOSpec extends DAOSpec {

  import db._

  val followingDAO: FollowingDAO = injector.instance[FollowingDAO]
  val followersDAO: FollowersDAO = injector.instance[FollowersDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]

  test("create") {

    val sessionAccount = createAccount("FollowersDAOSpec1")
    val followAccount1 = createAccount("FollowersDAOSpec2")
    val followAccount2 = createAccount("FollowersDAOSpec3")
    Await.result(followersDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.follower == true).filter(_.by == lift(followAccount1.id))))).headOption
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.follower == true).filter(_.by == lift(followAccount2.id))))).headOption
    assert(result1.isDefined == true)
    assert(result2.isDefined == true)

    assert(Await.result(accountsDAO.find(sessionAccount.id.toSessionId)).get.followerCount == 2)

    Await.result(followersDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))

    assert(Await.result(accountsDAO.find(sessionAccount.id.toSessionId)).get.followerCount == 0)

    Await.result(followersDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    val result3 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.follower == true).filter(_.by == lift(followAccount1.id))))).headOption
    val result4 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.follower == true).filter(_.by == lift(followAccount2.id))))).headOption
    assert(result3.isDefined == true)
    assert(result4.isDefined == true)

  }

  test("delete") {

    val sessionAccount = createAccount("FollowersDAOSpec4")
    val followAccount1 = createAccount("FollowersDAOSpec5")
    val followAccount2 = createAccount("FollowersDAOSpec6")
    Await.result(followersDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    Await.result(followersDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.follow == false)
    assert(result2.follow == false)

  }

  test("findAll") {

    val sessionAccount1 = createAccount("FollowersDAOSpec7")
    val sessionAccount2 = createAccount("FollowersDAOSpec8")
    val sessionAccount3 = createAccount("FollowersDAOSpec9")
    val sessionAccount4 = createAccount("FollowersDAOSpec10")
    val sessionAccount5 = createAccount("FollowersDAOSpec11")
    val sessionAccount6 = createAccount("FollowersDAOSpec12")
    val followedUser = createAccount("FollowersDAOSpec13")

    Await.result(followingDAO.create(followedUser.id, sessionAccount1.id.toSessionId))
    Await.result(followingDAO.create(followedUser.id, sessionAccount2.id.toSessionId))
    Await.result(followingDAO.create(followedUser.id, sessionAccount3.id.toSessionId))
    Await.result(followingDAO.create(followedUser.id, sessionAccount4.id.toSessionId))
    Await.result(followingDAO.create(followedUser.id, sessionAccount5.id.toSessionId))
    Await.result(followingDAO.create(followedUser.id, sessionAccount6.id.toSessionId))

    val result1 = Await.result(followersDAO.findAll(followedUser.id, None, None, Some(3), sessionAccount1.id.toSessionId))
    val account1 = result1(0)._1
    val account2 = result1(1)._1
    val account3 = result1(2)._1
    val follower1 = result1(2)._3
    assert(account1.id == sessionAccount6.id)
    assert(account2.id == sessionAccount5.id)
    assert(account3.id == sessionAccount4.id)

    val result2 = Await.result(followersDAO.findAll(followedUser.id, Some(follower1.followedAt), None, Some(3), sessionAccount1.id.toSessionId))
    val account4 = result2(0)._1
    val account5 = result2(1)._1
    val account6 = result2(2)._1
    assert(account4.id == sessionAccount3.id)
    assert(account5.id == sessionAccount2.id)
    assert(account6.id == sessionAccount1.id)

  }

}

