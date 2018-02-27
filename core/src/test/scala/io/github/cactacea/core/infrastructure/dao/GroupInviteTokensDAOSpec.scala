package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.helpers.DAOSpec

class GroupInviteTokensDAOSpec extends DAOSpec {

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val groupInvitesDAO: GroupInvitesDAO = injector.instance[GroupInvitesDAO]
  val groupInviteTokensDAO: DeliveryGroupInvitesDAO = injector.instance[DeliveryGroupInvitesDAO]
  val pushNotificationSettingDAO: PushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val devicesDAO: DevicesDAO = injector.instance[DevicesDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]

  test("findAll") {

    val sessionAccount = this.createAccount(0L)
    val owner1 = this.createAccount(1L)

    val displayName = Some("Invite Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupInviteId = Await.result(groupInvitesDAO.create(owner1.id, groupId1, sessionAccount.id.toSessionId))
    Await.result(pushNotificationSettingDAO.create(true, false, false, false, false, false, owner1.id.toSessionId))
    Await.result(devicesDAO.create(udid, None, owner1.id.toSessionId))
    Await.result(devicesDAO.update(udid, pushToken, owner1.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount.id, displayName, owner1.id.toSessionId))

    val result1 = Await.result(groupInviteTokensDAO.findTokens(groupInviteId))
    assert(result1.size == 1L)
    assert(result1(0)._2 == displayName.get)
    assert(result1(0)._1 == owner1.id)

  }

}
