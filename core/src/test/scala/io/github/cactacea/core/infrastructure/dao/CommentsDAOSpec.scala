package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, FeedId}

class CommentsDAOSpec extends DAOSpec {

  val commentFavoritesDAO = injector.instance[CommentFavoritesDAO]
  val commentsDAO: CommentsDAO = injector.instance[CommentsDAO]
  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]

  val pushNotificationSettingDAO: PushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val devicesDAO: DevicesDAO = injector.instance[DevicesDAO]
  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]
  val pushNotificationsDAO: PushNotificationsDAO = injector.instance[PushNotificationsDAO]


  test("create") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, sessionAccount1.id.toSessionId))
    val commentId1 = Await.result(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = Await.result(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))
    val commentId3 = Await.result(commentsDAO.create(feedId, "3" * 100, sessionAccount2.id.toSessionId))
    val commentId4 = Await.result(commentsDAO.create(feedId, "4" * 100, sessionAccount1.id.toSessionId))
    val commentId5 = Await.result(commentsDAO.create(feedId, "5" * 100, sessionAccount2.id.toSessionId))

    val result1 = Await.result(commentsDAO.findAll(feedId, Some(Long.MaxValue), Some(4), sessionAccount1.id.toSessionId))
    assert(result1.size == 4)
    val comment1 = result1(0)._1
    val comment2 = result1(1)._1
    val comment3 = result1(2)._1
    val comment4 = result1(3)._1
    assert((comment1.id, comment1.feedId, comment1.by, comment1.message) == (commentId5, feedId, sessionAccount2.id, "5" * 100))
    assert((comment2.id, comment2.feedId, comment2.by, comment2.message) == (commentId4, feedId, sessionAccount1.id, "4" * 100))
    assert((comment3.id, comment3.feedId, comment3.by, comment3.message) == (commentId3, feedId, sessionAccount2.id, "3" * 100))
    assert((comment4.id, comment4.feedId, comment4.by, comment4.message) == (commentId2, feedId, sessionAccount1.id, "2" * 100))

    val result2 = Await.result(commentsDAO.findAll(feedId, Some(comment4.postedAt), Some(4), sessionAccount1.id.toSessionId))
    assert(result2.size == 1)
    val comment5 = result2(0)._1
    assert((comment5.id, comment5.feedId, comment5.by, comment5.message) == (commentId1, feedId, sessionAccount2.id, "1" * 100))

    val feed1 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.commentCount
    assert(feed1 == 5)

  }

  test("create - fanout") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))

    val displayName = Some("Invitation Sender Name")
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    Await.result(pushNotificationSettingDAO.create(false, false, true, false, false, false, sessionAccount1.id.toSessionId))
    Await.result(devicesDAO.create(udid, None, sessionAccount1.id.toSessionId))
    Await.result(devicesDAO.update(udid, pushToken, sessionAccount1.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(sessionAccount2.id, displayName, sessionAccount1.id.toSessionId))

    val result = Await.result(pushNotificationsDAO.findByCommentId(commentId))

    assert(result(0).accountId == sessionAccount1.id)
    assert(result(0).displayName == displayName.get)

  }

  test("exist") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, sessionAccount1.id.toSessionId))
    val commentId1 = Await.result(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = Await.result(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))

    val exist1 = Await.result(commentsDAO.exist(commentId1, sessionAccount1.id.toSessionId))
    val exist2 = Await.result(commentsDAO.exist(commentId1, sessionAccount1.id.toSessionId))
    assert(exist1 == true)
    assert(exist2 == true)

  }

  test("delete") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, sessionAccount1.id.toSessionId))
    val commentId1 = Await.result(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = Await.result(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId1, sessionAccount1.id.toSessionId))

    val exist1 = Await.result(commentsDAO.exist(commentId1, sessionAccount2.id.toSessionId))
    val exist2 = Await.result(commentsDAO.exist(commentId2, sessionAccount1.id.toSessionId))
    assert(exist1 == true)
    assert(exist2 == true)

    val feed1 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId))
    assert(feed1.get._1.commentCount == 2)

    val result1 = Await.result(commentsDAO.delete(commentId1, sessionAccount2.id.toSessionId))
    val result2 = Await.result(commentsDAO.delete(commentId2, sessionAccount1.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

    val exist3 = Await.result(commentsDAO.exist(commentId1, sessionAccount2.id.toSessionId))
    val exist4 = Await.result(commentsDAO.exist(commentId2, sessionAccount1.id.toSessionId))
    assert(exist3 == false)
    assert(exist4 == false)

    val feed2 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).get._1.commentCount
    assert(feed2 == 0)

    val result3 = Await.result(commentsDAO.delete(CommentId(0L), sessionAccount2.id.toSessionId))
    assert(result3 == false)
  }

  test("find") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, sessionAccount1.id.toSessionId))
    val commentId1 = Await.result(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = Await.result(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))

    val comment1 = Await.result(commentsDAO.find(commentId1, sessionAccount1.id.toSessionId)).get._1
    val comment2 = Await.result(commentsDAO.find(commentId1, sessionAccount1.id.toSessionId)).get._1
    assert(comment1.id == commentId1)
    assert(comment2.id == commentId1)

    Await.result(commentsDAO.delete(commentId2, sessionAccount1.id.toSessionId))
    val comment3 = Await.result(commentsDAO.find(commentId2, sessionAccount1.id.toSessionId))
    assert(comment3.isEmpty)

  }

  test("findAll") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, sessionAccount1.id.toSessionId))
    val commentId1 = Await.result(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = Await.result(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))
    val commentId3 = Await.result(commentsDAO.create(feedId, "3" * 100, sessionAccount2.id.toSessionId))
    val commentId4 = Await.result(commentsDAO.create(feedId, "4" * 100, sessionAccount1.id.toSessionId))
    val commentId5 = Await.result(commentsDAO.create(feedId, "5" * 100, sessionAccount2.id.toSessionId))
    val commentId6 = Await.result(commentsDAO.create(feedId, "6" * 100, sessionAccount1.id.toSessionId))
    val commentId7 = Await.result(commentsDAO.create(feedId, "7" * 100, sessionAccount2.id.toSessionId))
    val commentId8 = Await.result(commentsDAO.create(feedId, "8" * 100, sessionAccount1.id.toSessionId))

    val result1 = Await.result(commentsDAO.findAll(feedId, Some(Long.MaxValue), Some(3), sessionAccount1.id.toSessionId))
    assert(result1.size == 3)
    val comment1 = result1(0)._1
    val comment2 = result1(1)._1
    val comment3 = result1(2)._1
    assert(comment1.id == commentId8)
    assert(comment2.id == commentId7)
    assert(comment3.id == commentId6)

    val result2 = Await.result(commentsDAO.findAll(feedId, Some(comment3.postedAt), Some(3), sessionAccount1.id.toSessionId))
    assert(result2.size == 3)
    val comment4 = result2(0)._1
    val comment5 = result2(1)._1
    val comment6 = result2(2)._1
    assert(comment4.id == commentId5)
    assert(comment5.id == commentId4)
    assert(comment6.id == commentId3)

    val result3 = Await.result(commentsDAO.findAll(feedId, Some(comment6.postedAt), Some(3), sessionAccount1.id.toSessionId))
    assert(result3.size == 2)
    val comment7 = result3(0)._1
    val comment8 = result3(1)._1
    assert(comment7.id == commentId2)
    assert(comment8.id == commentId1)

    val result4 = Await.result(commentsDAO.findAll(FeedId(0L), Some(Long.MaxValue), Some(3), sessionAccount1.id.toSessionId))
    assert(result4.size == 0)
  }

  test("find for push notification") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, sessionAccount1.id.toSessionId))
    val commentId1 = Await.result(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = Await.result(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))

    val comment1 = Await.result(commentsDAO.find(commentId1)).get
    val comment2 = Await.result(commentsDAO.find(commentId1)).get
    assert(comment1.id == commentId1)
    assert(comment2.id == commentId1)

    Await.result(commentsDAO.delete(commentId2, sessionAccount1.id.toSessionId))
    val comment3 = Await.result(commentsDAO.find(commentId2))
    assert(comment3.isEmpty)

  }

  test("updateNotified") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")

    val feedId = Await.result(feedsDAO.create("message", None, None, FeedPrivacyType.everyone, false, sessionAccount1.id.toSessionId))
    val commentId1 = Await.result(commentsDAO.create(feedId, "1" * 100, sessionAccount2.id.toSessionId))
    val commentId2 = Await.result(commentsDAO.create(feedId, "2" * 100, sessionAccount1.id.toSessionId))

    Await.result(commentsDAO.updateNotified(commentId1))
    Await.result(commentsDAO.updateNotified(commentId2))

    val comment1 = Await.result(commentsDAO.find(commentId1)).get
    val comment2 = Await.result(commentsDAO.find(commentId1)).get
    assert(comment1.id == commentId1)
    assert(comment2.id == commentId1)
    assert(comment1.notified == true)
    assert(comment2.notified == true)

  }

}
