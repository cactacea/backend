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

    // create settings
    execute(pushNotificationSettingsDAO.create(true, false, false,  false, false, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, true, false,  false, false, false, sessionAccount2.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, true,  false, false, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false,  true, false, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false,  false, false, true, sessionAccount6.id.toSessionId))
    val setting1 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount1.id))))).head
    val setting2 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount2.id))))).head
    val setting3 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount3.id))))).head
    val setting4 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount4.id))))).head
    val setting5 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount5.id))))).head
    val setting6 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount6.id))))).head

    assert(setting1.groupInvitation == true)
    assert(setting2.groupInvitation == false)
    assert(setting3.groupInvitation == false)
    assert(setting4.groupInvitation == false)
    assert(setting5.groupInvitation == false)
    assert(setting6.groupInvitation == false)

    assert(setting1.followerFeed == false)
    assert(setting2.followerFeed == true)
    assert(setting3.followerFeed == false)
    assert(setting4.followerFeed == false)
    assert(setting5.followerFeed == false)
    assert(setting6.followerFeed == false)

    assert(setting1.feedComment == false)
    assert(setting2.feedComment == false)
    assert(setting3.feedComment == true)
    assert(setting4.feedComment == false)
    assert(setting5.feedComment == false)
    assert(setting6.feedComment == false)

    assert(setting1.groupMessage == false)
    assert(setting2.groupMessage == false)
    assert(setting3.groupMessage == false)
    assert(setting4.groupMessage == false)
    assert(setting5.groupMessage == true)
    assert(setting6.groupMessage == false)

    assert(setting1.directMessage == false)
    assert(setting2.directMessage == false)
    assert(setting3.directMessage == false)
    assert(setting4.directMessage == false)
    assert(setting5.directMessage == false)
    assert(setting6.directMessage == false)

    assert(setting1.showMessage == false)
    assert(setting2.showMessage == false)
    assert(setting3.showMessage == false)
    assert(setting4.showMessage == false)
    assert(setting5.showMessage == false)
    assert(setting6.showMessage == true)

  }

  test("edit") {

    val sessionAccount1 = createAccount("PushNotificationSettingsDAOSpec7")
    val sessionAccount2 = createAccount("PushNotificationSettingsDAOSpec8")
    val sessionAccount3 = createAccount("PushNotificationSettingsDAOSpec9")
    val sessionAccount4 = createAccount("PushNotificationSettingsDAOSpec10")
    val sessionAccount5 = createAccount("PushNotificationSettingsDAOSpec11")
    val sessionAccount6 = createAccount("PushNotificationSettingsDAOSpec12")

    execute(pushNotificationSettingsDAO.create(true, false, false,  false, false, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, true, false,  false, false, false, sessionAccount2.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, true,  false, false, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false,  true, false, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false,  false, false, true, sessionAccount6.id.toSessionId))

    // edit settings
    execute(pushNotificationSettingsDAO.update(false, false, false,  true, true, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(false, false, false, false, true, false, sessionAccount2.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(false, true, false,  false, true, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(false, false, true,  false, true, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(true, false, false,  false, true, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingsDAO.update(true, false, false,  false, false, true, sessionAccount6.id.toSessionId))
    val ed1 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount1.id))))).head
    val ed2 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount2.id))))).head
    val ed3 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount3.id))))).head
    val ed4 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount4.id))))).head
    val ed5 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount5.id))))).head
    val ed6 = execute(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount6.id))))).head

    assert(ed1.groupInvitation == false)
    assert(ed2.groupInvitation == false)
    assert(ed3.groupInvitation == false)
    assert(ed4.groupInvitation == false)
    assert(ed5.groupInvitation == true)
    assert(ed6.groupInvitation == true)

    assert(ed1.followerFeed ==  false)
    assert(ed2.followerFeed ==  false)
    assert(ed3.followerFeed ==  true)
    assert(ed4.followerFeed ==  false)
    assert(ed5.followerFeed == false)
    assert(ed6.followerFeed == false)

    assert(ed1.feedComment ==  false)
    assert(ed2.feedComment ==  false)
    assert(ed3.feedComment == false)
    assert(ed4.feedComment ==  true)
    assert(ed5.feedComment == false)
    assert(ed6.feedComment == false)

    assert(ed1.groupMessage ==  true)
    assert(ed2.groupMessage ==  false)
    assert(ed3.groupMessage == false)
    assert(ed4.groupMessage == false)
    assert(ed5.groupMessage ==  false)
    assert(ed6.groupMessage ==  false)

    assert(ed1.directMessage == true)
    assert(ed2.directMessage ==  true)
    assert(ed3.directMessage == true)
    assert(ed4.directMessage == true)
    assert(ed5.directMessage ==  true)
    assert(ed6.directMessage ==  false)

    assert(ed1.showMessage == false)
    assert(ed2.showMessage == false)
    assert(ed3.showMessage == false)
    assert(ed4.showMessage == false)
    assert(ed5.showMessage == false)
    assert(ed6.showMessage == true)

  }

  test("find") {

    val sessionAccount1 = createAccount("PushNotificationSettingsDAOSpec13")
    val sessionAccount2 = createAccount("PushNotificationSettingsDAOSpec14")
    val sessionAccount3 = createAccount("PushNotificationSettingsDAOSpec15")
    val sessionAccount4 = createAccount("PushNotificationSettingsDAOSpec16")
    val sessionAccount5 = createAccount("PushNotificationSettingsDAOSpec17")
    val sessionAccount6 = createAccount("PushNotificationSettingsDAOSpec18")

    execute(pushNotificationSettingsDAO.create(true, false, false,  false, false, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, true, false,  false, false, false, sessionAccount2.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, true,  false, false, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false,  true, false, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingsDAO.create(false, false, false,  false, false, true, sessionAccount6.id.toSessionId))

    // find settings
    val fd1 = execute(pushNotificationSettingsDAO.find(sessionAccount1.id.toSessionId)).head
    val fd2 = execute(pushNotificationSettingsDAO.find(sessionAccount2.id.toSessionId)).head
    val fd3 = execute(pushNotificationSettingsDAO.find(sessionAccount3.id.toSessionId)).head
    val fd4 = execute(pushNotificationSettingsDAO.find(sessionAccount4.id.toSessionId)).head
    val fd5 = execute(pushNotificationSettingsDAO.find(sessionAccount5.id.toSessionId)).head
    val fd6 = execute(pushNotificationSettingsDAO.find(sessionAccount6.id.toSessionId)).head
    val fd7 = execute(pushNotificationSettingsDAO.find(SessionId(0L)))

    assert(fd1.groupInvitation == true)
    assert(fd2.groupInvitation == false)
    assert(fd3.groupInvitation == false)
    assert(fd4.groupInvitation == false)
    assert(fd5.groupInvitation == false)
    assert(fd6.groupInvitation == false)

    assert(fd1.followerFeed == false)
    assert(fd2.followerFeed == true)
    assert(fd3.followerFeed == false)
    assert(fd4.followerFeed == false)
    assert(fd5.followerFeed == false)
    assert(fd6.followerFeed == false)

    assert(fd1.feedComment == false)
    assert(fd2.feedComment == false)
    assert(fd3.feedComment == true)
    assert(fd4.feedComment == false)
    assert(fd5.feedComment == false)
    assert(fd6.feedComment == false)

    assert(fd1.groupMessage == false)
    assert(fd2.groupMessage == false)
    assert(fd3.groupMessage == false)
    assert(fd4.groupMessage == false)
    assert(fd5.groupMessage == true)
    assert(fd6.groupMessage == false)

    assert(fd1.directMessage == false)
    assert(fd2.directMessage == false)
    assert(fd3.directMessage == false)
    assert(fd4.directMessage == false)
    assert(fd5.directMessage == false)
    assert(fd6.directMessage == false)

    assert(fd1.showMessage == false)
    assert(fd2.showMessage == false)
    assert(fd3.showMessage == false)
    assert(fd4.showMessage == false)
    assert(fd5.showMessage == false)
    assert(fd6.showMessage == true)

    assert(fd7.isEmpty)
  }

}
