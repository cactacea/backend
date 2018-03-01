package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec
import io.jsonwebtoken.impl.TextCodec
import io.jsonwebtoken.impl.crypto.MacProvider
import io.github.cactacea.core.infrastructure.models.Relationships

class MutesDAOSpec extends DAOSpec {

  val mutesDAO: MutesDAO = injector.instance[MutesDAO]

  import db._

  test("create") {

    val sessionAccount = this.createAccount(0L)
    val mutedAccount1 = this.createAccount(1L)
    val mutedAccount2 = this.createAccount(2L)
    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount1.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount2.id.toSessionId))

    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount2.id))))).head
    assert(result1.muted == true)
    assert(result2.muted == true)

    Await.result(mutesDAO.delete(sessionAccount.id, mutedAccount1.id.toSessionId))
    Await.result(mutesDAO.delete(sessionAccount.id, mutedAccount2.id.toSessionId))

    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount1.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount.id, mutedAccount2.id.toSessionId))

    val result3 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount1.id))))).head
    val result4 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount2.id))))).head
    assert(result3.muted == true)
    assert(result4.muted == true)

  }

  test("delete") {

    val sessionAccount = this.createAccount(0L)
    val muteAccount1 = this.createAccount(1L)
    val muteAccount2 = this.createAccount(2L)
    Await.result(mutesDAO.create(sessionAccount.id, muteAccount1.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount.id, muteAccount2.id.toSessionId))
    Await.result(mutesDAO.delete(sessionAccount.id, muteAccount1.id.toSessionId))
    Await.result(mutesDAO.delete(sessionAccount.id, muteAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(muteAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(muteAccount2.id))))).head
    assert(result1.muted == false)
    assert(result2.muted == false)

  }

  test("exist") {

    val sessionAccount = this.createAccount(0L)
    val mutedAccount1 = this.createAccount(1L)
    val mutedAccount2 = this.createAccount(2L)
    val mutedAccount3 = this.createAccount(3L)
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

    val sessionAccount1 = this.createAccount(0L)
    val sessionAccount2 = this.createAccount(1L)
    val sessionAccount3 = this.createAccount(2L)
    val sessionAccount4 = this.createAccount(3L)
    val sessionAccount5 = this.createAccount(4L)
    val sessionAccount6 = this.createAccount(5L)
    val muteUser = this.createAccount(6L)

    Await.result(mutesDAO.create(sessionAccount1.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount2.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount3.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount4.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount5.id, muteUser.id.toSessionId))
    Await.result(mutesDAO.create(sessionAccount6.id, muteUser.id.toSessionId))

    val result1 = Await.result(mutesDAO.findAll(muteUser.id, None, None, Some(3), sessionAccount1.id.toSessionId))
    assert(result1(0)._1.id == sessionAccount6.id)
    assert(result1(1)._1.id == sessionAccount5.id)
    assert(result1(2)._1.id == sessionAccount4.id)

    val result2 = Await.result(mutesDAO.findAll(muteUser.id, Some(result1(2)._1.position), None, Some(3), sessionAccount1.id.toSessionId))
    assert(result2(0)._1.id == sessionAccount3.id)
    assert(result2(1)._1.id == sessionAccount2.id)
    assert(result2(2)._1.id == sessionAccount1.id)
  }

}
