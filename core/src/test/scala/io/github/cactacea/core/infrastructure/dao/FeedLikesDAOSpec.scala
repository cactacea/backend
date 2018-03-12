package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.FeedLikes

class FeedLikesDAOSpec extends DAOSpec {

  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]
  val feedLikesDAO: FeedLikesDAO = injector.instance[FeedLikesDAO]

  import db._

  test("create") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    val likeCount1 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.likeCount
    assert(likeCount1 == 2)
    val result1 = Await.result(db.run(query[FeedLikes].sortBy(_.postedAt)))
    assert(result1.size == 2)
    val like1 = result1(0)
    val like2 = result1(1)
    assert(like1.feedId == feedId)
    assert(like2.feedId == feedId)
    assert(like1.by == sessionAccount2.id)
    assert(like2.by == sessionAccount3.id)

    Await.result(feedLikesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    val result2 = Await.result(db.run(query[FeedLikes].sortBy(_.postedAt)))
    assert(result2.size == 2)
    val like3 = result2(0)
    val like4 = result2(1)
    assert(like3.feedId == feedId)
    assert(like4.feedId == feedId)
    assert(like3.by == sessionAccount2.id)
    assert(like4.by == sessionAccount3.id)
    val likeCount2 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.likeCount
    assert(likeCount2 == 2)

  }

  test("delete") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))

    val likeCount1 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.likeCount
    assert(likeCount1 == 2)

    Await.result(feedLikesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    assert(Await.result(db.run(query[FeedLikes].sortBy(_.postedAt))).size == 0)

    val likeCount2 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.likeCount
    assert(likeCount2 == 0)

    Await.result(feedLikesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    assert(Await.result(db.run(query[FeedLikes].sortBy(_.postedAt))).size == 0)

    val feedId2 = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val feedId3 = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val feedId4 = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    Await.result(feedLikesDAO.deleteLikes(sessionAccount1.id, sessionAccount2.id.toSessionId))

    assert(Await.result(feedLikesDAO.exist(feedId2, sessionAccount1.id.toSessionId)) == false)
    assert(Await.result(feedLikesDAO.exist(feedId3, sessionAccount1.id.toSessionId)) == false)
    assert(Await.result(feedLikesDAO.exist(feedId4, sessionAccount1.id.toSessionId)) == false)

    val result = Await.result(feedLikesDAO.deleteLikes(sessionAccount3.id, sessionAccount2.id.toSessionId))
    assert(result == true)
  }

  test("exist") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    val result1 = Await.result(feedLikesDAO.exist(feedId, sessionAccount2.id.toSessionId))
    val result2 = Await.result(feedLikesDAO.exist(feedId, sessionAccount3.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

    Await.result(feedLikesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    assert(Await.result(feedLikesDAO.exist(feedId, sessionAccount2.id.toSessionId)) == false)
    assert(Await.result(feedLikesDAO.exist(feedId, sessionAccount3.id.toSessionId)) == false)
  }

  test("findAll") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")
    val sessionAccount4 = createAccount("account3")
    val sessionAccount5 = createAccount("account4")
    val sessionAccount6 = createAccount("account5")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.everyone, true, None, sessionAccount1.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount4.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount5.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount6.id.toSessionId))

    val result1 = Await.result(feedLikesDAO.findAll(None, None, Some(3), sessionAccount2.id.toSessionId))
    assert(result1.size == 1)

    val result2 = Await.result(feedLikesDAO.findAll(sessionAccount3.id, None, None, Some(3), sessionAccount2.id.toSessionId))
    assert(result2.size == 1)

  }

  test("findUsers") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")
    val sessionAccount4 = createAccount("account3")
    val sessionAccount5 = createAccount("account4")
    val sessionAccount6 = createAccount("account5")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.everyone, true, None, sessionAccount1.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount4.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount5.id.toSessionId))
    Await.result(feedLikesDAO.create(feedId, sessionAccount6.id.toSessionId))

    val result1 = Await.result(feedLikesDAO.findAccounts(feedId, None, None, Some(3), sessionAccount2.id.toSessionId))
    assert(result1.size == 3)

    val result2 = Await.result(feedLikesDAO.findAccounts(feedId, Some(result1(2)._3), None, Some(3), sessionAccount2.id.toSessionId))
    assert(result2.size == 2)

  }

}
