package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.CommentLikes

class CommentLikesDAOSpec extends DAOSpec {

  import db._

  val commentsDAO: CommentsDAO = injector.instance[CommentsDAO]
  val commentLikesDAO: CommentLikesDAO = injector.instance[CommentLikesDAO]
  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]

  test("create") {

    val sessionAccount1 = createAccount("CommentLikesDAOSpec0")
    val sessionAccount2 = createAccount("CommentLikesDAOSpec1")
    val sessionAccount3 = createAccount("CommentLikesDAOSpec2")
    val sessionAccount4 = createAccount("CommentLikesDAOSpec3")
    val sessionAccount5 = createAccount("CommentLikesDAOSpec4")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "01234567890" * 10, sessionAccount2.id.toSessionId))

    // create
    Await.result(commentLikesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount5.id.toSessionId))

    val resultList1 = Await.result(db.run(query[CommentLikes].filter(_.commentId == lift(commentId)).filter(_.by == lift(sessionAccount2.id))))
    assert(resultList1.size == 1)
    val result1 = resultList1.head
    assert(result1.commentId == commentId)
    assert(result1.by == sessionAccount2.id)
    assert(result1.postedAt > 0)

    // create
    Await.result(commentLikesDAO.delete(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentLikesDAO.delete(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentLikesDAO.delete(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentLikesDAO.delete(commentId, sessionAccount5.id.toSessionId))

    Await.result(commentLikesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount5.id.toSessionId))

    val resultList2 = Await.result(db.run(query[CommentLikes].filter(_.commentId == lift(commentId)).filter(_.by == lift(sessionAccount2.id))))
    assert(resultList2.size == 1)
    val result3 = resultList2.head
    assert(result3.commentId == commentId)
    assert(result3.by == sessionAccount2.id)
    assert(result3.postedAt > 0)

  }


  test("delete") {

    val sessionAccount1 = createAccount("CommentLikesDAOSpec5")
    val sessionAccount2 = createAccount("CommentLikesDAOSpec6")
    val sessionAccount3 = createAccount("CommentLikesDAOSpec7")
    val sessionAccount4 = createAccount("CommentLikesDAOSpec8")
    val sessionAccount5 = createAccount("CommentLikesDAOSpec9")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "01234567890" * 10, sessionAccount2.id.toSessionId))

    Await.result(commentLikesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount5.id.toSessionId))

    // delete
    Await.result(commentLikesDAO.delete(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentLikesDAO.delete(commentId, sessionAccount3.id.toSessionId))

    val resultList1 = Await.result(db.run(query[CommentLikes].filter(_.commentId == lift(commentId))))
    assert(resultList1.size == 2)

  }

  test("exist") {

    val sessionAccount1 = createAccount("CommentLikesDAOSpec10")
    val sessionAccount2 = createAccount("CommentLikesDAOSpec11")
    val sessionAccount3 = createAccount("CommentLikesDAOSpec12")
    val sessionAccount4 = createAccount("CommentLikesDAOSpec13")
    val sessionAccount5 = createAccount("CommentLikesDAOSpec14")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "01234567890" * 10, sessionAccount2.id.toSessionId))

    Await.result(commentLikesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount5.id.toSessionId))
    Await.result(commentLikesDAO.delete(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentLikesDAO.delete(commentId, sessionAccount5.id.toSessionId))

    // exists
    val result1 = Await.result(commentLikesDAO.exist(commentId, sessionAccount2.id.toSessionId))
    val result2 = Await.result(commentLikesDAO.exist(commentId, sessionAccount3.id.toSessionId))
    val result3 = Await.result(commentLikesDAO.exist(commentId, sessionAccount4.id.toSessionId))
    val result4 = Await.result(commentLikesDAO.exist(commentId, sessionAccount5.id.toSessionId))

    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == false)
    assert(result4 == false)

  }

  test("findUsers") {

    val sessionAccount1 = createAccount("CommentLikesDAOSpec15")
    val sessionAccount2 = createAccount("CommentLikesDAOSpec16")
    val sessionAccount3 = createAccount("CommentLikesDAOSpec17")
    val sessionAccount4 = createAccount("CommentLikesDAOSpec18")
    val sessionAccount5 = createAccount("CommentLikesDAOSpec19")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val mediums1 = List(medium1.id, medium2.id)
    val tags = List("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10")
    val feedId = Await.result(feedsDAO.create("01234567890" * 10, Some(mediums1), Some(tags), FeedPrivacyType.self, true, None, sessionAccount1.id.toSessionId))
    val commentId = Await.result(commentsDAO.create(feedId, "01234567890" * 10, sessionAccount2.id.toSessionId))

    // create
    Await.result(commentLikesDAO.create(commentId, sessionAccount2.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount3.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount4.id.toSessionId))
    Await.result(commentLikesDAO.create(commentId, sessionAccount5.id.toSessionId))

    val result = Await.result(commentLikesDAO.findAll(commentId, Some(-1L), None, Some(4), sessionAccount2.id.toSessionId))
    assert(result.size == 4)

  }

}
