package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class PushNotificationFeedsDAOSpec extends DAOSpec {

  feature("find") (pending)
//  {
//
//    val sessionUser1 = createUser("PushNotificationsDAOSPec5")
//    val sessionUser2 = createUser("PushNotificationsDAOSPec6")
//    val sessionUser3 = createUser("PushNotificationsDAOSPec7")
//    val sessionUser4 = createUser("PushNotificationsDAOSPec8")
//    val sessionUser5 = createUser("PushNotificationsDAOSPec9")
//    val sessionUser6 = createUser("PushNotificationsDAOSPec10")
//    val medium1 = createMedium(sessionUser1.id)
//    val medium2 = createMedium(sessionUser1.id)
//    val medium3 = createMedium(sessionUser1.id)
//    val message = "message"
//    val mediums = List(medium1.id, medium2.id, medium3.id)
//    val tags = List("tag1", "tag2", "tag3")
//    val privacyType = FeedPrivacyType.followers
//    val contentWarning = true
//
//    // create feed
//    val feedId = await(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, None, sessionUser2.id.toSessionId))
//
//    // create follows
//    await(followersDAO.create(sessionUser2.id, sessionUser1.id.toSessionId))
//    await(followersDAO.create(sessionUser2.id, sessionUser3.id.toSessionId))
//    await(followersDAO.create(sessionUser2.id, sessionUser4.id.toSessionId))
//    await(followersDAO.create(sessionUser2.id, sessionUser5.id.toSessionId))
//    await(followersDAO.create(sessionUser2.id, sessionUser6.id.toSessionId))
//
//    // create user feeds
//    await(userFeedsDAO.create(feedId, sessionUser2.id.toSessionId))
//
//    val displayName = Some("Invitation Sender Name")
//    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
//    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")
//
//    await(pushNotificationSettingDAO.create(true, false, false, false, false, false, false, sessionUser1.id.toSessionId))
//    await(pushNotificationSettingDAO.create(true, false, false, false, false, false, false, sessionUser3.id.toSessionId))
//    await(pushNotificationSettingDAO.create(false,false, false, false, false, false, false, sessionUser4.id.toSessionId))
//    await(pushNotificationSettingDAO.create(true, false, false, false, false, false, false, sessionUser5.id.toSessionId))
//    await(pushNotificationSettingDAO.create(true, false, false, false, false, false, false, sessionUser6.id.toSessionId))
//
//    await(devicesDAO.create(udid, DeviceType.ios, None, sessionUser1.id.toSessionId))
//    await(devicesDAO.create(udid, DeviceType.ios, None, sessionUser3.id.toSessionId))
//    await(devicesDAO.create(udid, DeviceType.ios, None, sessionUser4.id.toSessionId))
//    await(devicesDAO.create(udid, DeviceType.ios, None, sessionUser5.id.toSessionId))
//    await(devicesDAO.create(udid, DeviceType.ios, None, sessionUser6.id.toSessionId))
//
//    await(devicesDAO.update(udid, pushToken, sessionUser1.id.toSessionId))
//    await(devicesDAO.update(udid, pushToken, sessionUser3.id.toSessionId))
//    await(devicesDAO.update(udid, pushToken, sessionUser4.id.toSessionId))
//    await(devicesDAO.update(udid, pushToken, sessionUser5.id.toSessionId))
//    await(devicesDAO.update(udid, pushToken, sessionUser6.id.toSessionId))
//    await(usersDAO.updateDisplayName(sessionUser2.id, displayName, sessionUser1.id.toSessionId))
//    await(usersDAO.updateDisplayName(sessionUser2.id, displayName, sessionUser3.id.toSessionId))
//    await(usersDAO.updateDisplayName(sessionUser2.id, displayName, sessionUser4.id.toSessionId))
//    await(usersDAO.updateDisplayName(sessionUser2.id, displayName, sessionUser5.id.toSessionId))
//    await(usersDAO.updateDisplayName(sessionUser2.id, displayName, sessionUser6.id.toSessionId))
//
//    // find user feed tokens
//    val result = await(pushNotificationFeedsDAO.find(feedId))
//
//    assert(result.get.head.destinations.size == 4)
//
//  }

  feature("update user notified") (pending)

//  {
//
//    val sessionUser1 = createUser("UserFeedsDAOSpec6")
//    val sessionUser2 = createUser("UserFeedsDAOSpec7")
//    val sessionUser3 = createUser("UserFeedsDAOSpec8")
//    val sessionUser4 = createUser("UserFeedsDAOSpec9")
//    val sessionUser5 = createUser("UserFeedsDAOSpec10")
//    val sessionUser6 = createUser("UserFeedsDAOSpec11")
//    val medium1 = createMedium(sessionUser1.id)
//    val medium2 = createMedium(sessionUser1.id)
//    val medium3 = createMedium(sessionUser1.id)
//    val message = "message"
//    val mediums = List(medium1.id, medium2.id, medium3.id)
//    val tags = List("tag1", "tag2", "tag3")
//    val privacyType = FeedPrivacyType.followers
//    val contentWarning = true
//
//    // create feed
//    val feedId = await(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, None, sessionUser2.id.toSessionId))
//
//    // create follows
//    await(
//      for {
//        _ <- followersDAO.create(sessionUser2.id, sessionUser1.id.toSessionId)
//        _ <- followersDAO.create(sessionUser2.id, sessionUser3.id.toSessionId)
//        _ <- followersDAO.create(sessionUser2.id, sessionUser4.id.toSessionId)
//        _ <- followersDAO.create(sessionUser2.id, sessionUser5.id.toSessionId)
//        _ <- followersDAO.create(sessionUser2.id, sessionUser6.id.toSessionId)
//      } yield (())
//    )
//
//    // create user feeds
//    await(userFeedsDAO.create(feedId, sessionUser2.id.toSessionId))
//
//    val ids =  List(
//      sessionUser1.id,
//      sessionUser3.id,
//      sessionUser4.id,
//      sessionUser5.id,
//      sessionUser6.id
//    )
//
//    // update notified
//    await(pushNotificationFeedsDAO.update(feedId, ids, true))
//
//    //
//    val q = quote { query[UserFeeds].filter(_.feedId == lift(feedId)).filter(_.notified == true).size}
//    val result = await(db.run(q))
//    assert(result == ids.size)
//
//
//  }


  feature("update notified") (pending)
//  {
//
//    val sessionUser = createUser("FeedsDAOSpec23")
//
//    val medium1 = createMedium(sessionUser.id)
//    val medium2 = createMedium(sessionUser.id)
//    createMedium(sessionUser.id)
//
//    val message1 = "message1"
//    val message2 = "message2"
//    val message3 = "message3"
//    //    val message4 = "message4"
//    //    val message5 = "message5"
//    //    val message6 = "message6"
//    val mediums1 = List[MediumId]()
//    val mediums2 = List(medium1.id)
//    val mediums3 = List(medium1.id, medium2.id)
//    //    val mediums4 = List(medium1.id, medium2.id, medium3.id)
//    //    val mediums5 = List(medium1.id, medium2.id)
//    //    val mediums6 = List(medium1.id, medium2.id, medium3.id)
//    val tags1 = List[String]()
//    val tags2 = List("tag1")
//    val tags3 = List("tag1", "tag2")
//    val privacyType1 = FeedPrivacyType.self
//    val privacyType2 = FeedPrivacyType.friends
//    val privacyType3 = FeedPrivacyType.self
//    val contentWarning1 = false
//    val contentWarning2 = true
//    val contentWarning3 = false
//
//    // create feeds
//    val feedId1 = await(feedsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionUser.id.toSessionId))
//    val feedId2 = await(feedsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionUser.id.toSessionId))
//    val feedId3 = await(feedsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionUser.id.toSessionId))
//
//    await(
//      for {
//        _ <- pushNotificationFeedsDAO.update(feedId1, true)
//        _ <- pushNotificationFeedsDAO.update(feedId2, false)
//        _ <- pushNotificationFeedsDAO.update(feedId3, true)
//      } yield (())
//    )
//
//    val result1 = await(db.run(query[Feeds].filter(_.id == lift(feedId1))).map(_.headOption))
//    val result2 = await(db.run(query[Feeds].filter(_.id == lift(feedId2))).map(_.headOption))
//    val result3 = await(db.run(query[Feeds].filter(_.id == lift(feedId3))).map(_.headOption))
//
//    assert(result1.isDefined == true)
//    assert(result2.isDefined == true)
//    assert(result3.isDefined == true)
//
//    val feed1 = result1.head
//    val feed2 = result2.head
//    val feed3 = result3.head
//
//    assert(feed1.notified == true)
//    assert(feed2.notified == false)
//    assert(feed3.notified == true)
//
//  }


}
