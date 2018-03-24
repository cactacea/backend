package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class MutesDAOSpec extends DAOSpec {

  val mutesDAO: MutesDAO = injector.instance[MutesDAO]

  import db._

  test("create") {

    val sessionAccount = createAccount("account0")
    val mutedAccount1 = createAccount("account1")
    val mutedAccount2 = createAccount("account2")
    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount1.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount2.id.toSessionId))

    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount2.id))))).head
    assert(result1.mute == true)
    assert(result2.mute == true)

    Await.result(mutesDAO.delete(sessionAccount.id, mutedAccount1.id.toSessionId))
    Await.result(mutesDAO.delete(sessionAccount.id, mutedAccount2.id.toSessionId))

    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount1.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount2.id.toSessionId))

    val result3 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount1.id))))).head
    val result4 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount2.id))))).head
    assert(result3.mute == true)
    assert(result4.mute == true)

  }

  test("delete") {

    val sessionAccount = createAccount("account0")
    val muteAccount1 = createAccount("account1")
    val muteAccount2 = createAccount("account2")
    Await.result(mutesDAO.create(sessionAccount.id, muteAccount1.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount.id, muteAccount2.id.toSessionId))
    Await.result(mutesDAO.delete(sessionAccount.id, muteAccount1.id.toSessionId))
    Await.result(mutesDAO.delete(sessionAccount.id, muteAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(muteAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(muteAccount2.id))))).head
    assert(result1.mute == false)
    assert(result2.mute == false)

  }

  test("exist") {

    val sessionAccount = createAccount("account0")
    val mutedAccount1 = createAccount("account1")
    val mutedAccount2 = createAccount("account2")
    val mutedAccount3 = createAccount("account3")
    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount1.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount2.id.toSessionId))

    val result1 = Await.result(mutesDAO.exist(sessionAccount.id, mutedAccount1.id.toSessionId))
    val result2 = Await.result(mutesDAO.exist(sessionAccount.id, mutedAccount2.id.toSessionId))
    val result3 = Await.result(mutesDAO.exist(sessionAccount.id, mutedAccount3.id.toSessionId))
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
    val muteUser = createAccount("account6")

    Await.result(mutesDAO.create(sessionAccount1.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount2.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount3.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount4.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount5.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount6.id, muteUser.id.toSessionId))

    val result1 = Await.result(mutesDAO.findAll(None, None, Some(3), muteUser.id.toSessionId))
    assert(result1(0)._1.id == sessionAccount6.id)
    assert(result1(1)._1.id == sessionAccount5.id)
    assert(result1(2)._1.id == sessionAccount4.id)

    val result2 = Await.result(mutesDAO.findAll(Some(result1(2)._3), None, Some(3), muteUser.id.toSessionId))
    assert(result2(0)._1.id == sessionAccount3.id)
    assert(result2(1)._1.id == sessionAccount2.id)
    assert(result2(2)._1.id == sessionAccount1.id)
  }

}
