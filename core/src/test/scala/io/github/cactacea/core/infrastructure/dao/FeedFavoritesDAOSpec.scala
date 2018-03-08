package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.FeedFavorites

class FeedFavoritesDAOSpec extends DAOSpec {

  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]
  val feedFavoritesDAO: FeedFavoritesDAO = injector.instance[FeedFavoritesDAO]

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
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount3.id.toSessionId))
    val favoriteCount1 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.favoriteCount
    assert(favoriteCount1 == 2)
    val result1 = Await.result(db.run(query[FeedFavorites].sortBy(_.postedAt)))
    assert(result1.size == 2)
    val favorite1 = result1(0)
    val favorite2 = result1(1)
    assert(favorite1.feedId == feedId)
    assert(favorite2.feedId == feedId)
    assert(favorite1.by == sessionAccount2.id)
    assert(favorite2.by == sessionAccount3.id)

    Await.result(feedFavoritesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount3.id.toSessionId))
    val result2 = Await.result(db.run(query[FeedFavorites].sortBy(_.postedAt)))
    assert(result2.size == 2)
    val favorite3 = result2(0)
    val favorite4 = result2(1)
    assert(favorite3.feedId == feedId)
    assert(favorite4.feedId == feedId)
    assert(favorite3.by == sessionAccount2.id)
    assert(favorite4.by == sessionAccount3.id)
    val favoriteCount2 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.favoriteCount
    assert(favoriteCount2 == 2)

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
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount3.id.toSessionId))

    val favoriteCount1 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.favoriteCount
    assert(favoriteCount1 == 2)

    Await.result(feedFavoritesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    assert(Await.result(db.run(query[FeedFavorites].sortBy(_.postedAt))).size == 0)

    val favoriteCount2 = Await.result(feedsDAO.find(feedId, sessionAccount1.id.toSessionId)).head._1.favoriteCount
    assert(favoriteCount2 == 0)

    Await.result(feedFavoritesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    assert(Await.result(db.run(query[FeedFavorites].sortBy(_.postedAt))).size == 0)

    val feedId2 = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val feedId3 = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val feedId4 = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    Await.result(feedFavoritesDAO.deleteFavorites(sessionAccount1.id, sessionAccount2.id.toSessionId))

    assert(Await.result(feedFavoritesDAO.exist(feedId2, sessionAccount1.id.toSessionId)) == false)
    assert(Await.result(feedFavoritesDAO.exist(feedId3, sessionAccount1.id.toSessionId)) == false)
    assert(Await.result(feedFavoritesDAO.exist(feedId4, sessionAccount1.id.toSessionId)) == false)

    val result = Await.result(feedFavoritesDAO.deleteFavorites(sessionAccount3.id, sessionAccount2.id.toSessionId))
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
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount3.id.toSessionId))
    val result1 = Await.result(feedFavoritesDAO.exist(feedId, sessionAccount2.id.toSessionId))
    val result2 = Await.result(feedFavoritesDAO.exist(feedId, sessionAccount3.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

    Await.result(feedFavoritesDAO.delete(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.delete(feedId, sessionAccount3.id.toSessionId))
    assert(Await.result(feedFavoritesDAO.exist(feedId, sessionAccount2.id.toSessionId)) == false)
    assert(Await.result(feedFavoritesDAO.exist(feedId, sessionAccount3.id.toSessionId)) == false)
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
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount3.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount4.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount5.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount6.id.toSessionId))

    val result1 = Await.result(feedFavoritesDAO.findAll(None, None, Some(3), sessionAccount2.id.toSessionId))
    assert(result1.size == 1)

    val result2 = Await.result(feedFavoritesDAO.findAll(sessionAccount3.id, None, None, Some(3), sessionAccount2.id.toSessionId))
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
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount3.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount4.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount5.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId, sessionAccount6.id.toSessionId))

    val result1 = Await.result(feedFavoritesDAO.findAccounts(feedId, None, None, Some(3), sessionAccount2.id.toSessionId))
    assert(result1.size == 3)

    val result2 = Await.result(feedFavoritesDAO.findAccounts(feedId, Some(result1(2)._1.position), None, Some(3), sessionAccount2.id.toSessionId))
    assert(result2.size == 2)

  }

}
