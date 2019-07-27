package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class PushNotificationCommentsDAOSpec extends DAOSpec {

  feature("create - fanout") (pending)
//  {
//
//    val sessionAccount1 = createAccount("CommentsDAOSpec3")
//    val sessionAccount2 = createAccount("CommentsDAOSpec4")
//
//    val feedId = await(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
//    val commentId = await(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
//
//    val displayName = Some("Invitation Sender Name")
//    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
//    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")
//
//    await(pushNotificationSettingDAO.create(false, true, false, false, false, false, false, sessionAccount1.id.toSessionId))
//    await(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount1.id.toSessionId))
//    await(devicesDAO.update(udid, pushToken, sessionAccount1.id.toSessionId))
//    await(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount1.id.toSessionId))
//
//    val result = await(pushNotificationCommentsDAO.find(commentId))
//    assert(result.isDefined)
//    assert(result.get(0).destinations.head.accountId == sessionAccount1.id)
//    assert(result.get(0).displayName == displayName.get)
//
//  }


  feature("find for push notification") (pending)
//  {
//
//    val sessionAccount1 = createAccount("CommentsDAOSpec13")
//    val sessionAccount2 = createAccount("CommentsDAOSpec14")
//
//    val feedId = await(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
//    val commentId1 = await(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
//    val commentId2 = await(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))
//
//    val comment1 = await(db.run(query[Comments].filter(_.id == lift(commentId1))).map(_.head))
//    val comment2 = await(db.run(query[Comments].filter(_.id == lift(commentId2))).map(_.head))
//    assert(comment1.id == commentId1)
//    assert(comment2.id == commentId2)
//
//    await(commentsDAO.delete(commentId2, sessionAccount1.id.toSessionId))
//    val comment3 = await(db.run(query[Comments].filter(_.id == lift(commentId2))))
//    assert(comment3.isEmpty)
//
//  }

  feature("updateNotified")  (pending)
//  {
//
//    val sessionAccount1 = createAccount("CommentsDAOSpec15")
//    val sessionAccount2 = createAccount("CommentsDAOSpec16")
//
//    val feedId = await(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
//    val commentId1 = await(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
//    val commentId2 = await(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))
//
//    await(pushNotificationCommentsDAO.update(commentId1))
//    await(pushNotificationCommentsDAO.update(commentId2))
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
