package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.PushNotificationSettings

class PushNotificationSettingsDAOSpec extends DAOSpec {

  import db._

  val pushNotificationSettingsDAO: PushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]

  test("create") {

    val sessionAccount1 = createAccount("PushNotificationSettingsDAOSpec1")
    val sessionAccount2 = createAccount("PushNotificationSettingsDAOSpec2")
    val sessionAccount3 = createAccount("PushNotificationSettingsDAOSpec3")
    val sessionAccount4 = createAccount("PushNotificationSettingsDAOSpec4")
    val sessionAccount5 = createAccount("PushNotificationSettingsDAOSpec5")
    val sessionAccount6 = createAccount("PushNotificationSettingsDAOSpec6")

    // create settings
    Await.result(pushNotificationSettingsDAO.create(true, false, false,  false, false, false, sessionAccount1.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, true, false,  false, false, false, sessionAccount2.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, true,  false, false, false, sessionAccount3.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, false,  true, false, false, sessionAccount5.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, false,  false, false, true, sessionAccount6.id.toSessionId))
    val setting1 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount1.id))))).head
    val setting2 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount2.id))))).head
    val setting3 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount3.id))))).head
    val setting4 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount4.id))))).head
    val setting5 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount5.id))))).head
    val setting6 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount6.id))))).head
    assert((setting1.groupInvitation, setting1.followerFeed, setting1.feedComment, setting1.groupMessage, setting1.directMessage, setting1.showMessage) == (true, false, false, false, false, false))
    assert((setting2.groupInvitation, setting2.followerFeed, setting2.feedComment, setting2.groupMessage, setting2.directMessage, setting2.showMessage) == (false, true, false, false, false, false))
    assert((setting3.groupInvitation, setting3.followerFeed, setting3.feedComment, setting3.groupMessage, setting3.directMessage, setting3.showMessage) == (false, false, true, false, false, false))
    assert((setting4.groupInvitation, setting4.followerFeed, setting4.feedComment, setting4.groupMessage, setting4.directMessage, setting4.showMessage) == (false, false, false,false, false, false))
    assert((setting5.groupInvitation, setting5.followerFeed, setting5.feedComment, setting5.groupMessage, setting5.directMessage, setting5.showMessage) == (false, false, false, true, false, false))
    assert((setting6.groupInvitation, setting6.followerFeed, setting6.feedComment, setting6.groupMessage, setting6.directMessage, setting6.showMessage) == (false, false, false, false, false, true))

  }

  test("edit") {

    val sessionAccount1 = createAccount("PushNotificationSettingsDAOSpec7")
    val sessionAccount2 = createAccount("PushNotificationSettingsDAOSpec8")
    val sessionAccount3 = createAccount("PushNotificationSettingsDAOSpec9")
    val sessionAccount4 = createAccount("PushNotificationSettingsDAOSpec10")
    val sessionAccount5 = createAccount("PushNotificationSettingsDAOSpec11")
    val sessionAccount6 = createAccount("PushNotificationSettingsDAOSpec12")

    Await.result(pushNotificationSettingsDAO.create(true, false, false,  false, false, false, sessionAccount1.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, true, false,  false, false, false, sessionAccount2.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, true,  false, false, false, sessionAccount3.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, false,  true, false, false, sessionAccount5.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, false,  false, false, true, sessionAccount6.id.toSessionId))

    // edit settings
    Await.result(pushNotificationSettingsDAO.update(false, false, false,  true, true, false, sessionAccount1.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.update(false, false, false, false, true, false, sessionAccount2.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.update(false, true, false,  false, true, false, sessionAccount3.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.update(false, false, true,  false, true, false, sessionAccount4.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.update(true, false, false,  false, true, false, sessionAccount5.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.update(true, false, false,  false, false, true, sessionAccount6.id.toSessionId))
    val ed1 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount1.id))))).head
    val ed2 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount2.id))))).head
    val ed3 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount3.id))))).head
    val ed4 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount4.id))))).head
    val ed5 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount5.id))))).head
    val ed6 = Await.result(db.run(quote(query[PushNotificationSettings].filter(_.accountId == lift(sessionAccount6.id))))).head
    assert((ed1.groupInvitation, ed1.followerFeed, ed1.feedComment, ed1.groupMessage, ed1.directMessage, ed1.showMessage) == (false, false, false, true, true, false))
    assert((ed2.groupInvitation, ed2.followerFeed, ed2.feedComment, ed2.groupMessage, ed2.directMessage, ed2.showMessage) == (false, false, false, false, true, false))
    assert((ed3.groupInvitation, ed3.followerFeed, ed3.feedComment, ed3.groupMessage, ed3.directMessage, ed3.showMessage) == (false, true, false, false, true, false))
    assert((ed4.groupInvitation, ed4.followerFeed, ed4.feedComment, ed4.groupMessage, ed4.directMessage, ed4.showMessage) == (false, false, true, false, true, false))
    assert((ed5.groupInvitation, ed5.followerFeed, ed5.feedComment, ed5.groupMessage, ed5.directMessage, ed5.showMessage) == (true, false, false, false, true, false))
    assert((ed6.groupInvitation, ed6.followerFeed, ed6.feedComment, ed6.groupMessage, ed6.directMessage, ed6.showMessage) == (true, false, false, false, false, true))

  }

  test("find") {

    val sessionAccount1 = createAccount("PushNotificationSettingsDAOSpec13")
    val sessionAccount2 = createAccount("PushNotificationSettingsDAOSpec14")
    val sessionAccount3 = createAccount("PushNotificationSettingsDAOSpec15")
    val sessionAccount4 = createAccount("PushNotificationSettingsDAOSpec16")
    val sessionAccount5 = createAccount("PushNotificationSettingsDAOSpec17")
    val sessionAccount6 = createAccount("PushNotificationSettingsDAOSpec18")

    Await.result(pushNotificationSettingsDAO.create(true, false, false,  false, false, false, sessionAccount1.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, true, false,  false, false, false, sessionAccount2.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, true,  false, false, false, sessionAccount3.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, false,  true, false, false, sessionAccount5.id.toSessionId))
    Await.result(pushNotificationSettingsDAO.create(false, false, false,  false, false, true, sessionAccount6.id.toSessionId))

    // find settings
    val fd1 = Await.result(pushNotificationSettingsDAO.find(sessionAccount1.id.toSessionId)).head
    val fd2 = Await.result(pushNotificationSettingsDAO.find(sessionAccount2.id.toSessionId)).head
    val fd3 = Await.result(pushNotificationSettingsDAO.find(sessionAccount3.id.toSessionId)).head
    val fd4 = Await.result(pushNotificationSettingsDAO.find(sessionAccount4.id.toSessionId)).head
    val fd5 = Await.result(pushNotificationSettingsDAO.find(sessionAccount5.id.toSessionId)).head
    val fd6 = Await.result(pushNotificationSettingsDAO.find(sessionAccount6.id.toSessionId)).head
    val fd7 = Await.result(pushNotificationSettingsDAO.find(SessionId(0L)))

    assert((fd1.groupInvitation, fd1.followerFeed, fd1.feedComment, fd1.groupMessage, fd1.directMessage, fd1.showMessage) == (true, false, false, false, false, false))
    assert((fd2.groupInvitation, fd2.followerFeed, fd2.feedComment, fd2.groupMessage, fd2.directMessage, fd2.showMessage) == (false, true, false, false, false, false))
    assert((fd3.groupInvitation, fd3.followerFeed, fd3.feedComment, fd3.groupMessage, fd3.directMessage, fd3.showMessage) == (false, false, true, false, false, false))
    assert((fd4.groupInvitation, fd4.followerFeed, fd4.feedComment, fd4.groupMessage, fd4.directMessage, fd4.showMessage) == (false, false, false, false, false, false))
    assert((fd5.groupInvitation, fd5.followerFeed, fd5.feedComment, fd5.groupMessage, fd5.directMessage, fd5.showMessage) == (false, false, false, true, false, false))
    assert((fd6.groupInvitation, fd6.followerFeed, fd6.feedComment, fd6.groupMessage, fd6.directMessage, fd6.showMessage) == (false, false, false, false, false, true))

    assert(fd7.isEmpty)
  }

}
