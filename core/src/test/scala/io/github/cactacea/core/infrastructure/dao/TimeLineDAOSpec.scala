package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.core.helpers.CactaceaDAOTest
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, TimelineFeedId}
import io.github.cactacea.core.infrastructure.models.Timelines

class TimeLineDAOSpec extends CactaceaDAOTest {

  val friendsDAO: FriendsDAO = injector.instance[FriendsDAO]
  val followsDAO: FollowsDAO = injector.instance[FollowsDAO]
  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]
  val feedFavoritesDAO: FeedFavoritesDAO = injector.instance[FeedFavoritesDAO]
  val feedReportsDAO: FeedReportsDAO = injector.instance[FeedReportsDAO]
  val commentsDAO: CommentsDAO = injector.instance[CommentsDAO]
  val timeLineDAO: TimeLineDAO = injector.instance[TimeLineDAO]
  val blocksDAO: BlocksDAO = injector.instance[BlocksDAO]
  val identifiesDAO: IdentifiesDAO = injector.instance[IdentifiesDAO]
  import db._

  test("create") {

    val sessionAccount1 = this.createAccount(0L)
    val sessionAccount2 = this.createAccount(1L)
    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.self
    val contentWarning = true

    val feedId6 = Await.result(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment1", sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment2", sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment3", sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment4", sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId6, sessionAccount1.id.toSessionId))
    Await.result(feedReportsDAO.create(feedId6, ReportType.spam, sessionAccount1.id.toSessionId))

    // create friendsDAO
    Await.result(friendsDAO.create(sessionAccount2.id, sessionAccount1.id.toSessionId))

    // create timeline
    Await.result(timeLineDAO.create(feedId6, sessionAccount2.id.toSessionId))

    val q = quote { query[Timelines].size}
    val result = Await.result(db.run(q))
    assert(result == 1)

  }

  test("delete") {

    val sessionAccount1 = this.createAccount(0L)
    val sessionAccount2 = this.createAccount(1L)
    val sessionAccount3 = this.createAccount(2L)
    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.self
    val contentWarning = true

    val feedId6 = Await.result(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment1", sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment2", sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment3", sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment4", sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId6, sessionAccount1.id.toSessionId))
    Await.result(feedReportsDAO.create(feedId6, ReportType.spam, sessionAccount1.id.toSessionId))

    // create follow
    Await.result(friendsDAO.create(sessionAccount1.id, sessionAccount2.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount1.id, sessionAccount3.id.toSessionId))

    // create timeline
    Await.result(timeLineDAO.create(feedId6, sessionAccount1.id.toSessionId))

    // delete timeline
    assert(Await.result(timeLineDAO.delete(sessionAccount2.id, sessionAccount1.id.toSessionId)) == true)

    val result = Await.result(timeLineDAO.findAll(None, None, Some(2), sessionAccount2.id.toSessionId))
    assert(result.size == 0)

  }

  test("findAll") {

    val sessionAccount1 = this.createAccount(0L)
    val sessionAccount2 = this.createAccount(1L)
    val sessionAccount3 = this.createAccount(2L)
    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.self
    val contentWarning = true

    val feedId6 = Await.result(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment1", sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment2", sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment3", sessionAccount2.id.toSessionId))
    Await.result(commentsDAO.create(feedId6, "feed6 comment4", sessionAccount2.id.toSessionId))
    Await.result(feedFavoritesDAO.create(feedId6, sessionAccount1.id.toSessionId))
    Await.result(feedReportsDAO.create(feedId6, ReportType.spam, sessionAccount1.id.toSessionId))

    // create follow
    Await.result(friendsDAO.create(sessionAccount1.id, sessionAccount2.id.toSessionId))
    Await.result(friendsDAO.create(sessionAccount1.id, sessionAccount3.id.toSessionId))

    // create timeline
    Await.result(timeLineDAO.create(feedId6, sessionAccount1.id.toSessionId))

    val id = Await.result(identifiesDAO.create().map(TimelineFeedId(_)))
    val by: Option[AccountId] = Some(sessionAccount1.id)
    val postedAt = System.currentTimeMillis()
    val accountId = sessionAccount3.id
    val q = quote {
      query[Timelines].insert(
        _.id -> lift(id),
        _.by -> lift(by),
        _.postedAt  -> lift(postedAt),
        _.accountId -> lift(accountId)
      )
    }
    Await.result(db.run(q))

    // delete timeline
    assert(Await.result(timeLineDAO.delete(sessionAccount2.id, sessionAccount1.id.toSessionId)) == true)

    val result = Await.result(timeLineDAO.findAll(None, None, Some(2), sessionAccount3.id.toSessionId))
    assert(result.size == 2)

  }

}
