package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{FeedPrivacyType}
import io.github.cactacea.core.helpers.CactaceaDAOTest

class DeliveryFeedsDAOSpec extends CactaceaDAOTest {

  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]
  val accountFeedsDAO: AccountFeedsDAO = injector.instance[AccountFeedsDAO]
  val followersDAO: FollowersDAO = injector.instance[FollowersDAO]
  val deliveryFeedsDAO: DeliveryFeedsDAO = injector.instance[DeliveryFeedsDAO]
  val pushNotificationSettingDAO: PushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val devicesDAO: DevicesDAO = injector.instance[DevicesDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]

  test("findAll") {

    val sessionAccount1 = this.createAccount(0L)
    val sessionAccount2 = this.createAccount(1L)
    val sessionAccount3 = this.createAccount(2L)
    val sessionAccount4 = this.createAccount(3L)
    val sessionAccount5 = this.createAccount(4L)
    val sessionAccount6 = this.createAccount(5L)
    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.everyone
    val contentWarning = true

    // create feed
    val feedId = Await.result(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, sessionAccount2.id.toSessionId))

    // create follow
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount3.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount4.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount5.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount6.id.toSessionId))

    // create account feeds
    Await.result(accountFeedsDAO.create(feedId, sessionAccount2.id.toSessionId))

    val displayName = Some("Invite Sender Name")
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
    val result = Await.result(deliveryFeedsDAO.findTokens(feedId))

    assert(result.size == 4)

  }

}
