package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.Blocks

class BlockersDAOSpec extends DAOSpec {

  import db._

  val blockersDAO: BlockersDAO = injector.instance[BlockersDAO]

  test("create") {

    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")

    // create
    Await.result(blockersDAO.create(account1.id, account2.id.toSessionId))
    Await.result(blockersDAO.create(account2.id, account3.id.toSessionId))
    Await.result(blockersDAO.create(account3.id, account1.id.toSessionId))
    val b1 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account1.id)).filter(_.by == lift(account2.id))))).head
    val b2 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account2.id)).filter(_.by == lift(account3.id))))).head
    val b3 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account3.id)).filter(_.by == lift(account1.id))))).head
    assert((b1.blocked, b1.beingBlocked) == (false, true))
    assert((b2.blocked, b2.beingBlocked) == (false, true))
    assert((b3.blocked, b3.beingBlocked) == (false, true))

    // delete
    Await.result(blockersDAO.delete(account1.id, account2.id.toSessionId))
    Await.result(blockersDAO.delete(account2.id, account3.id.toSessionId))
    Await.result(blockersDAO.delete(account3.id, account1.id.toSessionId))

    // create
    Await.result(blockersDAO.create(account1.id, account2.id.toSessionId))
    Await.result(blockersDAO.create(account2.id, account3.id.toSessionId))
    Await.result(blockersDAO.create(account3.id, account1.id.toSessionId))
    val b4 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account1.id)).filter(_.by == lift(account2.id))))).head
    val b5 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account2.id)).filter(_.by == lift(account3.id))))).head
    val b6 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account3.id)).filter(_.by == lift(account1.id))))).head
    assert((b4.blocked, b4.beingBlocked) == (false, true))
    assert((b5.blocked, b5.beingBlocked) == (false, true))
    assert((b6.blocked, b6.beingBlocked) == (false, true))

  }

  test("delete") {

    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")

    // create
    Await.result(blockersDAO.create(account1.id, account2.id.toSessionId))
    Await.result(blockersDAO.create(account2.id, account3.id.toSessionId))
    Await.result(blockersDAO.create(account3.id, account1.id.toSessionId))

    // delete
    Await.result(blockersDAO.delete(account1.id, account2.id.toSessionId))
    Await.result(blockersDAO.delete(account2.id, account3.id.toSessionId))
    Await.result(blockersDAO.delete(account3.id, account1.id.toSessionId))

    val d4 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account1.id)).filter(_.by == lift(account2.id))))).head
    val d5 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account2.id)).filter(_.by == lift(account3.id))))).head
    val d6 = Await.result(db.run(quote(query[Blocks].filter(_.accountId == lift(account3.id)).filter(_.by == lift(account1.id))))).head
    assert((d4.blocked, d4.beingBlocked) == (false, false))
    assert((d5.blocked, d5.beingBlocked) == (false, false))
    assert((d6.blocked, d6.beingBlocked) == (false, false))

  }

}
