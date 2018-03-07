package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.CommentFavorites

class CommentFavoritesDAOSpec extends DAOSpec {

  import db._

  val commentsDAO: CommentsDAO = injector.instance[CommentsDAO]
  val commentFavoritesDAO: CommentFavoritesDAO = injector.instance[CommentFavoritesDAO]
  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]

  test("create") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")
    val sessionAccount4 = createAccount("account3")
    val sessionAccount5 = createAccount("account4")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "01234567890" * 10, sessionAccount2.id.toSessionId))

    // create
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount5.id.toSessionId))

    val resultList1 = Await.result(db.run(query[CommentFavorites].filter(_.commentId == lift(commentId)).filter(_.by == lift(sessionAccount2.id))))
    assert(resultList1.size == 1)
    val result1 = resultList1.head
    assert(result1.commentId == commentId)
    assert(result1.by == sessionAccount2.id)
    assert(result1.postedAt > 0)

    // create
    Await.result(commentFavoritesDAO.delete(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentFavoritesDAO.delete(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentFavoritesDAO.delete(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentFavoritesDAO.delete(commentId, sessionAccount5.id.toSessionId))

    Await.result(commentFavoritesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount5.id.toSessionId))

    val resultList2 = Await.result(db.run(query[CommentFavorites].filter(_.commentId == lift(commentId)).filter(_.by == lift(sessionAccount2.id))))
    assert(resultList2.size == 1)
    val result3 = resultList2.head
    assert(result3.commentId == commentId)
    assert(result3.by == sessionAccount2.id)
    assert(result3.postedAt > 0)

  }


  test("delete") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")
    val sessionAccount4 = createAccount("account3")
    val sessionAccount5 = createAccount("account4")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "01234567890" * 10, sessionAccount2.id.toSessionId))

    Await.result(commentFavoritesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount5.id.toSessionId))

    // delete
    Await.result(commentFavoritesDAO.delete(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentFavoritesDAO.delete(commentId, sessionAccount3.id.toSessionId))

    val resultList1 = Await.result(db.run(query[CommentFavorites].filter(_.commentId == lift(commentId))))
    assert(resultList1.size == 2)

  }

  test("exist") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")
    val sessionAccount4 = createAccount("account3")
    val sessionAccount5 = createAccount("account4")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "01234567890" * 10, sessionAccount2.id.toSessionId))

    Await.result(commentFavoritesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount5.id.toSessionId))
    Await.result(commentFavoritesDAO.delete(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentFavoritesDAO.delete(commentId, sessionAccount5.id.toSessionId))

    // exists
    val result1 = Await.result(commentFavoritesDAO.exist(commentId, sessionAccount2.id.toSessionId))
    val result2 = Await.result(commentFavoritesDAO.exist(commentId, sessionAccount3.id.toSessionId))
    val result3 = Await.result(commentFavoritesDAO.exist(commentId, sessionAccount4.id.toSessionId))
    val result4 = Await.result(commentFavoritesDAO.exist(commentId, sessionAccount5.id.toSessionId))

    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == false)
    assert(result4 == false)

  }

  test("findUsers") {

    val sessionAccount1 = createAccount("account0")
    val sessionAccount2 = createAccount("account1")
    val sessionAccount3 = createAccount("account2")
    val sessionAccount4 = createAccount("account3")
    val sessionAccount5 = createAccount("account4")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "01234567890" * 10, sessionAccount2.id.toSessionId))

    // create
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentFavoritesDAO.create(commentId, sessionAccount5.id.toSessionId))

    val result = Await.result(commentFavoritesDAO.findAll(commentId, Some(Long.MaxValue), None, Some(4), sessionAccount2.id.toSessionId))
    assert(result.size == 4)

  }

}
