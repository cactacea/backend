package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.infrastructure.models.Relationships
import io.github.cactacea.core.specs.DAOSpec

class FollowersDAOSpec extends DAOSpec {

  import db._

  val followsDAO: FollowsDAO = injector.instance[FollowsDAO]
  val followersDAO: FollowersDAO = injector.instance[FollowersDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]

  test("create") {

    val sessionAccount = this.createAccount(0L)
    val followAccount1 = this.createAccount(1L)
    val followAccount2 = this.createAccount(2L)
    Await.result(followersDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.follower == lift(true)).filter(_.by == lift(followAccount1.id))))).headOption
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.follower == lift(true)).filter(_.by == lift(followAccount2.id))))).headOption
    assert(result1.isDefined == true)
    assert(result2.isDefined == true)

    assert(Await.result(accountsDAO.find(sessionAccount.id.toSessionId)).get.followerCount == 2)

    Await.result(followersDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))

    assert(Await.result(accountsDAO.find(sessionAccount.id.toSessionId)).get.followerCount == 0)

    Await.result(followersDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    val result3 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.follower == lift(true)).filter(_.by == lift(followAccount1.id))))).headOption
    val result4 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.follower == lift(true)).filter(_.by == lift(followAccount2.id))))).headOption
    assert(result3.isDefined == true)
    assert(result4.isDefined == true)

  }

  test("delete") {

    val sessionAccount = this.createAccount(0L)
    val followAccount1 = this.createAccount(1L)
    val followAccount2 = this.createAccount(2L)
    Await.result(followersDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    Await.result(followersDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(followersDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.followed == false)
    assert(result2.followed == false)

  }

  test("findAll") {

    val sessionAccount1 = this.createAccount(0L)
    val sessionAccount2 = this.createAccount(1L)
    val sessionAccount3 = this.createAccount(2L)
    val sessionAccount4 = this.createAccount(3L)
    val sessionAccount5 = this.createAccount(4L)
    val sessionAccount6 = this.createAccount(5L)
    val followUser = this.createAccount(6L)

    Await.result(followsDAO.create(followUser.id, sessionAccount1.id.toSessionId))
    Await.result(followsDAO.create(followUser.id, sessionAccount2.id.toSessionId))
    Await.result(followsDAO.create(followUser.id, sessionAccount3.id.toSessionId))
    Await.result(followsDAO.create(followUser.id, sessionAccount4.id.toSessionId))
    Await.result(followsDAO.create(followUser.id, sessionAccount5.id.toSessionId))
    Await.result(followsDAO.create(followUser.id, sessionAccount6.id.toSessionId))

    val result1 = Await.result(followersDAO.findAll(followUser.id, None, None, Some(3), sessionAccount1.id.toSessionId))
    assert(result1(0)._1.id == sessionAccount6.id)
    assert(result1(1)._1.id == sessionAccount5.id)
    assert(result1(2)._1.id == sessionAccount4.id)

    val result2 = Await.result(followersDAO.findAll(followUser.id, Some(result1(2)._1.position), None, Some(3), sessionAccount1.id.toSessionId))
    assert(result2(0)._1.id == sessionAccount3.id)
    assert(result2(1)._1.id == sessionAccount2.id)
    assert(result2(2)._1.id == sessionAccount1.id)
  }

}

