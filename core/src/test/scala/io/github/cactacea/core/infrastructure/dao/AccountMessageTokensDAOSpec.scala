package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.helpers.CactaceaDAOTest
import io.github.cactacea.core.infrastructure.identifiers.MessageId

class AccountMessageTokensDAOSpec extends CactaceaDAOTest {

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val groupAccountsDAO: GroupAccountsDAO = injector.instance[GroupAccountsDAO]
  val messagesDAO: MessagesDAO = injector.instance[MessagesDAO]
  val accountMessagesDAO: AccountMessagesDAO = injector.instance[AccountMessagesDAO]
  val accountMessageTokensDAO: DeliveryMessagesDAO = injector.instance[DeliveryMessagesDAO]
  val pushNotificationSettingDAO: PushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val devicesDAO: DevicesDAO = injector.instance[DevicesDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]
  val accountGroupsDAO: AccountGroupsDAO = injector.instance[AccountGroupsDAO]

  test("findAll for a direct message group") {

    val sessionAccount = this.createAccount(0L)
    val account1 = this.createAccount(1L)

    val displayName = Some("Invite Sender Name")
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

    val result1 = Await.result(accountMessageTokensDAO.findTokens(messageId))
    assert(result1.size == 1L)

    val result2 = Await.result(accountMessageTokensDAO.findTokens(MessageId(0L)))
    assert(result2.size == 0L)

  }


  test("findAll for a message group") {

    val sessionAccount = this.createAccount(0L)
    val account1 = this.createAccount(1L)

    val displayName = Some("Invite Sender Name")
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

    val result1 = Await.result(accountMessageTokensDAO.findTokens(messageId))
    assert(result1.size == 1L)

    val result2 = Await.result(accountMessageTokensDAO.findTokens(MessageId(0L)))
    assert(result2.size == 0L)

  }

}
