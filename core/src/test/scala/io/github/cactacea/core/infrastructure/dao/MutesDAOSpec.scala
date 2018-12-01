package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Mutes

class MutesDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("MutesDAOSpec1")
    val mutedAccount1 = createAccount("MutesDAOSpec2")
    val mutedAccount2 = createAccount("MutesDAOSpec3")
    execute(mutesDAO.create(sessionAccount.id, mutedAccount1.id.toSessionId))
    execute(mutesDAO.create(sessionAccount.id, mutedAccount2.id.toSessionId))

    val result1 = execute(db.run(quote(query[Mutes].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount1.id)))))
    val result2 = execute(db.run(quote(query[Mutes].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount2.id)))))
    assert(result1.nonEmpty)
    assert(result2.nonEmpty)

    execute(mutesDAO.delete(sessionAccount.id, mutedAccount1.id.toSessionId))
    execute(mutesDAO.delete(sessionAccount.id, mutedAccount2.id.toSessionId))

    execute(mutesDAO.create(sessionAccount.id, mutedAccount1.id.toSessionId))
    execute(mutesDAO.create(sessionAccount.id, mutedAccount2.id.toSessionId))

    val result3 = execute(db.run(quote(query[Mutes].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount1.id)))))
    val result4 = execute(db.run(quote(query[Mutes].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(mutedAccount2.id)))))
    assert(result3.nonEmpty)
    assert(result4.nonEmpty)

  }

  test("delete") {

    val sessionAccount = createAccount("MutesDAOSpec4")
    val muteAccount1 = createAccount("MutesDAOSpec5")
    val muteAccount2 = createAccount("MutesDAOSpec6")
    execute(mutesDAO.create(sessionAccount.id, muteAccount1.id.toSessionId))
    execute(mutesDAO.create(sessionAccount.id, muteAccount2.id.toSessionId))
    execute(mutesDAO.delete(sessionAccount.id, muteAccount1.id.toSessionId))
    execute(mutesDAO.delete(sessionAccount.id, muteAccount2.id.toSessionId))
    val result1 = execute(db.run(quote(query[Mutes].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(muteAccount1.id)))))
    val result2 = execute(db.run(quote(query[Mutes].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(muteAccount2.id)))))
    assert(result1.isEmpty)
    assert(result2.isEmpty)

  }

  test("exist") {

    val sessionAccount = createAccount("MutesDAOSpec7")
    val mutedAccount1 = createAccount("MutesDAOSpec8")
    val mutedAccount2 = createAccount("MutesDAOSpec9")
    val mutedAccount3 = createAccount("MutesDAOSpec10")
    execute(mutesDAO.create(sessionAccount.id, mutedAccount1.id.toSessionId))
    execute(mutesDAO.create(sessionAccount.id, mutedAccount2.id.toSessionId))

    val result1 = execute(mutesDAO.exist(sessionAccount.id, mutedAccount1.id.toSessionId))
    val result2 = execute(mutesDAO.exist(sessionAccount.id, mutedAccount2.id.toSessionId))
    val result3 = execute(mutesDAO.exist(sessionAccount.id, mutedAccount3.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == false)

  }

  test("findAll") {

    val sessionAccount1 = createAccount("MutesDAOSpec11")
    val sessionAccount2 = createAccount("MutesDAOSpec12")
    val sessionAccount3 = createAccount("MutesDAOSpec13")
    val sessionAccount4 = createAccount("MutesDAOSpec14")
    val sessionAccount5 = createAccount("MutesDAOSpec15")
    val sessionAccount6 = createAccount("MutesDAOSpec16")
    val muteUser = createAccount("MutesDAOSpec17")

    execute(mutesDAO.create(sessionAccount1.id, muteUser.id.toSessionId))
    execute(mutesDAO.create(sessionAccount2.id, muteUser.id.toSessionId))
    execute(mutesDAO.create(sessionAccount3.id, muteUser.id.toSessionId))
    execute(mutesDAO.create(sessionAccount4.id, muteUser.id.toSessionId))
    execute(mutesDAO.create(sessionAccount5.id, muteUser.id.toSessionId))
    execute(mutesDAO.create(sessionAccount6.id, muteUser.id.toSessionId))

    val result1 = execute(mutesDAO.findAll(None, 0, 3, muteUser.id.toSessionId))
    assert(result1(0)._1.id == sessionAccount6.id)
    assert(result1(1)._1.id == sessionAccount5.id)
    assert(result1(2)._1.id == sessionAccount4.id)

    val result2 = execute(mutesDAO.findAll(Some(result1(2)._3.mutedAt), 0, 3, muteUser.id.toSessionId))
    assert(result2(0)._1.id == sessionAccount3.id)
    assert(result2(1)._1.id == sessionAccount2.id)
    assert(result2(2)._1.id == sessionAccount1.id)
  }

}
