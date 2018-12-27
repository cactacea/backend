package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.PushNotificationSettings

class PushNotificationSettingsDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount1 = createAccount("PushNotificationSettingsDAOSpec1")
    val sessionAccount2 = createAccount("PushNotificationSettingsDAOSpec2")
    val sessionAccount3 = createAccount("PushNotificationSettingsDAOSpec3")
    val sessionAccount4 = createAccount("PushNotificationSettingsDAOSpec4")
    val sessionAccount5 = createAccount("PushNotificationSettingsDAOSpec5")
    val sessionAccount6 = createAccount("PushNotificationSettingsDAOSpec6")
    val sessionAccount7 = createAccount("PushNotificationSettingsDAOSpec7")

    // create settings
    execute(pushNotificationSettingsDAO.create(true, false, false, false, false, false, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, true, false, false, false, false, false, sessionAccount2.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, true, false, false, false, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, true, false, false, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, true, false, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, true, false, sessionAccount6.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, true, sessionAccount7.id.toSessionId))
    val setting1 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount1.id))))).headOption
    val setting2 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount2.id))))).headOption
    val setting3 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount3.id))))).headOption
    val setting4 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount4.id))))).headOption
    val setting5 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount5.id))))).headOption
    val setting6 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount6.id))))).headOption
    val setting7 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount7.id))))).headOption

    assert(setting1.map(_.feed).contains(true))
    assert(setting1.map(_.comment).contains(false))
    assert(setting1.map(_.friendRequest).contains(false))
    assert(setting1.map(_.message).contains(false))
    assert(setting1.map(_.groupMessage).contains(false))
    assert(setting1.map(_.groupInvitation).contains(false))
    assert(setting1.map(_.showMessage).contains(false))

    assert(setting2.map(_.feed).contains(false))
    assert(setting2.map(_.comment).contains(true))
    assert(setting2.map(_.friendRequest).contains(false))
    assert(setting2.map(_.message).contains(false))
    assert(setting2.map(_.groupMessage).contains(false))
    assert(setting2.map(_.groupInvitation).contains(false))
    assert(setting2.map(_.showMessage).contains(false))

    assert(setting3.map(_.feed).contains(false))
    assert(setting3.map(_.comment).contains(false))
    assert(setting3.map(_.friendRequest).contains(true))
    assert(setting3.map(_.message).contains(false))
    assert(setting3.map(_.groupMessage).contains(false))
    assert(setting3.map(_.groupInvitation).contains(false))
    assert(setting3.map(_.showMessage).contains(false))

    assert(setting4.map(_.feed).contains(false))
    assert(setting4.map(_.comment).contains(false))
    assert(setting4.map(_.friendRequest).contains(false))
    assert(setting4.map(_.message).contains(true))
    assert(setting4.map(_.groupMessage).contains(false))
    assert(setting4.map(_.groupInvitation).contains(false))
    assert(setting4.map(_.showMessage).contains(false))

    assert(setting5.map(_.feed).contains(false))
    assert(setting5.map(_.comment).contains(false))
    assert(setting5.map(_.friendRequest).contains(false))
    assert(setting5.map(_.message).contains(false))
    assert(setting5.map(_.groupMessage).contains(true))
    assert(setting5.map(_.groupInvitation).contains(false))
    assert(setting5.map(_.showMessage).contains(false))

    assert(setting6.map(_.feed).contains(false))
    assert(setting6.map(_.comment).contains(false))
    assert(setting6.map(_.friendRequest).contains(false))
    assert(setting6.map(_.message).contains(false))
    assert(setting6.map(_.groupMessage).contains(false))
    assert(setting6.map(_.groupInvitation).contains(true))
    assert(setting6.map(_.showMessage).contains(false))

    assert(setting7.map(_.feed).contains(false))
    assert(setting7.map(_.comment).contains(false))
    assert(setting7.map(_.friendRequest).contains(false))
    assert(setting7.map(_.message).contains(false))
    assert(setting7.map(_.groupMessage).contains(false))
    assert(setting7.map(_.groupInvitation).contains(false))
    assert(setting7.map(_.showMessage).contains(true))




  }

  test("edit") {

    val sessionAccount1 = createAccount("PushNotificationSettingsDAOSpec8")
    val sessionAccount2 = createAccount("PushNotificationSettingsDAOSpec9")
    val sessionAccount3 = createAccount("PushNotificationSettingsDAOSpec10")
    val sessionAccount4 = createAccount("PushNotificationSettingsDAOSpec11")
    val sessionAccount5 = createAccount("PushNotificationSettingsDAOSpec12")
    val sessionAccount6 = createAccount("PushNotificationSettingsDAOSpec13")
    val sessionAccount7 = createAccount("PushNotificationSettingsDAOSpec14")

    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, false, sessionAccount2.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, false, sessionAccount6.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, false, sessionAccount7.id.toSessionId))

    // edit settings
    execute(pushNotificationSettingsDAO.update(true , false, false, false, false, false, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(false, true , false, false, false, false, false, sessionAccount2.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(false, false, true , false, false, false, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(false, false, false, true , false, false, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(false, false, false, false, true , false, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(false, false, false, false, false, true , false, sessionAccount6.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(false, false, false, false, false, false , true, sessionAccount7.id.toSessionId))
    val setting1 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount1.id))))).headOption
    val setting2 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount2.id))))).headOption
    val setting3 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount3.id))))).headOption
    val setting4 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount4.id))))).headOption
    val setting5 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount5.id))))).headOption
    val setting6 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount6.id))))).headOption
    val setting7 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount7.id))))).headOption

    assert(setting1.map(_.feed).contains(true))
    assert(setting1.map(_.comment).contains(false))
    assert(setting1.map(_.friendRequest).contains(false))
    assert(setting1.map(_.message).contains(false))
    assert(setting1.map(_.groupMessage).contains(false))
    assert(setting1.map(_.groupInvitation).contains(false))
    assert(setting1.map(_.showMessage).contains(false))

    assert(setting2.map(_.feed).contains(false))
    assert(setting2.map(_.comment).contains(true))
    assert(setting2.map(_.friendRequest).contains(false))
    assert(setting2.map(_.message).contains(false))
    assert(setting2.map(_.groupMessage).contains(false))
    assert(setting2.map(_.groupInvitation).contains(false))
    assert(setting2.map(_.showMessage).contains(false))

    assert(setting3.map(_.feed).contains(false))
    assert(setting3.map(_.comment).contains(false))
    assert(setting3.map(_.friendRequest).contains(true))
    assert(setting3.map(_.message).contains(false))
    assert(setting3.map(_.groupMessage).contains(false))
    assert(setting3.map(_.groupInvitation).contains(false))
    assert(setting3.map(_.showMessage).contains(false))

    assert(setting4.map(_.feed).contains(false))
    assert(setting4.map(_.comment).contains(false))
    assert(setting4.map(_.friendRequest).contains(false))
    assert(setting4.map(_.message).contains(true))
    assert(setting4.map(_.groupMessage).contains(false))
    assert(setting4.map(_.groupInvitation).contains(false))
    assert(setting4.map(_.showMessage).contains(false))

    assert(setting5.map(_.feed).contains(false))
    assert(setting5.map(_.comment).contains(false))
    assert(setting5.map(_.friendRequest).contains(false))
    assert(setting5.map(_.message).contains(false))
    assert(setting5.map(_.groupMessage).contains(true))
    assert(setting5.map(_.groupInvitation).contains(false))
    assert(setting5.map(_.showMessage).contains(false))

    assert(setting6.map(_.feed).contains(false))
    assert(setting6.map(_.comment).contains(false))
    assert(setting6.map(_.friendRequest).contains(false))
    assert(setting6.map(_.message).contains(false))
    assert(setting6.map(_.groupMessage).contains(false))
    assert(setting6.map(_.groupInvitation).contains(true))
    assert(setting6.map(_.showMessage).contains(false))

    assert(setting7.map(_.feed).contains(false))
    assert(setting7.map(_.comment).contains(false))
    assert(setting7.map(_.friendRequest).contains(false))
    assert(setting7.map(_.message).contains(false))
    assert(setting7.map(_.groupMessage).contains(false))
    assert(setting7.map(_.groupInvitation).contains(false))
    assert(setting7.map(_.showMessage).contains(true))

  }

  test("find") {

    val sessionAccount1 = createAccount("PushNotificationSettingsDAOSpec15")
    val sessionAccount2 = createAccount("PushNotificationSettingsDAOSpec16")
    val sessionAccount3 = createAccount("PushNotificationSettingsDAOSpec17")
    val sessionAccount4 = createAccount("PushNotificationSettingsDAOSpec18")
    val sessionAccount5 = createAccount("PushNotificationSettingsDAOSpec19")
    val sessionAccount6 = createAccount("PushNotificationSettingsDAOSpec20")
    val sessionAccount7 = createAccount("PushNotificationSettingsDAOSpec21")

    // create settings
    execute(pushNotificationSettingsDAO.create(true, false, false, false, false, false, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, true, false, false, false, false, false, sessionAccount2.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, true, false, false, false, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, true, false, false, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, true, false, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, true, false, sessionAccount6.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, true, sessionAccount7.id.toSessionId))

    // find settings
    val setting1 = execute(pushNotificationSettingsDAO.find(sessionAccount1.id.toSessionId)).headOption
    val setting2 = execute(pushNotificationSettingsDAO.find(sessionAccount2.id.toSessionId)).headOption
    val setting3 = execute(pushNotificationSettingsDAO.find(sessionAccount3.id.toSessionId)).headOption
    val setting4 = execute(pushNotificationSettingsDAO.find(sessionAccount4.id.toSessionId)).headOption
    val setting5 = execute(pushNotificationSettingsDAO.find(sessionAccount5.id.toSessionId)).headOption
    val setting6 = execute(pushNotificationSettingsDAO.find(sessionAccount6.id.toSessionId)).headOption
    val setting7 = execute(pushNotificationSettingsDAO.find(sessionAccount7.id.toSessionId)).headOption
    val setting8 = execute(pushNotificationSettingsDAO.find(SessionId(0L)))

    assert(setting1.map(_.feed).contains(true))
    assert(setting1.map(_.comment).contains(false))
    assert(setting1.map(_.friendRequest).contains(false))
    assert(setting1.map(_.message).contains(false))
    assert(setting1.map(_.groupMessage).contains(false))
    assert(setting1.map(_.groupInvitation).contains(false))
    assert(setting1.map(_.showMessage).contains(false))

    assert(setting2.map(_.feed).contains(false))
    assert(setting2.map(_.comment).contains(true))
    assert(setting2.map(_.friendRequest).contains(false))
    assert(setting2.map(_.message).contains(false))
    assert(setting2.map(_.groupMessage).contains(false))
    assert(setting2.map(_.groupInvitation).contains(false))
    assert(setting2.map(_.showMessage).contains(false))

    assert(setting3.map(_.feed).contains(false))
    assert(setting3.map(_.comment).contains(false))
    assert(setting3.map(_.friendRequest).contains(true))
    assert(setting3.map(_.message).contains(false))
    assert(setting3.map(_.groupMessage).contains(false))
    assert(setting3.map(_.groupInvitation).contains(false))
    assert(setting3.map(_.showMessage).contains(false))

    assert(setting4.map(_.feed).contains(false))
    assert(setting4.map(_.comment).contains(false))
    assert(setting4.map(_.friendRequest).contains(false))
    assert(setting4.map(_.message).contains(true))
    assert(setting4.map(_.groupMessage).contains(false))
    assert(setting4.map(_.groupInvitation).contains(false))
    assert(setting4.map(_.showMessage).contains(false))

    assert(setting5.map(_.feed).contains(false))
    assert(setting5.map(_.comment).contains(false))
    assert(setting5.map(_.friendRequest).contains(false))
    assert(setting5.map(_.message).contains(false))
    assert(setting5.map(_.groupMessage).contains(true))
    assert(setting5.map(_.groupInvitation).contains(false))
    assert(setting5.map(_.showMessage).contains(false))

    assert(setting6.map(_.feed).contains(false))
    assert(setting6.map(_.comment).contains(false))
    assert(setting6.map(_.friendRequest).contains(false))
    assert(setting6.map(_.message).contains(false))
    assert(setting6.map(_.groupMessage).contains(false))
    assert(setting6.map(_.groupInvitation).contains(true))
    assert(setting6.map(_.showMessage).contains(false))

    assert(setting7.map(_.feed).contains(false))
    assert(setting7.map(_.comment).contains(false))
    assert(setting7.map(_.friendRequest).contains(false))
    assert(setting7.map(_.message).contains(false))
    assert(setting7.map(_.groupMessage).contains(false))
    assert(setting7.map(_.groupInvitation).contains(false))
    assert(setting7.map(_.showMessage).contains(true))

    assert(setting8.isEmpty)
  }

}
