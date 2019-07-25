package io.github.cactacea.backend.core.infrastructure.dao


import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Blocks

class BlocksDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val account1 = createAccount("BlocksDAOSpec1")
    val account2 = createAccount("BlocksDAOSpec2")
    val account3 = createAccount("BlocksDAOSpec3")

    // create
    execute(blocksDAO.create(account1.id, account2.id.toSessionId))
    execute(blocksDAO.create(account2.id, account3.id.toSessionId))
    execute(blocksDAO.create(account3.id, account1.id.toSessionId))
    val b1 = execute(db.run(quote(query[Blocks].filter(_.accountId == lift(account1.id)).filter(_.by == lift(account2.id)))))
    val b2 = execute(db.run(quote(query[Blocks].filter(_.accountId == lift(account2.id)).filter(_.by == lift(account3.id)))))
    val b3 = execute(db.run(quote(query[Blocks].filter(_.accountId == lift(account3.id)).filter(_.by == lift(account1.id)))))
    assert(b1.nonEmpty)
    assert(b2.nonEmpty)
    assert(b3.nonEmpty)

    // delete
    execute(blocksDAO.create(account1.id, account2.id.toSessionId))
    execute(blocksDAO.create(account2.id, account3.id.toSessionId))
    execute(blocksDAO.create(account3.id, account1.id.toSessionId))

    val b4 = execute(db.run(quote(query[Blocks].filter(_.accountId == lift(account1.id)).filter(_.by == lift(account2.id)))))
    val b5 = execute(db.run(quote(query[Blocks].filter(_.accountId == lift(account2.id)).filter(_.by == lift(account3.id)))))
    val b6 = execute(db.run(quote(query[Blocks].filter(_.accountId == lift(account3.id)).filter(_.by == lift(account1.id)))))
    assert(b4.nonEmpty)
    assert(b5.nonEmpty)
    assert(b6.nonEmpty)

  }

  test("delete") {

    val account1 = createAccount("BlocksDAOSpec4")
    val account2 = createAccount("BlocksDAOSpec5")
    val account3 = createAccount("BlocksDAOSpec6")

    // create
    execute(blocksDAO.create(account1.id, account2.id.toSessionId))
    execute(blocksDAO.create(account2.id, account3.id.toSessionId))
    execute(blocksDAO.create(account3.id, account1.id.toSessionId))

    // delete
    execute(blocksDAO.delete(account1.id, account2.id.toSessionId))
    execute(blocksDAO.delete(account2.id, account3.id.toSessionId))
    execute(blocksDAO.delete(account3.id, account1.id.toSessionId))

    val d4 = execute(db.run(quote(query[Blocks].filter(_.accountId == lift(account1.id)).filter(_.by == lift(account2.id)))))
    val d5 = execute(db.run(quote(query[Blocks].filter(_.accountId == lift(account2.id)).filter(_.by == lift(account3.id)))))
    val d6 = execute(db.run(quote(query[Blocks].filter(_.accountId == lift(account3.id)).filter(_.by == lift(account1.id)))))
    assert(d4.isEmpty)
    assert(d5.isEmpty)
    assert(d6.isEmpty)

  }

  test("exist") {

    val account1 = createAccount("BlocksDAOSpec7")
    val account2 = createAccount("BlocksDAOSpec8")
    val account3 = createAccount("BlocksDAOSpec9")

    // create
    execute(blocksDAO.create(account1.id, account2.id.toSessionId))
    execute(blocksDAO.create(account2.id, account3.id.toSessionId))
    execute(blocksDAO.create(account3.id, account1.id.toSessionId))

    assert(execute(blocksDAO.exist(account1.id, account2.id.toSessionId)) == true)
    assert(execute(blocksDAO.exist(account1.id, account3.id.toSessionId)) == false)
    assert(execute(blocksDAO.exist(account2.id, account1.id.toSessionId)) == false)
    assert(execute(blocksDAO.exist(account2.id, account3.id.toSessionId)) == true)
    assert(execute(blocksDAO.exist(account3.id, account1.id.toSessionId)) == true)
    assert(execute(blocksDAO.exist(account3.id, account2.id.toSessionId)) == false)

    // delete
    execute(blocksDAO.delete(account1.id, account2.id.toSessionId))
    execute(blocksDAO.delete(account2.id, account3.id.toSessionId))
    execute(blocksDAO.delete(account3.id, account1.id.toSessionId))

    assert(execute(blocksDAO.exist(account1.id, account2.id.toSessionId)) == false)
    assert(execute(blocksDAO.exist(account1.id, account3.id.toSessionId)) == false)
    assert(execute(blocksDAO.exist(account2.id, account1.id.toSessionId)) == false)
    assert(execute(blocksDAO.exist(account2.id, account3.id.toSessionId)) == false)
    assert(execute(blocksDAO.exist(account3.id, account1.id.toSessionId)) == false)
    assert(execute(blocksDAO.exist(account3.id, account2.id.toSessionId)) == false)

    // create
    execute(blocksDAO.create(account1.id, account2.id.toSessionId))
    execute(blocksDAO.create(account2.id, account3.id.toSessionId))
    execute(blocksDAO.create(account3.id, account1.id.toSessionId))

    assert(execute(blocksDAO.exist(account1.id, account2.id.toSessionId)) == true)
    assert(execute(blocksDAO.exist(account1.id, account3.id.toSessionId)) == false)
    assert(execute(blocksDAO.exist(account2.id, account1.id.toSessionId)) == false)
    assert(execute(blocksDAO.exist(account2.id, account3.id.toSessionId)) == true)
    assert(execute(blocksDAO.exist(account3.id, account1.id.toSessionId)) == true)
    assert(execute(blocksDAO.exist(account3.id, account2.id.toSessionId)) == false)

  }

  test("find all") {

    val account1 = createAccount("BlocksDAOSpec9")
    val account2 = createAccount("BlocksDAOSpec10")
    val account3 = createAccount("BlocksDAOSpec11")
    val account4 = createAccount("BlocksDAOSpec12")
    val account5 = createAccount("BlocksDAOSpec13")
    val account6 = createAccount("BlocksDAOSpec14")
    val account7 = createAccount("BlocksDAOSpec15")
    val account8 = createAccount("BlocksDAOSpec16")
    val account9 = createAccount("BlocksDAOSpec17")

    // create
    execute(blocksDAO.create(account4.id, account2.id.toSessionId))
    execute(blocksDAO.create(account4.id, account3.id.toSessionId))

    execute(blocksDAO.create(account2.id, account1.id.toSessionId))
    execute(blocksDAO.create(account3.id, account1.id.toSessionId))
    execute(blocksDAO.create(account4.id, account1.id.toSessionId))
    execute(blocksDAO.create(account5.id, account1.id.toSessionId))
    execute(blocksDAO.create(account6.id, account1.id.toSessionId))
    execute(blocksDAO.create(account7.id, account1.id.toSessionId))
    execute(blocksDAO.create(account8.id, account1.id.toSessionId))
    execute(blocksDAO.create(account9.id, account1.id.toSessionId))

    // find
    val result1 = execute(blocksDAO.find(None, 0, 3, account1.id.toSessionId))
    assert(result1.size == 3)
    val blockedAccount1 = result1(0)
    val blockedAccount2 = result1(1)
    val blockedAccount3 = result1(2)
    val next3 = result1(2).next
    assert(blockedAccount1.id == account9.id)
    assert(blockedAccount2.id == account8.id)
    assert(blockedAccount3.id == account7.id)

    val result2 = execute(blocksDAO.find(next3, 0, 3, account1.id.toSessionId))
    assert(result2.size == 3)
    val blockedAccount4 = result2(0)
    val blockedAccount5 = result2(1)
    val blockedAccount6 = result2(2)
    assert(blockedAccount4.id == account6.id)
    assert(blockedAccount5.id == account5.id)
    assert(blockedAccount6.id == account4.id)
    val next6 = result2(2).next

    val result3 = execute(blocksDAO.find(next6, 0, 3, account1.id.toSessionId))
    assert(result3.size == 2)
    val blockedAccount7 = result3(0)
    val blockedAccount8 = result3(1)
    assert(blockedAccount7.id == account3.id)
    assert(blockedAccount8.id == account2.id)

  }

}
