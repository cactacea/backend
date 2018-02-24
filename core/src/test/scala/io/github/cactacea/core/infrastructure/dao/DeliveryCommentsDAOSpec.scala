package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{FeedPrivacyType}
import io.github.cactacea.core.helpers.CactaceaDAOTest

class DeliveryCommentsDAOSpec extends CactaceaDAOTest {

  val commentsDAO: CommentsDAO = injector.instance[CommentsDAO]
  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]
  val pushNotificationSettingDAO: PushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val devicesDAO: DevicesDAO = injector.instance[DevicesDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]
  val commentTokensDAO: DeliveryCommentsDAO = injector.instance[DeliveryCommentsDAO]

  test("create") {

    val sessionAccount1 = createAccount(0L)
    val sessionAccount2 = createAccount(1L)

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))

    val displayName = Some("Invite Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    Await.result(pushNotificationSettingDAO.create(false, false, true, false, false, false, sessionAccount1.id.toSessionId))
    Await.result(devicesDAO.create(udid, None, sessionAccount1.id.toSessionId))
    Await.result(devicesDAO.update(udid, pushToken, sessionAccount1.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount1.id.toSessionId))

    val result = Await.result(commentTokensDAO.findTokens(commentId))
    assert(result(0)._1 == sessionAccount1.id)
    assert(result(0)._2 == displayName.get)

  }

}
