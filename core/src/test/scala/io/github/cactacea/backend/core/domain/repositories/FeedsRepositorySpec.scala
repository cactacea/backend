package io.github.cactacea.backend.core.domain.repositories


import java.util.Locale

import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, MediumId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AuthorityNotFound, MediumNotFound}

class FeedsRepositorySpec extends RepositorySpec {

  feature("create") {
    scenario("should create a feed") {
      forOne(userGen, everyoneFeedGen, medium5SeqOptGen) { (a, f, l) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val ids = l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val feedId = await(feedsRepository.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result1 = await(feedsRepository.find(feedId, sessionId))
        assert(result1.message == f.message)
        assert(result1.warning == f.contentWarning)
        assert(result1.mediums.map(_.id) == ids.getOrElse(Seq[MediumId]()))
        assert(result1.tags == tags)

        tags.map(_.foreach({ name =>
          assertFutureValue(existsFeedTag(feedId, name), true)
        }))

        ids.map(_.foreach({ mediumId =>
          assertFutureValue(existsFeedMedium(feedId, mediumId), true)
        }))

      }
    }

    scenario("should create an user feed and a notification") {
      forOne(userGen, userGen, followerFeedGen) { (s, a, f) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user = await(createUser(a.userName))
        val userId = user.id
        await(followsRepository.create(sessionId.userId, userId.sessionId))
        val feedId = await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result1 = await(feedsRepository.find(None, 0, 10, None, userId.sessionId))
        assert(result1.size == 1)
        assert(result1.headOption.exists(_.id == feedId))

        val result = await(notificationsRepository.find(None, 0, 10, Seq(Locale.getDefault()), userId.sessionId))
        assert(result.size == 1)
        assert(result.headOption.exists(_.contentId.exists(_ == feedId.value)))
        assert(result.headOption.exists(_.notificationType == NotificationType.feed))
      }
    }

    scenario("should return exception if medium not exist") {
      forOne(userGen, everyoneFeedGen) { (a, f) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(feedsRepository.create(f.message, Option(Seq(MediumId(0L))), None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        }.error == MediumNotFound)

      }
    }
  }


  feature("update") {
    scenario("should update a feed") {

      forOne(userGen, everyoneFeedGen, everyoneFeedGen, medium5SeqOptGen, medium5SeqOptGen) { (a, f, f2, l, l2) =>
        val sessionId = await(createUser(a.userName)).id.sessionId
        val ids = l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val feedId = await(feedsRepository.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))


        val ids2 = l2.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags2 = f2.tags.map(_.split(' ').toSeq)
        await(feedsRepository.update(feedId, f2.message, ids2, tags2, f2.privacyType, f2.contentWarning, f2.expiration, sessionId))

        // result
        val result1 = await(feedsRepository.find(feedId, sessionId))
        assert(result1.message == f2.message)
        assert(result1.warning == f2.contentWarning)
        assert(result1.mediums.map(_.id) == ids2.getOrElse(Seq[MediumId]()))
        assert(result1.tags == tags2)

      }
    }

    scenario("should return exception if medium not exist") {
      forOne(userGen, everyoneFeedGen, medium5SeqOptGen) { (a, f, l) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val ids = l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val feedId = await(feedsRepository.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(feedsRepository.update(feedId, f.message, Option(Seq(MediumId(0))), tags, f.privacyType, f.contentWarning, f.expiration, sessionId))
        }.error == MediumNotFound)

      }
    }

    scenario("should return exception if feed not exist") {
      forOne(userGen, everyoneFeedGen, medium5SeqOptGen) { (a, f, l) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)

        // result
        assert(intercept[CactaceaException] {
          await(feedsRepository.update(FeedId(0), f.message, None, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))
        }.error == AuthorityNotFound)

      }
    }
  }

  feature("delete") {
    scenario("should delete a feed") {
      forOne(userGen, everyoneFeedGen) { (a, f) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val feedId = await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        assertFutureValue(feedsDAO.own(feedId, sessionId), true)
        await(feedsRepository.delete(feedId, sessionId))
        assertFutureValue(feedsDAO.own(feedId, sessionId), false)

      }
    }

    scenario("should delete comments on a feed") {
      forOne(userGen, everyoneFeedGen, commentGen) { (a, f, c) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val feedId = await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))

        // result
        assertFutureValue(feedsDAO.own(feedId, sessionId), true)
        assertFutureValue(commentsDAO.own(commentId, sessionId), true)
        await(feedsRepository.delete(feedId, sessionId))
        assertFutureValue(feedsDAO.own(feedId, sessionId), false)
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)

      }
    }

    scenario("should delete likes on a feed") {
      forOne(userGen, userGen, everyoneFeedGen) { (s, a, f) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user = await(createUser(a.userName))
        val userId = user.id
        val feedId = await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(feedLikesRepository.create(feedId, userId.sessionId))

        // result
        assertFutureValue(feedsDAO.own(feedId, sessionId), true)
        assertFutureValue(feedLikesDAO.own(feedId, userId.sessionId), true)
        await(feedsRepository.delete(feedId, sessionId))
        assertFutureValue(feedsDAO.own(feedId, sessionId), false)
        assertFutureValue(feedLikesDAO.own(feedId, userId.sessionId), false)

      }
    }

    scenario("should delete reports on a feed") {
      forOne(userGen, everyoneFeedGen, feedReportGen) { (a, f, r) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val feedId = await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(feedsRepository.report(feedId, r.reportType, r.reportContent, sessionId))

        // result
        assertFutureValue(feedsDAO.own(feedId, sessionId), true)
        assertFutureValue(existsFeedReport(feedId, sessionId), true)
        await(feedsRepository.delete(feedId, sessionId))
        assertFutureValue(feedsDAO.own(feedId, sessionId), false)
        assertFutureValue(existsFeedReport(feedId, sessionId), false)

      }
    }


    scenario("should delete reports on comments on a feed") {
      forOne(userGen, everyoneFeedGen, commentGen, commentReportGen) { (a, f, c, r) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val feedId = await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))
        await(commentsRepository.report(commentId, r.reportType, r.reportContent, sessionId))

        // result
        assertFutureValue(feedsDAO.own(feedId, sessionId), true)
        assertFutureValue(commentsDAO.own(commentId, sessionId), true)
        assertFutureValue(existsCommentReport(commentId, sessionId), true)
        await(feedsRepository.delete(feedId, sessionId))
        assertFutureValue(feedsDAO.own(feedId, sessionId), false)
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
        assertFutureValue(existsCommentReport(commentId, sessionId), false)

      }
    }

    scenario("should delete likes on comments on a feed") {
      forOne(userGen, userGen, everyoneFeedGen, commentGen) { (s, a, f, c) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user = await(createUser(a.userName))
        val userId = user.id
        val feedId = await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))
        await(commentLikesRepository.create(commentId, userId.sessionId))

        // result
        assertFutureValue(feedsDAO.own(feedId, sessionId), true)
        assertFutureValue(commentsDAO.own(commentId, sessionId), true)
        assertFutureValue(commentLikesDAO.own(commentId, userId.sessionId), true)
        await(feedsRepository.delete(feedId, sessionId))
        assertFutureValue(feedsDAO.own(feedId, sessionId), false)
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
        assertFutureValue(commentLikesDAO.own(commentId, userId.sessionId), false)

      }
    }

    scenario("should delete tags and meidums on a feed") {
      forOne(userGen, everyoneFeedGen, medium5SeqOptGen) { (a, f, l) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val ids = l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val feedId = await(feedsRepository.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        await(feedsRepository.find(feedId, sessionId))
        assertFutureValue(feedsDAO.own(feedId, sessionId), true)
        tags.map(_.foreach({ name =>
          assertFutureValue(existsFeedTag(feedId, name), true)
        }))

        ids.map(_.foreach({ mediumId =>
          assertFutureValue(existsFeedMedium(feedId, mediumId), true)
        }))

        await(feedsRepository.delete(feedId, sessionId))

        assertFutureValue(feedsDAO.own(feedId, sessionId), false)
        tags.map(_.foreach({ name =>
          assertFutureValue(existsFeedTag(feedId, name), false)
        }))

        ids.map(_.foreach({ mediumId =>
          assertFutureValue(existsFeedMedium(feedId, mediumId), false)
        }))

      }
    }

    scenario("should delete an user feeds") {
      forOne(userGen, userGen, followerFeedGen) { (s, a, f) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user = await(createUser(a.userName))
        val userId = user.id
        await(followsRepository.create(sessionId.userId, userId.sessionId))
        val feedId = await(feedsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        assert(existsUserFeeds(feedId, userId))
        await(feedsRepository.delete(feedId, sessionId))
        assert(!existsUserFeeds(feedId, userId))

      }
    }

    scenario("should return exception if feed not exist") {
      forOne(userGen) { (s) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(feedsRepository.delete(FeedId(0), sessionId))
        }.error == AuthorityNotFound)

      }

    }
  }


//  feature("find feeds an user posted") {
//
//    scenario("should return feed list user posted") {
//
//    }
//
//    scenario("should return exception if an user not exist") {
//
//    }
//  }
//
//  feature("find feeds session user posted") {
//    scenario("should return feed list session user posted") {
//
//    }
//  }
//
//  feature("find a feed") {
//    scenario("should return a feed") {
//
//    }
//    scenario("should return exception if a feed not exist") {}
//
//  }

}
