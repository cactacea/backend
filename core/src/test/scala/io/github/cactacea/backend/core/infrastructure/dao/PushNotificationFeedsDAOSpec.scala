package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{DeviceType, FeedPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.github.cactacea.backend.core.infrastructure.models.{AccountFeeds, Feeds}

class PushNotificationFeedsDAOSpec extends DAOSpec {

  import db._

  test("find") {

    val sessionAccount1 = createAccount("PushNotificationsDAOSPec5")
    val sessionAccount2 = createAccount("PushNotificationsDAOSPec6")
    val sessionAccount3 = createAccount("PushNotificationsDAOSPec7")
    val sessionAccount4 = createAccount("PushNotificationsDAOSPec8")
    val sessionAccount5 = createAccount("PushNotificationsDAOSPec9")
    val sessionAccount6 = createAccount("PushNotificationsDAOSPec10")
    val medium1 = createMedium(sessionAccount1.id)
    val medium2 = createMedium(sessionAccount1.id)
    val medium3 = createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.followers
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

    execute(pushNotificationSettingDAO.create(true, false, false, false, false, false, false, sessionAccount1.id.toSessionId))
    execute(pushNotificationSettingDAO.create(true, false, false, false, false, false, false, sessionAccount3.id.toSessionId))
    execute(pushNotificationSettingDAO.create(false,false, false, false, false, false, false, sessionAccount4.id.toSessionId))
    execute(pushNotificationSettingDAO.create(true, false, false, false, false, false, false, sessionAccount5.id.toSessionId))
    execute(pushNotificationSettingDAO.create(true, false, false, false, false, false, false, sessionAccount6.id.toSessionId))

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
    val result = execute(pushNotificationFeedsDAO.find(feedId))

    assert(result.get.head.destinations.size == 4)

  }

  test("update account notified") {

    val sessionAccount1 = createAccount("AccountFeedsDAOSpec6")
    val sessionAccount2 = createAccount("AccountFeedsDAOSpec7")
    val sessionAccount3 = createAccount("AccountFeedsDAOSpec8")
    val sessionAccount4 = createAccount("AccountFeedsDAOSpec9")
    val sessionAccount5 = createAccount("AccountFeedsDAOSpec10")
    val sessionAccount6 = createAccount("AccountFeedsDAOSpec11")
    val medium1 = createMedium(sessionAccount1.id)
    val medium2 = createMedium(sessionAccount1.id)
    val medium3 = createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.followers
    val contentWarning = true

    // create feed
    val feedId = execute(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, None, sessionAccount2.id.toSessionId))

    // create follows
    execute(
      for {
        _ <- followersDAO.create(sessionAccount2.id, sessionAccount1.id.toSessionId)
        _ <- followersDAO.create(sessionAccount2.id, sessionAccount3.id.toSessionId)
        _ <- followersDAO.create(sessionAccount2.id, sessionAccount4.id.toSessionId)
        _ <- followersDAO.create(sessionAccount2.id, sessionAccount5.id.toSessionId)
        _ <- followersDAO.create(sessionAccount2.id, sessionAccount6.id.toSessionId)
      } yield (())
    )

    // create account feeds
    execute(accountFeedsDAO.create(feedId, sessionAccount2.id.toSessionId))

    val ids =  List(
      sessionAccount1.id,
      sessionAccount3.id,
      sessionAccount4.id,
      sessionAccount5.id,
      sessionAccount6.id
    )

    // update notified
    execute(pushNotificationFeedsDAO.update(feedId, ids, true))

    //
    val q = quote { query[AccountFeeds].filter(_.feedId == lift(feedId)).filter(_.notified == true).size}
    val result = execute(db.run(q))
    assert(result == ids.size)


  }


  test("update notified") {

    val sessionAccount = createAccount("FeedsDAOSpec23")

    val medium1 = createMedium(sessionAccount.id)
    val medium2 = createMedium(sessionAccount.id)
    createMedium(sessionAccount.id)

    val message1 = "message1"
    val message2 = "message2"
    val message3 = "message3"
    //    val message4 = "message4"
    //    val message5 = "message5"
    //    val message6 = "message6"
    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    //    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    //    val mediums5 = List(medium1.id, medium2.id)
    //    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val privacyType1 = FeedPrivacyType.self
    val privacyType2 = FeedPrivacyType.friends
    val privacyType3 = FeedPrivacyType.self
    val contentWarning1 = false
    val contentWarning2 = true
    val contentWarning3 = false

    // create feeds
    val feedId1 = execute(feedsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionAccount.id.toSessionId))
    val feedId2 = execute(feedsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionAccount.id.toSessionId))
    val feedId3 = execute(feedsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionAccount.id.toSessionId))

    execute(
      for {
        _ <- pushNotificationFeedsDAO.update(feedId1, true)
        _ <- pushNotificationFeedsDAO.update(feedId2, false)
        _ <- pushNotificationFeedsDAO.update(feedId3, true)
      } yield (())
    )

    val result1 = execute(db.run(query[Feeds].filter(_.id == lift(feedId1))).map(_.headOption))
    val result2 = execute(db.run(query[Feeds].filter(_.id == lift(feedId2))).map(_.headOption))
    val result3 = execute(db.run(query[Feeds].filter(_.id == lift(feedId3))).map(_.headOption))

    assert(result1.isDefined == true)
    assert(result2.isDefined == true)
    assert(result3.isDefined == true)

    val feed1 = result1.head
    val feed2 = result2.head
    val feed3 = result3.head

    assert(feed1.notified == true)
    assert(feed2.notified == false)
    assert(feed3.notified == true)

  }


}
