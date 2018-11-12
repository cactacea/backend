package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.github.cactacea.backend.core.infrastructure.models._

class FeedsDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount1 = createAccount("FeedsDAOSpec1")
    val sessionAccount2 = createAccount("FeedsDAOSpec2")

    val medium1 = createMedium(sessionAccount1.id)
    val medium2 = createMedium(sessionAccount1.id)
    val medium3 = createMedium(sessionAccount1.id)
    val medium4 = createMedium(sessionAccount2.id)
    val medium5 = createMedium(sessionAccount2.id)

    val message1 = "message1"
    val message2 = "message2"
    val message3 = "message3"
    val message4 = "message4"
    val message5 = "message5"
    val message6 = "message6"
    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")
    val privacyType1 = FeedPrivacyType.self
    val privacyType2 = FeedPrivacyType.friends
    val privacyType3 = FeedPrivacyType.self
    val privacyType4 = FeedPrivacyType.followers
    val privacyType5 = FeedPrivacyType.friends
    val privacyType6 = FeedPrivacyType.self
    val contentWarning1 = false
    val contentWarning2 = true
    val contentWarning3 = false
    val contentWarning4 = true
    val contentWarning5 = false
    val contentWarning6 = true

    // create feeds
    val feedId1 = execute(feedsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionAccount1.id.toSessionId))
    val feedId2 = execute(feedsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionAccount1.id.toSessionId))
    val feedId3 = execute(feedsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionAccount1.id.toSessionId))
    val feedId4 = execute(feedsDAO.create(message4, Some(mediums4), Some(tags4), privacyType4, contentWarning4, None, sessionAccount1.id.toSessionId))
    val feedId5 = execute(feedsDAO.create(message5, Some(mediums5), Some(tags5), privacyType5, contentWarning5, None, sessionAccount2.id.toSessionId))
    val feedId6 = execute(feedsDAO.create(message6, Some(mediums6), Some(tags6), privacyType6, contentWarning6, None, sessionAccount2.id.toSessionId))

    // create comments
    execute(
      db.transaction {
        for {
          _ <- commentsDAO.create(feedId1, "feed1 comment1", sessionAccount1.id.toSessionId)
          _ <- commentsDAO.create(feedId1, "feed1 comment2", sessionAccount1.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment1", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment2", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment3", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment4", sessionAccount2.id.toSessionId)
        } yield (Unit)
      }
    )

    // create feed likes
    execute(
      db.transaction {
        for {
          _ <- feedLikesDAO.create(feedId5, sessionAccount1.id.toSessionId)
          _ <- feedLikesDAO.create(feedId6, sessionAccount1.id.toSessionId)
        } yield (Unit)
      }
    )

    // create report
    execute(
      db.transaction {
        for {
          _ <- feedReportsDAO.create(feedId6, ReportType.spam, Some("report content"), sessionAccount1.id.toSessionId)
        } yield (Unit)
      }
    )

    // create feeds result
    val feed1 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId1))))).head
    val feed2 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId2))))).head
    val feed3 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId3))))).head
    val feed4 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId4))))).head
    val feed5 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId5))))).head
    val feed6 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId6))))).head
    assert((feed1.id, feed1.message, feed1.privacyType, feed1.contentWarning, feed1.by) == (feedId1, message1, privacyType1, contentWarning1, sessionAccount1.id))
    assert((feed2.id, feed2.message, feed2.privacyType, feed2.contentWarning, feed2.by) == (feedId2, message2, privacyType2, contentWarning2, sessionAccount1.id))
    assert((feed3.id, feed3.message, feed3.privacyType, feed3.contentWarning, feed3.by) == (feedId3, message3, privacyType3, contentWarning3, sessionAccount1.id))
    assert((feed4.id, feed4.message, feed4.privacyType, feed4.contentWarning, feed4.by) == (feedId4, message4, privacyType4, contentWarning4, sessionAccount1.id))
    assert((feed5.id, feed5.message, feed5.privacyType, feed5.contentWarning, feed5.by) == (feedId5, message5, privacyType5, contentWarning5, sessionAccount2.id))
    assert((feed6.id, feed6.message, feed6.privacyType, feed6.contentWarning, feed6.by) == (feedId6, message6, privacyType6, contentWarning6, sessionAccount2.id))

    // create feed tags result
    val feedTags1 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId1)))))
    val feedTags2 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId2)))))
    val feedTags3 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId3)))))
    val feedTags4 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId4)))))
    val feedTags5 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId5)))))
    val feedTags6 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId6)))))
    assert((feedTags1.size, feedTags1.map(_.name)) == (tags1.size, tags1))
    assert((feedTags2.size, feedTags2.map(_.name)) == (tags2.size, tags2))
    assert((feedTags3.size, feedTags3.map(_.name)) == (tags3.size, tags3))
    assert((feedTags4.size, feedTags4.map(_.name)) == (tags4.size, tags4))
    assert((feedTags5.size, feedTags5.map(_.name)) == (tags5.size, tags5))
    assert((feedTags6.size, feedTags6.map(_.name)) == (tags6.size, tags6))

    // create feed mediums result
    val feedMediums1 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId1)))))
    val feedMediums2 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId2)))))
    val feedMediums3 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId3)))))
    val feedMediums4 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId4)))))
    val feedMediums5 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId5)))))
    val feedMediums6 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId6)))))
    assert((feedMediums1.size, feedMediums1.map(_.mediumId)) == (mediums1.size, mediums1))
    assert((feedMediums2.size, feedMediums2.map(_.mediumId)) == (mediums2.size, mediums2))
    assert((feedMediums3.size, feedMediums3.map(_.mediumId)) == (mediums3.size, mediums3))
    assert((feedMediums4.size, feedMediums4.map(_.mediumId)) == (mediums4.size, mediums4))
    assert((feedMediums5.size, feedMediums5.map(_.mediumId)) == (mediums5.size, mediums5))
    assert((feedMediums6.size, feedMediums6.map(_.mediumId)) == (mediums6.size, mediums6))

    // Feeds count
    val session1 = execute(accountsDAO.find(sessionAccount1.id.toSessionId))
    assert(session1.head.feedsCount == 4L)

    val session2 = execute(accountsDAO.find(sessionAccount2.id.toSessionId))
    assert(session2.head.feedsCount == 2L)

  }

  test("edit") {

    val sessionAccount1 = createAccount("FeedsDAOSpec3")
    val sessionAccount2 = createAccount("FeedsDAOSpec4")

    val medium1 = createMedium(sessionAccount1.id)
    val medium2 = createMedium(sessionAccount1.id)
    val medium3 = createMedium(sessionAccount1.id)
    val medium4 = createMedium(sessionAccount2.id)
    val medium5 = createMedium(sessionAccount2.id)

    val message1 = "message1"
    val message2 = "message2"
    val message3 = "message3"
    val message4 = "message4"
    val message5 = "message5"
    val message6 = "message6"
    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")
    val privacyType1 = FeedPrivacyType.self
    val privacyType2 = FeedPrivacyType.friends
    val privacyType3 = FeedPrivacyType.self
    val privacyType4 = FeedPrivacyType.followers
    val privacyType5 = FeedPrivacyType.friends
    val privacyType6 = FeedPrivacyType.self
    val contentWarning1 = false
    val contentWarning2 = true
    val contentWarning3 = false
    val contentWarning4 = true
    val contentWarning5 = false
    val contentWarning6 = true

    // create feeds
    val feedId1 = execute(feedsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionAccount1.id.toSessionId))
    val feedId2 = execute(feedsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionAccount1.id.toSessionId))
    val feedId3 = execute(feedsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionAccount1.id.toSessionId))
    val feedId4 = execute(feedsDAO.create(message4, Some(mediums4), Some(tags4), privacyType4, contentWarning4, None, sessionAccount1.id.toSessionId))
    val feedId5 = execute(feedsDAO.create(message5, Some(mediums5), Some(tags5), privacyType5, contentWarning5, None, sessionAccount2.id.toSessionId))
    val feedId6 = execute(feedsDAO.create(message6, Some(mediums6), Some(tags6), privacyType6, contentWarning6, None, sessionAccount2.id.toSessionId))

    // create comments
    execute(
      db.transaction {
        for {
          _ <- commentsDAO.create(feedId1, "feed1 comment1", sessionAccount1.id.toSessionId)
          _ <- commentsDAO.create(feedId1, "feed1 comment2", sessionAccount1.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment1", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment2", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment3", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment4", sessionAccount2.id.toSessionId)
        } yield (Unit)
      }
    )

    // create feed likes
    execute(
      db.transaction(
        for {
          _ <- feedLikesDAO.create(feedId5, sessionAccount1.id.toSessionId)
          _ <- feedLikesDAO.create(feedId6, sessionAccount1.id.toSessionId)
        } yield (Unit)
      )
    )

    // create report
    execute(
      db.transaction(
        for {
          _ <- feedReportsDAO.create(feedId6, ReportType.spam, Some("report content"), sessionAccount1.id.toSessionId)
        } yield (Unit)
      )
    )

    // create feeds result
    val feed1 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId1))))).head
    val feed2 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId2))))).head
    val feed3 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId3))))).head
    val feed4 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId4))))).head
    val feed5 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId5))))).head
    val feed6 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId6))))).head
    assert((feed1.id, feed1.message, feed1.privacyType, feed1.contentWarning, feed1.by) == (feedId1, message1, privacyType1, contentWarning1, sessionAccount1.id))
    assert((feed2.id, feed2.message, feed2.privacyType, feed2.contentWarning, feed2.by) == (feedId2, message2, privacyType2, contentWarning2, sessionAccount1.id))
    assert((feed3.id, feed3.message, feed3.privacyType, feed3.contentWarning, feed3.by) == (feedId3, message3, privacyType3, contentWarning3, sessionAccount1.id))
    assert((feed4.id, feed4.message, feed4.privacyType, feed4.contentWarning, feed4.by) == (feedId4, message4, privacyType4, contentWarning4, sessionAccount1.id))
    assert((feed5.id, feed5.message, feed5.privacyType, feed5.contentWarning, feed5.by) == (feedId5, message5, privacyType5, contentWarning5, sessionAccount2.id))
    assert((feed6.id, feed6.message, feed6.privacyType, feed6.contentWarning, feed6.by) == (feedId6, message6, privacyType6, contentWarning6, sessionAccount2.id))

    // create feed tags result
    val feedTags1 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId1)))))
    val feedTags2 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId2)))))
    val feedTags3 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId3)))))
    val feedTags4 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId4)))))
    val feedTags5 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId5)))))
    val feedTags6 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId6)))))
    assert((feedTags1.size, feedTags1.map(_.name)) == (tags1.size, tags1))
    assert((feedTags2.size, feedTags2.map(_.name)) == (tags2.size, tags2))
    assert((feedTags3.size, feedTags3.map(_.name)) == (tags3.size, tags3))
    assert((feedTags4.size, feedTags4.map(_.name)) == (tags4.size, tags4))
    assert((feedTags5.size, feedTags5.map(_.name)) == (tags5.size, tags5))
    assert((feedTags6.size, feedTags6.map(_.name)) == (tags6.size, tags6))

    // create feed mediums result
    val feedMediums1 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId1)))))
    val feedMediums2 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId2)))))
    val feedMediums3 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId3)))))
    val feedMediums4 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId4)))))
    val feedMediums5 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId5)))))
    val feedMediums6 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId6)))))
    assert((feedMediums1.size, feedMediums1.map(_.mediumId)) == (mediums1.size, mediums1))
    assert((feedMediums2.size, feedMediums2.map(_.mediumId)) == (mediums2.size, mediums2))
    assert((feedMediums3.size, feedMediums3.map(_.mediumId)) == (mediums3.size, mediums3))
    assert((feedMediums4.size, feedMediums4.map(_.mediumId)) == (mediums4.size, mediums4))
    assert((feedMediums5.size, feedMediums5.map(_.mediumId)) == (mediums5.size, mediums5))
    assert((feedMediums6.size, feedMediums6.map(_.mediumId)) == (mediums6.size, mediums6))

    // edit
    val newMessage6 = "new message6"
    val newMediums6 = List(medium4.id, medium5.id)
    val newTags6 = List("tag4", "tag5")
    val newPrivacyType6 = FeedPrivacyType.followers
    val newContentWarning6 = false

    execute(feedsDAO.update(feedId6, newMessage6, Some(newMediums6), Some(newTags6), newPrivacyType6, newContentWarning6, None, sessionAccount2.id.toSessionId))
    val newFeed6 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId6))))).head
    val newFeedTags6 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId6)))))
    val newFeedMediums6 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId6)))))
    assert((newFeed6.id, newFeed6.message, newFeed6.privacyType, newFeed6.contentWarning, newFeed6.by) == (feedId6, newMessage6, newPrivacyType6, newContentWarning6, sessionAccount2.id))
    assert((newFeedTags6.size, newFeedTags6.map(_.name)) == (newTags6.size, newTags6))
    assert((newFeedMediums6.size, newFeedMediums6.map(_.mediumId)) == (newMediums6.size, newMediums6))

    // edit not found
    execute(feedsDAO.update(feedId6, newMessage6, Some(newMediums6), Some(newTags6), newPrivacyType6, newContentWarning6, None, sessionAccount1.id.toSessionId))

  }

  test("delete") {

    val sessionAccount1 = createAccount("FeedsDAOSpec5")
    val sessionAccount2 = createAccount("FeedsDAOSpec6")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)
    val medium4 = this.createMedium(sessionAccount2.id)
    val medium5 = this.createMedium(sessionAccount2.id)

    val message1 = "message1"
    val message2 = "message2"
    val message3 = "message3"
    val message4 = "message4"
    val message5 = "message5"
    val message6 = "message6"
    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")
    val privacyType1 = FeedPrivacyType.self
    val privacyType2 = FeedPrivacyType.friends
    val privacyType3 = FeedPrivacyType.self
    val privacyType4 = FeedPrivacyType.followers
    val privacyType5 = FeedPrivacyType.friends
    val privacyType6 = FeedPrivacyType.self
    val contentWarning1 = false
    val contentWarning2 = true
    val contentWarning3 = false
    val contentWarning4 = true
    val contentWarning5 = false
    val contentWarning6 = true

    // create feeds
    val feedId1 = execute(feedsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionAccount1.id.toSessionId))
    val feedId2 = execute(feedsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionAccount1.id.toSessionId))
    val feedId3 = execute(feedsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionAccount1.id.toSessionId))
    val feedId4 = execute(feedsDAO.create(message4, Some(mediums4), Some(tags4), privacyType4, contentWarning4, None, sessionAccount1.id.toSessionId))
    val feedId5 = execute(feedsDAO.create(message5, Some(mediums5), Some(tags5), privacyType5, contentWarning5, None, sessionAccount2.id.toSessionId))
    val feedId6 = execute(feedsDAO.create(message6, Some(mediums6), Some(tags6), privacyType6, contentWarning6, None, sessionAccount2.id.toSessionId))

    // create comments
    execute(
      db.transaction(
        for {
          _ <- commentsDAO.create(feedId1, "feed1 comment1", sessionAccount1.id.toSessionId)
          _ <- commentsDAO.create(feedId1, "feed1 comment2", sessionAccount1.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment1", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment2", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment3", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment4", sessionAccount2.id.toSessionId)
        } yield (Unit)
      )
    )

    // create feed likes
    execute(
      db.transaction(
        for {
          _ <- feedLikesDAO.create(feedId5, sessionAccount1.id.toSessionId)
          _ <- feedLikesDAO.create(feedId6, sessionAccount1.id.toSessionId)
        } yield (Unit)
      )
    )

    // create report
    execute(
      db.transaction(
        for {
          _ <- feedReportsDAO.create(feedId6, ReportType.spam, Some("report content"), sessionAccount1.id.toSessionId)
        } yield (Unit)
      )
    )

    // create feeds result
    val feed1 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId1))))).head
    val feed2 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId2))))).head
    val feed3 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId3))))).head
    val feed4 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId4))))).head
    val feed5 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId5))))).head
    val feed6 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId6))))).head
    assert((feed1.id, feed1.message, feed1.privacyType, feed1.contentWarning, feed1.by) == (feedId1, message1, privacyType1, contentWarning1, sessionAccount1.id))
    assert((feed2.id, feed2.message, feed2.privacyType, feed2.contentWarning, feed2.by) == (feedId2, message2, privacyType2, contentWarning2, sessionAccount1.id))
    assert((feed3.id, feed3.message, feed3.privacyType, feed3.contentWarning, feed3.by) == (feedId3, message3, privacyType3, contentWarning3, sessionAccount1.id))
    assert((feed4.id, feed4.message, feed4.privacyType, feed4.contentWarning, feed4.by) == (feedId4, message4, privacyType4, contentWarning4, sessionAccount1.id))
    assert((feed5.id, feed5.message, feed5.privacyType, feed5.contentWarning, feed5.by) == (feedId5, message5, privacyType5, contentWarning5, sessionAccount2.id))
    assert((feed6.id, feed6.message, feed6.privacyType, feed6.contentWarning, feed6.by) == (feedId6, message6, privacyType6, contentWarning6, sessionAccount2.id))

    // create feed tags result
    val feedTags1 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId1)))))
    val feedTags2 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId2)))))
    val feedTags3 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId3)))))
    val feedTags4 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId4)))))
    val feedTags5 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId5)))))
    val feedTags6 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId6)))))
    assert((feedTags1.size, feedTags1.map(_.name)) == (tags1.size, tags1))
    assert((feedTags2.size, feedTags2.map(_.name)) == (tags2.size, tags2))
    assert((feedTags3.size, feedTags3.map(_.name)) == (tags3.size, tags3))
    assert((feedTags4.size, feedTags4.map(_.name)) == (tags4.size, tags4))
    assert((feedTags5.size, feedTags5.map(_.name)) == (tags5.size, tags5))
    assert((feedTags6.size, feedTags6.map(_.name)) == (tags6.size, tags6))

    // create feed mediums result
    val feedMediums1 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId1)))))
    val feedMediums2 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId2)))))
    val feedMediums3 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId3)))))
    val feedMediums4 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId4)))))
    val feedMediums5 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId5)))))
    val feedMediums6 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId6)))))
    assert((feedMediums1.size, feedMediums1.map(_.mediumId)) == (mediums1.size, mediums1))
    assert((feedMediums2.size, feedMediums2.map(_.mediumId)) == (mediums2.size, mediums2))
    assert((feedMediums3.size, feedMediums3.map(_.mediumId)) == (mediums3.size, mediums3))
    assert((feedMediums4.size, feedMediums4.map(_.mediumId)) == (mediums4.size, mediums4))
    assert((feedMediums5.size, feedMediums5.map(_.mediumId)) == (mediums5.size, mediums5))
    assert((feedMediums6.size, feedMediums6.map(_.mediumId)) == (mediums6.size, mediums6))

    // delete
    execute(feedsDAO.delete(feedId6, sessionAccount2.id.toSessionId))

    val deleteResultFeeds = execute(db.run(query[Feeds].filter(_.id == lift(feedId6)).size))
    val deleteResultTags = execute(db.run(query[FeedTags].filter(_.feedId == lift(feedId6)).size))
    val deleteResultLikes = execute(db.run(query[FeedLikes].filter(_.feedId == lift(feedId6)).size))
    val deleteResultReports = execute(db.run(query[FeedReports].filter(_.feedId == lift(feedId6)).size))
    val deleteResultComments = execute(db.run(query[Comments].filter(_.feedId == lift(feedId6)).size))
    assert(deleteResultTags == 0L)
    assert(deleteResultLikes == 0L)
    assert(deleteResultReports == 0L)
    assert(deleteResultComments == 0L)

    // delete not found
    execute(feedsDAO.delete(feedId5, sessionAccount1.id.toSessionId))

    // Feeds count
    val session1 = execute(accountsDAO.find(sessionAccount1.id.toSessionId))
    assert(session1.head.feedsCount == 4L)

    val session2 = execute(accountsDAO.find(sessionAccount2.id.toSessionId))
    assert(session2.head.feedsCount == 0L)

  }


  test("exist") {

    val sessionUser = createAccount("FeedsDAOSpec7")
    val followUser = createAccount("FeedsDAOSpec8")
    val friendUser = createAccount("FeedsDAOSpec9")
    val noRelationshipUser = createAccount("FeedsDAOSpec10")

    val medium1 = createMedium(sessionUser.id)
    val medium2 = createMedium(sessionUser.id)
    val medium3 = createMedium(sessionUser.id)
    val medium4 = createMedium(sessionUser.id)
    val medium5 = createMedium(sessionUser.id)

    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")

    // create feeds
    val feedId1 = execute(feedsDAO.create("everyone" , Some(mediums1), Some(tags1), FeedPrivacyType.everyone,        false, None, sessionUser.id.toSessionId))
    val feedId2 = execute(feedsDAO.create("followers", Some(mediums2), Some(tags2), FeedPrivacyType.followers,  false, None, sessionUser.id.toSessionId))
    val feedId3 = execute(feedsDAO.create("friends"  , Some(mediums3), Some(tags3), FeedPrivacyType.friends,    false, None, sessionUser.id.toSessionId))
    val feedId4 = execute(feedsDAO.create("self"     , Some(mediums4), Some(tags4), FeedPrivacyType.self,       false, None, sessionUser.id.toSessionId))
    val feedId5 = execute(feedsDAO.create("everyone" , Some(mediums5), Some(tags5), FeedPrivacyType.everyone,        false, None, sessionUser.id.toSessionId))
    val feedId6 = execute(feedsDAO.create("followers", Some(mediums6), Some(tags6), FeedPrivacyType.followers,  false, None, sessionUser.id.toSessionId))
    val feedId7 = execute(feedsDAO.create("friends"  , Some(mediums6), Some(tags6), FeedPrivacyType.friends,    false, None, sessionUser.id.toSessionId))
    val feedId8 = execute(feedsDAO.create("self"     , Some(mediums6), Some(tags6), FeedPrivacyType.self,       false, None, sessionUser.id.toSessionId))

    // follows user
    execute(
      db.transaction {
        for {
          _ <- followsDAO.create(sessionUser.id, followUser.id.toSessionId)
          _ <- followersDAO.create(followUser.id, sessionUser.id.toSessionId)
        } yield (Unit)
      }
    )

    // friend user
    execute(
      db.transaction {
        for {
          _ <- followsDAO.create(sessionUser.id, friendUser.id.toSessionId)
          _ <- followersDAO.create(friendUser.id, sessionUser.id.toSessionId)
          _ <- friendsDAO.create(sessionUser.id, friendUser.id.toSessionId)
        } yield (Unit)
      }
    )

    // exist by follower
    assert(execute(feedsDAO.exist(feedId1, followUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId2, followUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId3, followUser.id.toSessionId)) == false)
    assert(execute(feedsDAO.exist(feedId4, followUser.id.toSessionId)) == false)
    assert(execute(feedsDAO.exist(feedId5, followUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId6, followUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId7, followUser.id.toSessionId)) == false)
    assert(execute(feedsDAO.exist(feedId8, followUser.id.toSessionId)) == false)

    // exist by friend
    assert(execute(feedsDAO.exist(feedId1, friendUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId2, friendUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId3, friendUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId4, friendUser.id.toSessionId)) == false)
    assert(execute(feedsDAO.exist(feedId5, friendUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId6, friendUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId7, friendUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId8, friendUser.id.toSessionId)) == false)

    // exist by no relationship user
    assert(execute(feedsDAO.exist(feedId1, noRelationshipUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId2, noRelationshipUser.id.toSessionId)) == false)
    assert(execute(feedsDAO.exist(feedId3, noRelationshipUser.id.toSessionId)) == false)
    assert(execute(feedsDAO.exist(feedId4, noRelationshipUser.id.toSessionId)) == false)
    assert(execute(feedsDAO.exist(feedId5, noRelationshipUser.id.toSessionId)) == true)
    assert(execute(feedsDAO.exist(feedId6, noRelationshipUser.id.toSessionId)) == false)
    assert(execute(feedsDAO.exist(feedId7, noRelationshipUser.id.toSessionId)) == false)
    assert(execute(feedsDAO.exist(feedId8, noRelationshipUser.id.toSessionId)) == false)

  }

  test("find an account's feeds") {

    val sessionAccount = createAccount("FeedsDAOSpec11")
    val followerUser = createAccount("FeedsDAOSpec12")
    val friendUser = createAccount("FeedsDAOSpec13")
    val noRelationshipUser = createAccount("FeedsDAOSpec14")

    val medium1 = this.createMedium(sessionAccount.id)
    val medium2 = this.createMedium(sessionAccount.id)
    val medium3 = this.createMedium(sessionAccount.id)
    val medium4 = this.createMedium(sessionAccount.id)
    val medium5 = this.createMedium(sessionAccount.id)

    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")

    // create feeds
    execute(
      db.transaction {
        for {
          _ <- feedsDAO.create("everyone"      , Some(mediums1), Some(tags1), FeedPrivacyType.everyone,        false, None, sessionAccount.id.toSessionId)
          _ <- feedsDAO.create("followers", Some(mediums2), Some(tags2), FeedPrivacyType.followers,  false, None, sessionAccount.id.toSessionId)
          _ <- feedsDAO.create("friends"  , Some(mediums3), Some(tags3), FeedPrivacyType.friends,    false, None, sessionAccount.id.toSessionId)
          _ <- feedsDAO.create("self"     , Some(mediums4), Some(tags4), FeedPrivacyType.self,       false, None, sessionAccount.id.toSessionId)
          _ <- feedsDAO.create("everyone"      , Some(mediums5), Some(tags5), FeedPrivacyType.everyone,        false, None, sessionAccount.id.toSessionId)
          _ <- feedsDAO.create("followers", Some(mediums6), Some(tags6), FeedPrivacyType.followers,  false, None, sessionAccount.id.toSessionId)
          _ <- feedsDAO.create("friends"  , Some(mediums6), Some(tags6), FeedPrivacyType.friends,    false, None, sessionAccount.id.toSessionId)
          _ <- feedsDAO.create("self"     , Some(mediums6), Some(tags6), FeedPrivacyType.self,       false, None, sessionAccount.id.toSessionId)
        } yield (Unit)
      }
    )

    // follows user
    execute(
      db.transaction {
        for {
          _ <- followsDAO.create(sessionAccount.id, followerUser.id.toSessionId)
          _ <- followersDAO.create(followerUser.id, sessionAccount.id.toSessionId)
        } yield (Unit)
      }
    )

    // friend user
    execute(
      db.transaction {
        for {
          _ <- followsDAO.create(sessionAccount.id, friendUser.id.toSessionId)
          _ <- friendsDAO.create(sessionAccount.id, friendUser.id.toSessionId)
          _ <- followersDAO.create(friendUser.id, sessionAccount.id.toSessionId)

        } yield (Unit)
      }
    )

    // find by follower
    assert(execute(feedsDAO.findAll(sessionAccount.id, None, None, Some(10), followerUser.id.toSessionId)).size == 4)

    // find by friend
    assert(execute(feedsDAO.findAll(sessionAccount.id, None, None, Some(10), friendUser.id.toSessionId)).size == 6)

    // find by no relationship user
    assert(execute(feedsDAO.findAll(sessionAccount.id, None, None, Some(10), noRelationshipUser.id.toSessionId)).size == 2)

  }

  test("find") {

    val sessionAccount = createAccount("FeedsDAOSpec15")
    val followerUser = createAccount("FeedsDAOSpec16")
    val friendUser = createAccount("FeedsDAOSpec17")
    val noRelationshipUser = createAccount("FeedsDAOSpec18")

    val medium1 = createMedium(sessionAccount.id)
    val medium2 = createMedium(sessionAccount.id)
    val medium3 = createMedium(sessionAccount.id)
    val medium4 = createMedium(sessionAccount.id)
    val medium5 = createMedium(sessionAccount.id)

    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")

    // create feeds
    val feedId1 = execute(feedsDAO.create("everyone"      , Some(mediums1), Some(tags1), FeedPrivacyType.everyone,        false, None, sessionAccount.id.toSessionId))
    val feedId2 = execute(feedsDAO.create("followers", Some(mediums2), Some(tags2), FeedPrivacyType.followers,  false, None, sessionAccount.id.toSessionId))
    val feedId3 = execute(feedsDAO.create("friends"  , Some(mediums3), Some(tags3), FeedPrivacyType.friends,    false, None, sessionAccount.id.toSessionId))
    val feedId4 = execute(feedsDAO.create("self"     , Some(mediums4), Some(tags4), FeedPrivacyType.self,       false, None, sessionAccount.id.toSessionId))
    val feedId5 = execute(feedsDAO.create("everyone"      , Some(mediums5), Some(tags5), FeedPrivacyType.everyone,        false, None, sessionAccount.id.toSessionId))
    val feedId6 = execute(feedsDAO.create("followers", Some(mediums6), Some(tags6), FeedPrivacyType.followers,  false, None, sessionAccount.id.toSessionId))
    val feedId7 = execute(feedsDAO.create("friends"  , Some(mediums6), Some(tags6), FeedPrivacyType.friends,    false, None, sessionAccount.id.toSessionId))
    val feedId8 = execute(feedsDAO.create("self"     , Some(mediums6), Some(tags6), FeedPrivacyType.self,       false, None, sessionAccount.id.toSessionId))

    // follows user
    execute(
      db.transaction {
        for {
          _ <- followsDAO.create(sessionAccount.id, followerUser.id.toSessionId)
          _ <- followersDAO.create(followerUser.id, sessionAccount.id.toSessionId)
        } yield (Unit)
      }
    )

    // friend user
    execute(
      db.transaction {
        for {
          _ <- followsDAO.create(sessionAccount.id, friendUser.id.toSessionId)
          _ <- friendsDAO.create(sessionAccount.id, friendUser.id.toSessionId)
          _ <- followersDAO.create(friendUser.id, sessionAccount.id.toSessionId)
        } yield (Unit)
      }
    )

    // find by follower
    assert(execute(feedsDAO.find(feedId1, followerUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId2, followerUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId3, followerUser.id.toSessionId)).isDefined == false)
    assert(execute(feedsDAO.find(feedId4, followerUser.id.toSessionId)).isDefined == false)
    assert(execute(feedsDAO.find(feedId5, followerUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId6, followerUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId7, followerUser.id.toSessionId)).isDefined == false)
    assert(execute(feedsDAO.find(feedId8, followerUser.id.toSessionId)).isDefined == false)

    // find by friend
    assert(execute(feedsDAO.find(feedId1, friendUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId2, friendUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId3, friendUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId4, friendUser.id.toSessionId)).isDefined == false)
    assert(execute(feedsDAO.find(feedId5, friendUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId6, friendUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId7, friendUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId8, friendUser.id.toSessionId)).isDefined == false)

    // find by no relationship user
    assert(execute(feedsDAO.find(feedId1, noRelationshipUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId2, noRelationshipUser.id.toSessionId)).isDefined == false)
    assert(execute(feedsDAO.find(feedId3, noRelationshipUser.id.toSessionId)).isDefined == false)
    assert(execute(feedsDAO.find(feedId4, noRelationshipUser.id.toSessionId)).isDefined == false)
    assert(execute(feedsDAO.find(feedId5, noRelationshipUser.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId6, noRelationshipUser.id.toSessionId)).isDefined == false)
    assert(execute(feedsDAO.find(feedId7, noRelationshipUser.id.toSessionId)).isDefined == false)
    assert(execute(feedsDAO.find(feedId8, noRelationshipUser.id.toSessionId)).isDefined == false)

    // find by owner
    assert(execute(feedsDAO.find(feedId1, sessionAccount.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId2, sessionAccount.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId3, sessionAccount.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId4, sessionAccount.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId5, sessionAccount.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId6, sessionAccount.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId7, sessionAccount.id.toSessionId)).isDefined == true)
    assert(execute(feedsDAO.find(feedId8, sessionAccount.id.toSessionId)).isDefined == true)

  }

  test("find session's feeds") {

    val sessionAccount1 = createAccount("FeedsDAOSpec19")
    val sessionAccount2 = createAccount("FeedsDAOSpec20")

    val medium1 = createMedium(sessionAccount1.id)
    val medium2 = createMedium(sessionAccount1.id)
    val medium3 = createMedium(sessionAccount1.id)
    val medium4 = createMedium(sessionAccount2.id)
    val medium5 = createMedium(sessionAccount2.id)

    val message1 = "message1"
    val message2 = "message2"
    val message3 = "message3"
    val message4 = "message4"
    val message5 = "message5"
    val message6 = "message6"
    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")
    val privacyType1 = FeedPrivacyType.self
    val privacyType2 = FeedPrivacyType.friends
    val privacyType3 = FeedPrivacyType.self
    val privacyType4 = FeedPrivacyType.followers
    val privacyType5 = FeedPrivacyType.friends
    val privacyType6 = FeedPrivacyType.self
    val contentWarning1 = false
    val contentWarning2 = true
    val contentWarning3 = false
    val contentWarning4 = true
    val contentWarning5 = false
    val contentWarning6 = true

    // create feeds
    val feedId1 = execute(feedsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionAccount1.id.toSessionId))
    val feedId2 = execute(feedsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionAccount1.id.toSessionId))
    val feedId3 = execute(feedsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionAccount1.id.toSessionId))
    val feedId4 = execute(feedsDAO.create(message4, Some(mediums4), Some(tags4), privacyType4, contentWarning4, None, sessionAccount1.id.toSessionId))
    val feedId5 = execute(feedsDAO.create(message5, Some(mediums5), Some(tags5), privacyType5, contentWarning5, None, sessionAccount2.id.toSessionId))
    val feedId6 = execute(feedsDAO.create(message6, None, None, privacyType6, contentWarning6, None, sessionAccount2.id.toSessionId))

    // create comments
    execute(
      db.transaction {
        for {
          _ <- commentsDAO.create(feedId1, "feed1 comment1", sessionAccount1.id.toSessionId)
          _ <- commentsDAO.create(feedId1, "feed1 comment2", sessionAccount1.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment1", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment2", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment3", sessionAccount2.id.toSessionId)
          _ <- commentsDAO.create(feedId6, "feed6 comment4", sessionAccount2.id.toSessionId)
        } yield (Unit)
      }
    )

    // create feed likes
    execute(
      db.transaction {
        for {
          _ <- feedLikesDAO.create(feedId5, sessionAccount1.id.toSessionId)
          _ <- feedLikesDAO.create(feedId6, sessionAccount1.id.toSessionId)
        } yield (Unit)
      }
    )

    // create report
    execute(
      db.transaction {
        for {
          _ <- feedReportsDAO.create(feedId6, ReportType.spam, Some("report content"), sessionAccount1.id.toSessionId)
        } yield (Unit)
      }
    )

    // create feeds result
    val feed1 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId1))))).head
    val feed2 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId2))))).head
    val feed3 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId3))))).head
    val feed4 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId4))))).head
    val feed5 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId5))))).head
    val feed6 = execute(db.run(quote(query[Feeds].filter(_.id == lift(feedId6))))).head
    assert((feed1.id, feed1.message, feed1.privacyType, feed1.contentWarning, feed1.by) == (feedId1, message1, privacyType1, contentWarning1, sessionAccount1.id))
    assert((feed2.id, feed2.message, feed2.privacyType, feed2.contentWarning, feed2.by) == (feedId2, message2, privacyType2, contentWarning2, sessionAccount1.id))
    assert((feed3.id, feed3.message, feed3.privacyType, feed3.contentWarning, feed3.by) == (feedId3, message3, privacyType3, contentWarning3, sessionAccount1.id))
    assert((feed4.id, feed4.message, feed4.privacyType, feed4.contentWarning, feed4.by) == (feedId4, message4, privacyType4, contentWarning4, sessionAccount1.id))
    assert((feed5.id, feed5.message, feed5.privacyType, feed5.contentWarning, feed5.by) == (feedId5, message5, privacyType5, contentWarning5, sessionAccount2.id))
    assert((feed6.id, feed6.message, feed6.privacyType, feed6.contentWarning, feed6.by) == (feedId6, message6, privacyType6, contentWarning6, sessionAccount2.id))

    // create feed tags result
    val feedTags1 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId1)))))
    val feedTags2 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId2)))))
    val feedTags3 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId3)))))
    val feedTags4 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId4)))))
    val feedTags5 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId5)))))
    val feedTags6 = execute(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId6)))))
    assert((feedTags1.size, feedTags1.map(_.name)) == (tags1.size, tags1))
    assert((feedTags2.size, feedTags2.map(_.name)) == (tags2.size, tags2))
    assert((feedTags3.size, feedTags3.map(_.name)) == (tags3.size, tags3))
    assert((feedTags4.size, feedTags4.map(_.name)) == (tags4.size, tags4))
    assert((feedTags5.size, feedTags5.map(_.name)) == (tags5.size, tags5))
    assert((feedTags6.size, feedTags6.map(_.name)) == (0, List[String]()))

    // create feed mediums result
    val feedMediums1 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId1)))))
    val feedMediums2 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId2)))))
    val feedMediums3 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId3)))))
    val feedMediums4 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId4)))))
    val feedMediums5 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId5)))))
    val feedMediums6 = execute(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId6)))))
    assert((feedMediums1.size, feedMediums1.map(_.mediumId)) == (mediums1.size, mediums1))
    assert((feedMediums2.size, feedMediums2.map(_.mediumId)) == (mediums2.size, mediums2))
    assert((feedMediums3.size, feedMediums3.map(_.mediumId)) == (mediums3.size, mediums3))
    assert((feedMediums4.size, feedMediums4.map(_.mediumId)) == (mediums4.size, mediums4))
    assert((feedMediums5.size, feedMediums5.map(_.mediumId)) == (mediums5.size, mediums5))
    assert((feedMediums6.size, feedMediums6.map(_.mediumId)) == (0, List[String]()))

    // find top page
    val count1 = 2
    val sessionFeeds1 = execute(feedsDAO.findAll(None, None, Some(count1), sessionAccount1.id.toSessionId))
    assert(sessionFeeds1.size == count1)
    val sessionFeed1 = sessionFeeds1(0)._1
    val sessionFeed2 = sessionFeeds1(1)._1
    val sessionFeedTags1 = sessionFeeds1(0)._2
    val sessionFeedTags2 = sessionFeeds1(1)._2
    val sessionMediums1 = sessionFeeds1(0)._3
    val sessionMediums2 = sessionFeeds1(1)._3
    assert((sessionFeed1.id, sessionFeed1.message, sessionFeed1.privacyType, sessionFeed1.contentWarning, sessionFeed1.by) == (feedId4, message4, privacyType4, contentWarning4, sessionAccount1.id))
    assert((sessionFeed2.id, sessionFeed2.message, sessionFeed2.privacyType, sessionFeed2.contentWarning, sessionFeed1.by) == (feedId3, message3, privacyType3, contentWarning3, sessionAccount1.id))
    assert((sessionFeedTags1.size, sessionFeedTags1.map(_.name)) == (tags4.size, tags4))
    assert((sessionFeedTags2.size, sessionFeedTags2.map(_.name)) == (tags3.size, tags3))
    assert((sessionMediums1.size, sessionMediums1.map(_.id)) == (mediums4.size, mediums4))
    assert((sessionMediums2.size, sessionMediums2.map(_.id)) == (mediums3.size, mediums3))

    // find 2 page
    val sessionFeeds2 = execute(feedsDAO.findAll(Some(sessionFeed2.id.value), None, Some(count1), sessionAccount1.id.toSessionId))
    assert(sessionFeeds2.size == count1)
    val sessionFeed3 = sessionFeeds2(0)._1
    val sessionFeed4 = sessionFeeds2(1)._1
    val sessionFeedTags3 = sessionFeeds2(0)._2
    val sessionFeedTags4 = sessionFeeds2(1)._2
    val sessionMediums3 = sessionFeeds2(0)._3
    val sessionMediums4 = sessionFeeds2(1)._3
    assert((sessionFeed3.id, sessionFeed3.message, sessionFeed3.privacyType, sessionFeed3.contentWarning, sessionFeed3.by) == (feedId2, message2, privacyType2, contentWarning2, sessionAccount1.id))
    assert((sessionFeed4.id, sessionFeed4.message, sessionFeed4.privacyType, sessionFeed4.contentWarning, sessionFeed4.by) == (feedId1, message1, privacyType1, contentWarning1, sessionAccount1.id))
    assert((sessionFeedTags3.size, sessionFeedTags3.map(_.name)) == (tags2.size, tags2))
    assert((sessionFeedTags4.size, sessionFeedTags4.map(_.name)) == (tags1.size, tags1))
    assert((sessionMediums3.size, sessionMediums3.map(_.id)) == (mediums2.size, mediums2))
    assert((sessionMediums4.size, sessionMediums4.map(_.id)) == (mediums1.size, mediums1))

  }


  test("find for push notification") {

    val sessionAccount1 = createAccount("FeedsDAOSpec21")
    val sessionAccount2 = createAccount("FeedsDAOSpec22")

    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)

    val message1 = "message1"
    val message2 = "message2"
    val message3 = "message3"
    val message4 = "message4"
    val message5 = "message5"
    val message6 = "message6"
    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")
    val privacyType1 = FeedPrivacyType.self
    val privacyType2 = FeedPrivacyType.friends
    val privacyType3 = FeedPrivacyType.self
    val privacyType4 = FeedPrivacyType.followers
    val privacyType5 = FeedPrivacyType.friends
    val privacyType6 = FeedPrivacyType.self
    val contentWarning1 = false
    val contentWarning2 = true
    val contentWarning3 = false
    val contentWarning4 = true
    val contentWarning5 = false
    val contentWarning6 = true

    // create feeds
    val feedId1 = execute(feedsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionAccount1.id.toSessionId))
    val feedId2 = execute(feedsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionAccount1.id.toSessionId))
    val feedId3 = execute(feedsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionAccount1.id.toSessionId))
    val feedId4 = execute(feedsDAO.create(message4, Some(mediums4), Some(tags4), privacyType4, contentWarning4, None, sessionAccount1.id.toSessionId))
    val feedId5 = execute(feedsDAO.create(message5, Some(mediums5), Some(tags5), privacyType5, contentWarning5, None, sessionAccount2.id.toSessionId))
    val feedId6 = execute(feedsDAO.create(message6, Some(mediums6), Some(tags6), privacyType6, contentWarning6, None, sessionAccount2.id.toSessionId))


    val result1 = execute(feedsDAO.find(feedId1))
    val result2 = execute(feedsDAO.find(feedId2))
    val result3 = execute(feedsDAO.find(feedId3))
    val result4 = execute(feedsDAO.find(feedId4))
    val result5 = execute(feedsDAO.find(feedId5))
    val result6 = execute(feedsDAO.find(feedId6))

    assert(result1.isDefined == true)
    assert(result2.isDefined == true)
    assert(result3.isDefined == true)
    assert(result4.isDefined == true)
    assert(result5.isDefined == true)
    assert(result6.isDefined == true)

    val feed1 = result1.head
    val feed2 = result2.head
    val feed3 = result3.head
    val feed4 = result4.head
    val feed5 = result5.head
    val feed6 = result6.head

    assert(feed1.by == sessionAccount1.id)
    assert(feed2.by == sessionAccount1.id)
    assert(feed3.by == sessionAccount1.id)
    assert(feed4.by == sessionAccount1.id)
    assert(feed5.by == sessionAccount2.id)
    assert(feed6.by == sessionAccount2.id)


  }


  test("updateNotified") {

    val sessionAccount = createAccount("FeedsDAOSpec23")

    val medium1 = createMedium(sessionAccount.id)
    val medium2 = createMedium(sessionAccount.id)
    val medium3 = createMedium(sessionAccount.id)

    val message1 = "message1"
    val message2 = "message2"
    val message3 = "message3"
    val message4 = "message4"
    val message5 = "message5"
    val message6 = "message6"
    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")
    val privacyType1 = FeedPrivacyType.self
    val privacyType2 = FeedPrivacyType.friends
    val privacyType3 = FeedPrivacyType.self
    val privacyType4 = FeedPrivacyType.followers
    val privacyType5 = FeedPrivacyType.friends
    val privacyType6 = FeedPrivacyType.self
    val contentWarning1 = false
    val contentWarning2 = true
    val contentWarning3 = false
    val contentWarning4 = true
    val contentWarning5 = false
    val contentWarning6 = true

    // create feeds
    val feedId1 = execute(feedsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionAccount.id.toSessionId))
    val feedId2 = execute(feedsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionAccount.id.toSessionId))
    val feedId3 = execute(feedsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionAccount.id.toSessionId))

    execute(
      db.transaction {
        for {
          _ <- feedsDAO.updateNotified(feedId1, true)
          _ <- feedsDAO.updateNotified(feedId2, false)
          _ <- feedsDAO.updateNotified(feedId3, true)
        } yield (Unit)
      }
    )

    val result1 = execute(feedsDAO.find(feedId1))
    val result2 = execute(feedsDAO.find(feedId2))
    val result3 = execute(feedsDAO.find(feedId3))

    assert(result1.isDefined == true)
    assert(result2.isDefined == true)
    assert(result3.isDefined == true)

    val feed1 = result1.head
    val feed2 = result2.head
    val feed3 = result3.head

    assert(feed1.notified == true)
    assert(feed2.notified == false)
    assert(feed3.notified == true)

  }

  test("updateDelivered") {

    val sessionAccount = createAccount("FeedsDAOSpec24")

    val medium1 = this.createMedium(sessionAccount.id)
    val medium2 = this.createMedium(sessionAccount.id)
    val medium3 = this.createMedium(sessionAccount.id)

    val message1 = "message1"
    val message2 = "message2"
    val message3 = "message3"
    val message4 = "message4"
    val message5 = "message5"
    val message6 = "message6"
    val mediums1 = List[MediumId]()
    val mediums2 = List(medium1.id)
    val mediums3 = List(medium1.id, medium2.id)
    val mediums4 = List(medium1.id, medium2.id, medium3.id)
    val mediums5 = List(medium1.id, medium2.id)
    val mediums6 = List(medium1.id, medium2.id, medium3.id)
    val tags1 = List[String]()
    val tags2 = List("tag1")
    val tags3 = List("tag1", "tag2")
    val tags4 = List("tag1", "tag2", "tag3")
    val tags5 = List("tag1")
    val tags6 = List("tag1", "tag2", "tag3")
    val privacyType1 = FeedPrivacyType.self
    val privacyType2 = FeedPrivacyType.friends
    val privacyType3 = FeedPrivacyType.self
    val privacyType4 = FeedPrivacyType.followers
    val privacyType5 = FeedPrivacyType.friends
    val privacyType6 = FeedPrivacyType.self
    val contentWarning1 = false
    val contentWarning2 = true
    val contentWarning3 = false
    val contentWarning4 = true
    val contentWarning5 = false
    val contentWarning6 = true

    // create feeds
    val feedId1 = execute(feedsDAO.create(message1, Some(mediums1), Some(tags1), privacyType1, contentWarning1, None, sessionAccount.id.toSessionId))
    val feedId2 = execute(feedsDAO.create(message2, Some(mediums2), Some(tags2), privacyType2, contentWarning2, None, sessionAccount.id.toSessionId))
    val feedId3 = execute(feedsDAO.create(message3, Some(mediums3), Some(tags3), privacyType3, contentWarning3, None, sessionAccount.id.toSessionId))

    val result1 = execute(feedsDAO.find(feedId1))
    val result2 = execute(feedsDAO.find(feedId2))
    val result3 = execute(feedsDAO.find(feedId3))

    assert(result1.isDefined == true)
    assert(result2.isDefined == true)
    assert(result3.isDefined == true)

  }


}

