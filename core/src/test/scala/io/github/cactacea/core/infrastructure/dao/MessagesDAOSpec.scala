package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{DeviceType, GroupAuthorityType, GroupPrivacyType, MediumType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.MessageId
import io.github.cactacea.backend.core.infrastructure.models.Messages

class MessagesDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("MediumsDAOSpec4")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))
    val messageId = execute(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))

    val result = execute(db.run(quote(query[Messages].filter(_.groupId == lift(groupId)))))
    assert(result.size == 1)
    val message = result(0)
    assert(message.id == messageId)
    assert(message.message == Some("new message"))
    assert(message.accountCount == 0L)
    assert(message.contentWarning == false)
    assert(message.notified == false)

    val mediumId2 = execute(mediumsDAO.create("key1", "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg"), MediumType.image, 1, 4, 100L, sessionAccount.id.toSessionId))
    execute(messagesDAO.create(groupId, None, Some(mediumId2), sessionAccount.id.toSessionId))
    val result2 = execute(messagesDAO.find(messageId))
    assert(result2.isDefined)

  }

  test("delete") {

    val sessionAccount = createAccount("MediumsDAOSpec5")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))
    execute(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))

    execute(messagesDAO.delete(groupId))
    val result = execute(db.run(quote(query[Messages].filter(_.groupId == lift(groupId)))))
    assert(result.size == 0)
  }

  test("updateReadStatus") {

    val sessionAccount = createAccount("MediumsDAOSpec6")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))
    val messageId = execute(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))
    // TODO : check
    execute(messagesDAO.updateReadAccountCount(List(messageId)))

    val result2 = execute(db.run(quote(query[Messages].filter(_.groupId == lift(groupId)))))
    assert(result2.size == 1)
    val message = result2(0)
    assert(message.readAccountCount == 1)

  }


  test("findPushNotifications - direct message") {

    val sessionAccount = createAccount("PushNotificationsDAOSPec1")
    val account1 = createAccount("PushNotificationsDAOSPec2")

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    val groupId = execute(groupsDAO.create(sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val messageId = execute(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, account1.id.toSessionId))
    execute(pushNotificationSettingDAO.create(false, false, false, true, false, false, false, account1.id.toSessionId))
    execute(devicesDAO.create(udid, DeviceType.ios, None, account1.id.toSessionId))
    execute(devicesDAO.update(udid, pushToken, account1.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount.id, displayName, account1.id.toSessionId))

    val result1 = execute(messagesDAO.findPushNotifications(messageId))
    assert(result1.size == 1L)

    val result2 = execute(messagesDAO.findPushNotifications(MessageId(0L)))
    assert(result2.size == 0L)

  }

  test("findPushNotifications - group message") {

    val sessionAccount = createAccount("PushNotificationsDAOSPec3")
    val account1 = createAccount("PushNotificationsDAOSPec4")

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    val groupId = execute(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val messageId = execute(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, account1.id.toSessionId))
    execute(pushNotificationSettingDAO.create(false, false, false, false, true, false, false, account1.id.toSessionId))
    execute(devicesDAO.create(udid, DeviceType.ios, None, account1.id.toSessionId))
    execute(devicesDAO.update(udid, pushToken, account1.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount.id, displayName, account1.id.toSessionId))

    val result1 = execute(messagesDAO.findPushNotifications(messageId))
    assert(result1.size == 1L)

    val result2 = execute(messagesDAO.findPushNotifications(MessageId(0L)))
    assert(result2.size == 0L)

  }


  test("updatePushNotifications") {

    val sessionAccount = createAccount("MediumsDAOSpec7")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone,
      GroupAuthorityType.member, sessionAccount.id.toSessionId))
    val messageId = execute(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))
    // TODO : Check
    execute(messagesDAO.updatePushNotifications(messageId))

    val result2 = execute(db.run(quote(query[Messages].filter(_.id == lift(messageId)))))
    assert(result2.size == 1)
    val message = result2(0)
    assert(message.notified == true)

  }

}
