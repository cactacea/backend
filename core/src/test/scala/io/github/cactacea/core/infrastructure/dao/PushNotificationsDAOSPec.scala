package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{DeviceType, FeedPrivacyType, GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.MessageId

class PushNotificationsDAOSPec extends DAOSpec {

  test("findMessage - direct message") {

    val sessionAccount = createAccount("PushNotificationsDAOSPec1")
    val account1 = createAccount("PushNotificationsDAOSPec2")

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    val groupId = execute(groupsDAO.create(sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val messageId = execute(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, account1.id.toSessionId))
    execute(pushNotificationSettingDAO.create(false, false, false, false, true, false, account1.id.toSessionId))
    execute(devicesDAO.create(udid, DeviceType.ios, None, account1.id.toSessionId))
    execute(devicesDAO.update(udid, pushToken, account1.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount.id, displayName, account1.id.toSessionId))

    val result1 = execute(pushNotificationsDAO.findByMessageId(messageId))
    assert(result1.size == 1L)

    val result2 = execute(pushNotificationsDAO.findByMessageId(MessageId(0L)))
    assert(result2.size == 0L)

  }


  test("findMessage - group message") {

    val sessionAccount = createAccount("PushNotificationsDAOSPec3")
    val account1 = createAccount("PushNotificationsDAOSPec4")

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    val groupId = execute(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val messageId = execute(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, account1.id.toSessionId))
    execute(pushNotificationSettingDAO.create(false, false, false, true, false, false, account1.id.toSessionId))
    execute(devicesDAO.create(udid, DeviceType.ios, None, account1.id.toSessionId))
    execute(devicesDAO.update(udid, pushToken, account1.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount.id, displayName, account1.id.toSessionId))

    val result1 = execute(pushNotificationsDAO.findByMessageId(messageId))
    assert(result1.size == 1L)

    val result2 = execute(pushNotificationsDAO.findByMessageId(MessageId(0L)))
    assert(result2.size == 0L)

  }

  test("findFeeds") {

    val sessionAccount1 = createAccount("PushNotificationsDAOSPec5")
    val sessionAccount2 = createAccount("PushNotificationsDAOSPec6")
    val sessionAccount3 = createAccount("PushNotificationsDAOSPec7")
    val sessionAccount4 = createAccount("PushNotificationsDAOSPec8")
    val sessionAccount5 = createAccount("PushNotificationsDAOSPec9")
    val sessionAccount6 = createAccount("PushNotificationsDAOSPec10")
    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.everyone
    val contentWarning = true

    // create feed
    val feedId = execute(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, None, sessionAccount2.id.toSessionId))

    // create follows
    execute(followersDAO.create(sessionAccount2.id, sessionAccount1.id.toSessionId))
    execute(followersDAO.create(sessionAccount2.id, sessionAccount3.id.toSessionId))
    execute(followersDAO.create(sessionAccount2.id, sessionAccount4.id.toSessionId))
    execute(followersDAO.create(sessionAccount2.id, sessionAccount5.id.toSessionId))
    execute(followersDAO.create(sessionAccount2.id, sessionAccount6.id.toSessionId))

    // create account feeds
    execute(accountFeedsDAO.create(feedId, sessionAccount2.id.toSessionId))

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    execute(pushNotificationSettingDAO.create(false, true, false, false, false, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingDAO.create(false, true, false, false, false, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingDAO.create(false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingDAO.create(false, true, false, false, false, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingDAO.create(false, true, false, false, false, false, sessionAccount6.id.toSessionId))

    execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount1.id.toSessionId))
    execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount3.id.toSessionId))
    execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount4.id.toSessionId))
    execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount5.id.toSessionId))
    execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount6.id.toSessionId))

    execute(devicesDAO.update(udid, pushToken, sessionAccount1.id.toSessionId))
    execute(devicesDAO.update(udid, pushToken, sessionAccount3.id.toSessionId))
    execute(devicesDAO.update(udid, pushToken, sessionAccount4.id.toSessionId))
    execute(devicesDAO.update(udid, pushToken, sessionAccount5.id.toSessionId))
    execute(devicesDAO.update(udid, pushToken, sessionAccount6.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount1.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount3.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount4.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount5.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount6.id.toSessionId))

    // find account feed tokens
    val result = execute(pushNotificationsDAO.findByFeed(feedId))

    assert(result.size == 4)

  }
}
