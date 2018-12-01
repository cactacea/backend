package io.github.cactacea.backend.core.infrastructure.dao


import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.FeedLikes

class FeedLikesDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount1 = createAccount("FeedLikesDAOSpec1")
    val sessionAccount2 = createAccount("FeedLikesDAOSpec2")
    val sessionAccount3 = createAccount("FeedLikesDAOSpec3")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = execute(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    val likeCount1 = execute(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.likeCount
    assert(likeCount1 == 2)
    val result1 = execute(db.run(query[FeedLikes].filter(_.feedId == lift(feedId)).sortBy(_.likedAt)))
    assert(result1.size == 2)
    val like1 = result1(0)
    val like2 = result1(1)
    assert(like1.feedId == feedId)
    assert(like2.feedId == feedId)
    assert(like1.by == sessionAccount2.id)
    assert(like2.by == sessionAccount3.id)

    execute(feedLikesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    val result2 = execute(db.run(query[FeedLikes].filter(_.feedId == lift(feedId)).sortBy(_.likedAt)))
    assert(result2.size == 2)
    val like3 = result2(0)
    val like4 = result2(1)
    assert(like3.feedId == feedId)
    assert(like4.feedId == feedId)
    assert(like3.by == sessionAccount2.id)
    assert(like4.by == sessionAccount3.id)
    val likeCount2 = execute(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.likeCount
    assert(likeCount2 == 2)

  }

  test("delete") {

    val sessionAccount1 = createAccount("FeedLikesDAOSpec4")
    val sessionAccount2 = createAccount("FeedLikesDAOSpec5")
    val sessionAccount3 = createAccount("FeedLikesDAOSpec6")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = execute(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))

    val likeCount1 = execute(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.likeCount
    assert(likeCount1 == 2)

    execute(feedLikesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    assert(execute(db.run(query[FeedLikes].filter(_.feedId == lift(feedId)).sortBy(_.likedAt))).size == 0)

    val likeCount2 = execute(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.likeCount
    assert(likeCount2 == 0)

    execute(feedLikesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    assert(execute(db.run(query[FeedLikes].filter(_.feedId == lift(feedId)).sortBy(_.likedAt))).size == 0)

    val feedId2 = execute(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val feedId3 = execute(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val feedId4 = execute(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    execute(feedLikesDAO.deleteLikes(sessionAccount1.id, sessionAccount2.id.toSessionId))

    assert(execute(feedLikesDAO.exist(feedId2, sessionAccount1.id.toSessionId)) == false)
    assert(execute(feedLikesDAO.exist(feedId3, sessionAccount1.id.toSessionId)) == false)
    assert(execute(feedLikesDAO.exist(feedId4, sessionAccount1.id.toSessionId)) == false)

    execute(feedLikesDAO.deleteLikes(sessionAccount3.id, sessionAccount2.id.toSessionId))
  }

  test("exist") {

    val sessionAccount1 = createAccount("FeedLikesDAOSpec7")
    val sessionAccount2 = createAccount("FeedLikesDAOSpec8")
    val sessionAccount3 = createAccount("FeedLikesDAOSpec9")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = execute(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    val result1 = execute(feedLikesDAO.exist(feedId, sessionAccount2.id.toSessionId))
    val result2 = execute(feedLikesDAO.exist(feedId, sessionAccount3.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

    execute(feedLikesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    assert(execute(feedLikesDAO.exist(feedId, sessionAccount2.id.toSessionId)) == false)
    assert(execute(feedLikesDAO.exist(feedId, sessionAccount3.id.toSessionId)) == false)
  }

  test("findAll") {

    val sessionAccount1 = createAccount("FeedLikesDAOSpec10")
    val sessionAccount2 = createAccount("FeedLikesDAOSpec11")
    val sessionAccount3 = createAccount("FeedLikesDAOSpec12")
    val sessionAccount4 = createAccount("FeedLikesDAOSpec13")
    val sessionAccount5 = createAccount("FeedLikesDAOSpec14")
    val sessionAccount6 = createAccount("FeedLikesDAOSpec15")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = execute(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.everyone, true, None, sessionAccount1.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount4.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount5.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount6.id.toSessionId))

    val result1 = execute(feedLikesDAO.findAll(None, 0, 3, sessionAccount2.id.toSessionId))
    assert(result1.size == 1)

    val result2 = execute(feedLikesDAO.findAll(sessionAccount3.id, None, 0, 3, sessionAccount2.id.toSessionId))
    assert(result2.size == 1)

    // TODO : Next Page
  }

  test("findUsers") {

    val sessionAccount1 = createAccount("FeedLikesDAOSpec16")
    val sessionAccount2 = createAccount("FeedLikesDAOSpec17")
    val sessionAccount3 = createAccount("FeedLikesDAOSpec18")
    val sessionAccount4 = createAccount("FeedLikesDAOSpec19")
    val sessionAccount5 = createAccount("FeedLikesDAOSpec20")
    val sessionAccount6 = createAccount("FeedLikesDAOSpec21")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = execute(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.everyone, true, None, sessionAccount1.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount2.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount3.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount4.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount5.id.toSessionId))
    execute(feedLikesDAO.create(feedId, sessionAccount6.id.toSessionId))

    val result1 = execute(feedLikesDAO.findAccounts(feedId, None, 0, 3, sessionAccount2.id.toSessionId))
    assert(result1.size == 3)

    val feedLike = result1(2)._3
    val result2 = execute(feedLikesDAO.findAccounts(feedId, Some(feedLike.likedAt), 0, 3, sessionAccount2.id.toSessionId))
    assert(result2.size == 2)

  }

}
