package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class NotificationCommentsDAOSpec extends DAOSpec {

  feature("create - fanout") (pending)
//  {
//
//    val sessionUser1 = createUser("CommentsDAOSpec3")
//    val sessionUser2 = createUser("CommentsDAOSpec4")
//
//    val tweetId = await(tweetsDAO.create("message", None, None, TweetPrivacyType.everyone, false, None, sessionUser1.id.toSessionId))
//    val commentId = await(commentsDAO.create(tweetId, "1" * 100, sessionUser2.id.toSessionId))
//
//    val displayName = Some("Invitation Sender Name")
//    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
//    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")
//
//    await(notificationSettingDAO.create(false, true, false, false, false, false, false, sessionUser1.id.toSessionId))
//    await(devicesDAO.create(udid, DeviceType.ios, None, sessionUser1.id.toSessionId))
//    await(devicesDAO.update(udid, pushToken, sessionUser1.id.toSessionId))
//    await(usersDAO.updateDisplayName(sessionUser2.id, displayName, sessionUser1.id.toSessionId))
//
//    val result = await(notificationCommentsDAO.find(commentId))
//    assert(result.isDefined)
//    assert(result.get(0).destinations.head.userId == sessionUser1.id)
//    assert(result.get(0).displayName == displayName.get)
//
//  }


  feature("find for push notification") (pending)
//  {
//
//    val sessionUser1 = createUser("CommentsDAOSpec13")
//    val sessionUser2 = createUser("CommentsDAOSpec14")
//
//    val tweetId = await(tweetsDAO.create("message", None, None, TweetPrivacyType.everyone, false, None, sessionUser1.id.toSessionId))
//    val commentId1 = await(commentsDAO.create(tweetId, "1" * 100, sessionUser2.id.toSessionId))
//    val commentId2 = await(commentsDAO.create(tweetId, "2" * 100, sessionUser1.id.toSessionId))
//
//    val comment1 = await(db.run(query[Comments].filter(_.id == lift(commentId1))).map(_.head))
//    val comment2 = await(db.run(query[Comments].filter(_.id == lift(commentId2))).map(_.head))
//    assert(comment1.id == commentId1)
//    assert(comment2.id == commentId2)
//
//    await(commentsDAO.delete(commentId2, sessionUser1.id.toSessionId))
//    val comment3 = await(db.run(query[Comments].filter(_.id == lift(commentId2))))
//    assert(comment3.isEmpty)
//
//  }

  feature("updateNotified")  (pending)
//  {
//
//    val sessionUser1 = createUser("CommentsDAOSpec15")
//    val sessionUser2 = createUser("CommentsDAOSpec16")
//
//    val tweetId = await(tweetsDAO.create("message", None, None, TweetPrivacyType.everyone, false, None, sessionUser1.id.toSessionId))
//    val commentId1 = await(commentsDAO.create(tweetId, "1" * 100, sessionUser2.id.toSessionId))
//    val commentId2 = await(commentsDAO.create(tweetId, "2" * 100, sessionUser1.id.toSessionId))
//
//    await(notificationCommentsDAO.update(commentId1))
//    await(notificationCommentsDAO.update(commentId2))
//
//    val comment1 = await(db.run(query[Comments].filter(_.id == lift(commentId1))).map(_.head))
//    val comment2 = await(db.run(query[Comments].filter(_.id == lift(commentId2))).map(_.head))
//    assert(comment1.id == commentId1)
//    assert(comment2.id == commentId2)
//    assert(comment1.notified == true)
//    assert(comment2.notified == true)
//
//  }

}
