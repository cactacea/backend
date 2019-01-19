package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{DeviceType, FeedPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId

class CommentsDAOSpec extends DAOSpec {



  test("create") {

    val sessionAccount1 = createAccount("CommentsDAOSpec1")
    val sessionAccount2 = createAccount("CommentsDAOSpec2")

    val feedId = execute(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
    val commentId1 = execute(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = execute(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))
    val commentId3 = execute(commentsDAO.create(feedId, "3" * 100, sessionAccount2.id.toSessionId))
    val commentId4 = execute(commentsDAO.create(feedId, "4" * 100, sessionAccount1.id.toSessionId))
    val commentId5 = execute(commentsDAO.create(feedId, "5" * 100, sessionAccount2.id.toSessionId))

    val result1 = execute(commentsDAO.find(feedId, None, 0, 4, sessionAccount1.id.toSessionId))
    assert(result1.size == 4)
    val comment1 = result1(0)
    val comment2 = result1(1)
    val comment3 = result1(2)
    val comment4 = result1(3)
    assert(comment1.id == commentId5)
    assert(comment2.id == commentId4)
    assert(comment3.id == commentId3)
    assert(comment4.id == commentId2)

    // TODO : Test ReplyId
//    assert(comment1.feedId == feedId)
//    assert(comment2.feedId == feedId)
//    assert(comment3.feedId == feedId)
//    assert(comment4.feedId == feedId)

    assert(comment1.account.id == sessionAccount2.id)
    assert(comment2.account.id == sessionAccount1.id)
    assert(comment3.account.id == sessionAccount2.id)
    assert(comment4.account.id == sessionAccount1.id)

    assert(comment1.message == "5" * 100)
    assert(comment2.message == "4" * 100)
    assert(comment3.message == "3" * 100)
    assert(comment4.message == "2" * 100)

    val result2 = execute(commentsDAO.find(feedId, Some(comment4.id.value), 0, 4, sessionAccount1.id.toSessionId))
    assert(result2.size == 1)
    val comment5 = result2(0)


    assert(comment5.id == commentId1)
//    assert(comment5.feedId == feedId)
    assert(comment5.account.id == sessionAccount2.id)
    assert(comment5.message == "1" * 100)

    val feed1 = execute(feedsDAO.find(feedId, sessionAccount1.id.toSessionId))
    assert(feed1.map(_.commentCount) == Some(5))

  }

  test("create - fanout") {

    val sessionAccount1 = createAccount("CommentsDAOSpec3")
    val sessionAccount2 = createAccount("CommentsDAOSpec4")

    val feedId = execute(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
    val commentId = execute(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    execute(pushNotificationSettingDAO.create(false, true, false, false, false, false, false, sessionAccount1.id.toSessionId))
    execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount1.id.toSessionId))
    execute(devicesDAO.update(udid, pushToken, sessionAccount1.id.toSessionId))
    execute(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount1.id.toSessionId))

    val result = execute(commentsDAO.findPushNotifications(commentId, false))

    assert(result(0).accountId == sessionAccount1.id)
    assert(result(0).displayName == displayName.get)

  }

  test("exist") {

    val sessionAccount1 = createAccount("CommentsDAOSpec5")
    val sessionAccount2 = createAccount("CommentsDAOSpec6")

    val feedId = execute(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
    val commentId1 = execute(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    execute(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))

    val exist1 = execute(commentsDAO.exist(commentId1, sessionAccount1.id.toSessionId))
    val exist2 = execute(commentsDAO.exist(commentId1, sessionAccount1.id.toSessionId))
    assert(exist1 == true)
    assert(exist2 == true)

  }

  test("delete") {

    val sessionAccount1 = createAccount("CommentsDAOSpec7")
    val sessionAccount2 = createAccount("CommentsDAOSpec8")

    val feedId = execute(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
    val commentId1 = execute(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = execute(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))
    execute(commentLikesDAO.create(commentId1, sessionAccount1.id.toSessionId))

    val exist1 = execute(commentsDAO.exist(commentId1, sessionAccount2.id.toSessionId))
    val exist2 = execute(commentsDAO.exist(commentId2, sessionAccount1.id.toSessionId))
    assert(exist1 == true)
    assert(exist2 == true)

    val feed1 = execute(feedsDAO.find(feedId, sessionAccount1.id.toSessionId))
    assert(feed1.map(_.commentCount) == Some(2))

    execute(commentsDAO.delete(commentId1, sessionAccount2.id.toSessionId))
    execute(commentsDAO.delete(commentId2, sessionAccount1.id.toSessionId))

    val exist3 = execute(commentsDAO.exist(commentId1, sessionAccount2.id.toSessionId))
    val exist4 = execute(commentsDAO.exist(commentId2, sessionAccount1.id.toSessionId))
    assert(exist3 == false)
    assert(exist4 == false)

    val feed2 = execute(feedsDAO.find(feedId, sessionAccount1.id.toSessionId))
    assert(feed2.map(_.commentCount) == Some(0))

  }

  test("find") {

    val sessionAccount1 = createAccount("CommentsDAOSpec9")
    val sessionAccount2 = createAccount("CommentsDAOSpec10")

    val feedId = execute(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
    val commentId1 = execute(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = execute(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))

    val comment1 = execute(commentsDAO.find(commentId1, sessionAccount1.id.toSessionId)).get
    val comment2 = execute(commentsDAO.find(commentId2, sessionAccount1.id.toSessionId)).get
    assert(comment1.id == commentId1)
    assert(comment2.id == commentId2)

    execute(commentsDAO.delete(commentId2, sessionAccount1.id.toSessionId))
    val comment3 = execute(commentsDAO.find(commentId2, sessionAccount1.id.toSessionId))
    assert(comment3.isEmpty)

  }

  test("find all") {

    val sessionAccount1 = createAccount("CommentsDAOSpec11")
    val sessionAccount2 = createAccount("CommentsDAOSpec12")

    val feedId = execute(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
    val commentId1 = execute(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = execute(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))
    val commentId3 = execute(commentsDAO.create(feedId, "3" * 100, sessionAccount2.id.toSessionId))
    val commentId4 = execute(commentsDAO.create(feedId, "4" * 100, sessionAccount1.id.toSessionId))
    val commentId5 = execute(commentsDAO.create(feedId, "5" * 100, sessionAccount2.id.toSessionId))
    val commentId6 = execute(commentsDAO.create(feedId, "6" * 100, sessionAccount1.id.toSessionId))
    val commentId7 = execute(commentsDAO.create(feedId, "7" * 100, sessionAccount2.id.toSessionId))
    val commentId8 = execute(commentsDAO.create(feedId, "8" * 100, sessionAccount1.id.toSessionId))

    val result1 = execute(commentsDAO.find(feedId, None, 0, 3, sessionAccount1.id.toSessionId))
    assert(result1.size == 3)
    val comment1 = result1(0)
    val comment2 = result1(1)
    val comment3 = result1(2)
    assert(comment1.id == commentId8)
    assert(comment2.id == commentId7)
    assert(comment3.id == commentId6)

    val result2 = execute(commentsDAO.find(feedId, comment3.next, 0, 3, sessionAccount1.id.toSessionId))
    assert(result2.size == 3)
    val comment4 = result2(0)
    val comment5 = result2(1)
    val comment6 = result2(2)
    assert(comment4.id == commentId5)
    assert(comment5.id == commentId4)
    assert(comment6.id == commentId3)

    val result3 = execute(commentsDAO.find(feedId, comment6.next, 0, 3, sessionAccount1.id.toSessionId))
    assert(result3.size == 2)
    val comment7 = result3(0)
    val comment8 = result3(1)
    assert(comment7.id == commentId2)
    assert(comment8.id == commentId1)

    val result4 = execute(commentsDAO.find(FeedId(0L), Some(-1L), 0, 3, sessionAccount1.id.toSessionId))
    assert(result4.size == 0)
  }

  test("find for push notification") {

    val sessionAccount1 = createAccount("CommentsDAOSpec13")
    val sessionAccount2 = createAccount("CommentsDAOSpec14")

    val feedId = execute(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
    val commentId1 = execute(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = execute(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))

    val comment1 = execute(commentsDAO.find(commentId1)).get
    val comment2 = execute(commentsDAO.find(commentId1)).get
    assert(comment1.id == commentId1)
    assert(comment2.id == commentId1)

    execute(commentsDAO.delete(commentId2, sessionAccount1.id.toSessionId))
    val comment3 = execute(commentsDAO.find(commentId2))
    assert(comment3.isEmpty)

  }

  test("updateNotified") {

    val sessionAccount1 = createAccount("CommentsDAOSpec15")
    val sessionAccount2 = createAccount("CommentsDAOSpec16")

    val feedId = execute(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, None, sessionAccount1.id.toSessionId))
    val commentId1 = execute(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = execute(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))

    execute(commentsDAO.updatePushNotifications(commentId1))
    execute(commentsDAO.updatePushNotifications(commentId2))

    val comment1 = execute(commentsDAO.find(commentId1)).get
    val comment2 = execute(commentsDAO.find(commentId1)).get
    assert(comment1.id == commentId1)
    assert(comment2.id == commentId1)
    assert(comment1.notified == true)
    assert(comment2.notified == true)

  }

}
