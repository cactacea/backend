package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class PushNotificationTweetsDAOSpec extends DAOSpec {

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
//    val mediums = Seq(medium1.id, medium2.id, medium3.id)
//    val tags = Seq("tag1", "tag2", "tag3")
//    val privacyType = TweetPrivacyType.followers
//    val contentWarning = true
//
//    // create tweet
//    val tweetId = await(tweetsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, None, sessionUser2.id.toSessionId))
//
//    // create follows
//    await(followersDAO.create(sessionUser2.id, sessionUser1.id.toSessionId))
//    await(followersDAO.create(sessionUser2.id, sessionUser3.id.toSessionId))
//    await(followersDAO.create(sessionUser2.id, sessionUser4.id.toSessionId))
//    await(followersDAO.create(sessionUser2.id, sessionUser5.id.toSessionId))
//    await(followersDAO.create(sessionUser2.id, sessionUser6.id.toSessionId))
//
//    // create user tweets
//    await(userTweetsDAO.create(tweetId, sessionUser2.id.toSessionId))
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
//    // find user tweet tokens
//    val result = await(pushNotificationTweetsDAO.find(tweetId))
//
//    assert(result.get.head.destinations.size == 4)
//
//  }

  feature("update user notified") (pending)

//  {
//
//    val sessionUser1 = createUser("UserTweetsDAOSpec6")
//    val sessionUser2 = createUser("UserTweetsDAOSpec7")
//    val sessionUser3 = createUser("UserTweetsDAOSpec8")
//    val sessionUser4 = createUser("UserTweetsDAOSpec9")
//    val sessionUser5 = createUser("UserTweetsDAOSpec10")
//    val sessionUser6 = createUser("UserTweetsDAOSpec11")
//    val medium1 = createMedium(sessionUser1.id)
//    val medium2 = createMedium(sessionUser1.id)
//    val medium3 = createMedium(sessionUser1.id)
//    val message = "message"
//    val mediums = Seq(medium1.id, medium2.id, medium3.id)
//    val tags = Seq("tag1", "tag2", "tag3")
//    val privacyType = TweetPrivacyType.followers
//    val contentWarning = true
//
//    // create tweet
//    val tweetId = await(tweetsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, None, sessionUser2.id.toSessionId))
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
//    // create user tweets
//    await(userTweetsDAO.create(tweetId, sessionUser2.id.toSessionId))
//
//    val ids =  Seq(
//      sessionUser1.id,
//      sessionUser3.id,
//      sessionUser4.id,
//      sessionUser5.id,
//      sessionUser6.id
//    )
//
//    // update notified
//    await(pushNotificationTweetsDAO.update(tweetId, ids, true))
//
//    //
//    val q = quote { query[UserTweets].filter(_.tweetId == lift(tweetId)).filter(_.notified == true).size}
//    val result = await(db.run(q))
//    assert(result == ids.size)
//
//
//  }


  feature("update notified") (pending)
//  {
//
//    val sessionUser = createUser("TweetsDAOSpec23")
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
//    val mediums1 = Seq[MediumId]()
//    val mediums2 = Seq(medium1.id)
//    val mediums3 = Seq(medium1.id, medium2.id)
//    //    val mediums4 = Seq(medium1.id, medium2.id, medium3.id)
//    //    val mediums5 = Seq(medium1.id, medium2.id)
//    //    val mediums6 = Seq(medium1.id, medium2.id, medium3.id)
//    val tags1 = Seq[String]()
//    val tags2 = Seq("tag1")
//    val tags3 = Seq("tag1", "tag2")
//    val privacyType1 = TweetPrivacyType.self
//    val privacyType2 = TweetPrivacyType.friends
//    val privacyType3 = TweetPrivacyType.self
//    val contentWarning1 = false
//    val contentWarning2 = true
//    val contentWarning3 = false
//
//    // create tweets
//    val tweetId1 = await(tweetsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionUser.id.toSessionId))
//    val tweetId2 = await(tweetsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionUser.id.toSessionId))
//    val tweetId3 = await(tweetsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionUser.id.toSessionId))
//
//    await(
//      for {
//        _ <- pushNotificationTweetsDAO.update(tweetId1, true)
//        _ <- pushNotificationTweetsDAO.update(tweetId2, false)
//        _ <- pushNotificationTweetsDAO.update(tweetId3, true)
//      } yield (())
//    )
//
//    val result1 = await(db.run(query[Tweets].filter(_.id == lift(tweetId1))).map(_.headOption))
//    val result2 = await(db.run(query[Tweets].filter(_.id == lift(tweetId2))).map(_.headOption))
//    val result3 = await(db.run(query[Tweets].filter(_.id == lift(tweetId3))).map(_.headOption))
//
//    assert(result1.isDefined == true)
//    assert(result2.isDefined == true)
//    assert(result3.isDefined == true)
//
//    val tweet1 = result1.head
//    val tweet2 = result2.head
//    val tweet3 = result3.head
//
//    assert(tweet1.notified == true)
//    assert(tweet2.notified == false)
//    assert(tweet3.notified == true)
//
//  }


}
