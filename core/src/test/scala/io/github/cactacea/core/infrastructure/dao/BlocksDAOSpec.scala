package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.Blocks

class BlocksDAOSpec extends DAOSpec {

  import db._

  val blocksDAO: BlocksDAO = injector.instance[BlocksDAO]

  test("create") {

    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")

    // create
    Await.result(blocksDAO.create(account1.id, account2.id.toSessionId))
    Await.result(blocksDAO.create(account2.id, account3.id.toSessionId))
    Await.result(blocksDAO.create(account3.id, account1.id.toSessionId))
    val b1 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account1.id)).filter(_.by == lift(account2.id))))).head
    val b2 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account2.id)).filter(_.by == lift(account3.id))))).head
    val b3 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account3.id)).filter(_.by == lift(account1.id))))).head
    assert((b1.blocked, b1.beingBlocked) == (true, false))
    assert((b2.blocked, b2.beingBlocked) == (true, false))
    assert((b3.blocked, b3.beingBlocked) == (true, false))

    // delete
    Await.result(blocksDAO.create(account1.id, account2.id.toSessionId))
    Await.result(blocksDAO.create(account2.id, account3.id.toSessionId))
    Await.result(blocksDAO.create(account3.id, account1.id.toSessionId))

    val b4 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account1.id)).filter(_.by == lift(account2.id))))).head
    val b5 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account2.id)).filter(_.by == lift(account3.id))))).head
    val b6 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account3.id)).filter(_.by == lift(account1.id))))).head
    assert((b4.blocked, b4.beingBlocked) == (true, false))
    assert((b5.blocked, b5.beingBlocked) == (true, false))
    assert((b6.blocked, b6.beingBlocked) == (true, false))

  }

  test("delete") {

    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")

    // create
    Await.result(blocksDAO.create(account1.id, account2.id.toSessionId))
    Await.result(blocksDAO.create(account2.id, account3.id.toSessionId))
    Await.result(blocksDAO.create(account3.id, account1.id.toSessionId))

    // delete
    Await.result(blocksDAO.delete(account1.id, account2.id.toSessionId))
    Await.result(blocksDAO.delete(account2.id, account3.id.toSessionId))
    Await.result(blocksDAO.delete(account3.id, account1.id.toSessionId))

    val d4 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account1.id)).filter(_.by == lift(account2.id))))).head
    val d5 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account2.id)).filter(_.by == lift(account3.id))))).head
    val d6 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account3.id)).filter(_.by == lift(account1.id))))).head
    assert((d4.blocked, d4.beingBlocked) == (false, false))
    assert((d5.blocked, d5.beingBlocked) == (false, false))
    assert((d6.blocked, d6.beingBlocked) == (false, false))

  }

  test("exist") {

    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")

    // create
    Await.result(blocksDAO.create(account1.id, account2.id.toSessionId))
    Await.result(blocksDAO.create(account2.id, account3.id.toSessionId))
    Await.result(blocksDAO.create(account3.id, account1.id.toSessionId))

    assert(Await.result(blocksDAO.exist(account1.id, account2.id.toSessionId)) == true)
    assert(Await.result(blocksDAO.exist(account1.id, account3.id.toSessionId)) == false)
    assert(Await.result(blocksDAO.exist(account2.id, account1.id.toSessionId)) == false)
    assert(Await.result(blocksDAO.exist(account2.id, account3.id.toSessionId)) == true)
    assert(Await.result(blocksDAO.exist(account3.id, account1.id.toSessionId)) == true)
    assert(Await.result(blocksDAO.exist(account3.id, account2.id.toSessionId)) == false)

    // delete
    Await.result(blocksDAO.delete(account1.id, account2.id.toSessionId))
    Await.result(blocksDAO.delete(account2.id, account3.id.toSessionId))
    Await.result(blocksDAO.delete(account3.id, account1.id.toSessionId))

    assert(Await.result(blocksDAO.exist(account1.id, account2.id.toSessionId)) == false)
    assert(Await.result(blocksDAO.exist(account1.id, account3.id.toSessionId)) == false)
    assert(Await.result(blocksDAO.exist(account2.id, account1.id.toSessionId)) == false)
    assert(Await.result(blocksDAO.exist(account2.id, account3.id.toSessionId)) == false)
    assert(Await.result(blocksDAO.exist(account3.id, account1.id.toSessionId)) == false)
    assert(Await.result(blocksDAO.exist(account3.id, account2.id.toSessionId)) == false)

    // create
    Await.result(blocksDAO.create(account1.id, account2.id.toSessionId))
    Await.result(blocksDAO.create(account2.id, account3.id.toSessionId))
    Await.result(blocksDAO.create(account3.id, account1.id.toSessionId))

    assert(Await.result(blocksDAO.exist(account1.id, account2.id.toSessionId)) == true)
    assert(Await.result(blocksDAO.exist(account1.id, account3.id.toSessionId)) == false)
    assert(Await.result(blocksDAO.exist(account2.id, account1.id.toSessionId)) == false)
    assert(Await.result(blocksDAO.exist(account2.id, account3.id.toSessionId)) == true)
    assert(Await.result(blocksDAO.exist(account3.id, account1.id.toSessionId)) == true)
    assert(Await.result(blocksDAO.exist(account3.id, account2.id.toSessionId)) == false)

  }

  test("findAll") {

    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")
    val account4 = createAccount("account4")
    val account5 = createAccount("account5")
    val account6 = createAccount("account6")
    val account7 = createAccount("account7")
    val account8 = createAccount("account8")
    val account9 = createAccount("account9")

    // create
    Await.result(blocksDAO.create(account4.id, account2.id.toSessionId))
    Await.result(blocksDAO.create(account4.id, account3.id.toSessionId))

    Await.result(blocksDAO.create(account2.id, account1.id.toSessionId))
    Await.result(blocksDAO.create(account3.id, account1.id.toSessionId))
    Await.result(blocksDAO.create(account4.id, account1.id.toSessionId))
    Await.result(blocksDAO.create(account5.id, account1.id.toSessionId))
    Await.result(blocksDAO.create(account6.id, account1.id.toSessionId))
    Await.result(blocksDAO.create(account7.id, account1.id.toSessionId))
    Await.result(blocksDAO.create(account8.id, account1.id.toSessionId))
    Await.result(blocksDAO.create(account9.id, account1.id.toSessionId))

    // find
    val result1 = Await.result(blocksDAO.findAll(None, None, Some(3), account1.id.toSessionId))
    assert(result1.size == 3)
    val blockedAccount1 = result1(0)._1
    val blockedAccount2 = result1(1)._1
    val blockedAccount3 = result1(2)._1
    val blocked3 = result1(2)._3
    assert(blockedAccount1.id == account9.id)
    assert(blockedAccount2.id == account8.id)
    assert(blockedAccount3.id == account7.id)

    val result2 = Await.result(blocksDAO.findAll(Some(blocked3.id.value), None, Some(3), account1.id.toSessionId))
    assert(result2.size == 3)
    val blockedAccount4 = result2(0)._1
    val blockedAccount5 = result2(1)._1
    val blockedAccount6 = result2(2)._1
    assert(blockedAccount4.id == account6.id)
    assert(blockedAccount5.id == account5.id)
    assert(blockedAccount6.id == account4.id)
    val blocked6 = result2(2)._3

    val result3 = Await.result(blocksDAO.findAll(Some(blocked6.id.value), None, Some(3), account1.id.toSessionId))
    assert(result3.size == 2)
    val blockedAccount7 = result3(0)._1
    val blockedAccount8 = result3(1)._1
    assert(blockedAccount7.id == account3.id)
    assert(blockedAccount8.id == account2.id)

  }

}
