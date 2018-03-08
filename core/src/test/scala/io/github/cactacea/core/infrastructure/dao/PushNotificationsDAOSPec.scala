package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{FeedPrivacyType, GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.identifiers.MessageId

class PushNotificationsDAOSPec extends DAOSpec {

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val groupAccountsDAO: GroupAccountsDAO = injector.instance[GroupAccountsDAO]
  val messagesDAO: MessagesDAO = injector.instance[MessagesDAO]
  val accountMessagesDAO: AccountMessagesDAO = injector.instance[AccountMessagesDAO]
  val pushNotificationsDAO: PushNotificationsDAO = injector.instance[PushNotificationsDAO]
  val pushNotificationSettingDAO: PushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val devicesDAO: DevicesDAO = injector.instance[DevicesDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]
  val accountGroupsDAO: AccountGroupsDAO = injector.instance[AccountGroupsDAO]
  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]
  val accountFeedsDAO: AccountFeedsDAO = injector.instance[AccountFeedsDAO]
  val followersDAO: FollowersDAO = injector.instance[FollowersDAO]

  test("findMessage - direct message") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    val groupId = Await.result(groupsDAO.create(sessionAccount.id.toSessionId))

    Await.result(accountGroupsDAO.create(account1.id, groupId))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId))

    val messageId = Await.result(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    Await.result(accountMessagesDAO.create(groupId, messageId, account1.id.toSessionId))
    Await.result(pushNotificationSettingDAO.create(false, false, false, false, true, false, account1.id.toSessionId))
    Await.result(devicesDAO.create(udid, None, account1.id.toSessionId))
    Await.result(devicesDAO.update(udid, pushToken, account1.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount.id, displayName, account1.id.toSessionId))

    val result1 = Await.result(pushNotificationsDAO.findByMessageId(messageId))
    assert(result1.size == 1L)

    val result2 = Await.result(pushNotificationsDAO.findByMessageId(MessageId(0L)))
    assert(result2.size == 0L)

  }


  test("findMessage - group message") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    val groupId = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))

    Await.result(accountGroupsDAO.create(account1.id, groupId))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId))

    val messageId = Await.result(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    Await.result(accountMessagesDAO.create(groupId, messageId, account1.id.toSessionId))
    Await.result(pushNotificationSettingDAO.create(false, false, false, true, false, false, account1.id.toSessionId))
    Await.result(devicesDAO.create(udid, None, account1.id.toSessionId))
    Await.result(devicesDAO.update(udid, pushToken, account1.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount.id, displayName, account1.id.toSessionId))

    val result1 = Await.result(pushNotificationsDAO.findByMessageId(messageId))
    assert(result1.size == 1L)

    val result2 = Await.result(pushNotificationsDAO.findByMessageId(MessageId(0L)))
    assert(result2.size == 0L)

  }

  test("findFeeds") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")
    val sessionAccount4 = createAccount("account3")
    val sessionAccount5 = createAccount("account4")
    val sessionAccount6 = createAccount("account5")
    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.everyone
    val contentWarning = true

    // create feed
    val feedId = Await.result(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, None, sessionAccount2.id.toSessionId))

    // create follow
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount3.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount4.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount5.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount6.id.toSessionId))

    // create account feeds
    Await.result(accountFeedsDAO.create(feedId, sessionAccount2.id.toSessionId))

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    Await.result(pushNotificationSettingDAO.create(false, true, false, false, false, false, sessionAccount1.id.toSessionId))
    Await.result(pushNotificationSettingDAO.create(false, true, false, false, false, false, sessionAccount3.id.toSessionId))
    Await.result(pushNotificationSettingDAO.create(false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    Await.result(pushNotificationSettingDAO.create(false, true, false, false, false, false, sessionAccount5.id.toSessionId))
    Await.result(pushNotificationSettingDAO.create(false, true, false, false, false, false, sessionAccount6.id.toSessionId))

    Await.result(devicesDAO.create(udid, None, sessionAccount1.id.toSessionId))
    Await.result(devicesDAO.create(udid, None, sessionAccount3.id.toSessionId))
    Await.result(devicesDAO.create(udid, None, sessionAccount4.id.toSessionId))
    Await.result(devicesDAO.create(udid, None, sessionAccount5.id.toSessionId))
    Await.result(devicesDAO.create(udid, None, sessionAccount6.id.toSessionId))

    Await.result(devicesDAO.update(udid, pushToken, sessionAccount1.id.toSessionId))
    Await.result(devicesDAO.update(udid, pushToken, sessionAccount3.id.toSessionId))
    Await.result(devicesDAO.update(udid, pushToken, sessionAccount4.id.toSessionId))
    Await.result(devicesDAO.update(udid, pushToken, sessionAccount5.id.toSessionId))
    Await.result(devicesDAO.update(udid, pushToken, sessionAccount6.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount1.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount3.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount4.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount5.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount6.id.toSessionId))

    // find account feed tokens
    val result = Await.result(pushNotificationsDAO.findByFeed(feedId))

    assert(result.size == 4)

  }
}
