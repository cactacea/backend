package io.github.cactacea.backend.core.domain.repositories


import java.util.Locale

import io.github.cactacea.backend.core.domain.enums.{TweetType, InformationType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{TweetId, MediumId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AuthorityNotFound, MediumNotFound}

class TweetsRepositorySpec extends RepositorySpec {

  feature("create") {
    scenario("should create a tweet") {
      forOne(userGen, everyoneTweetGen, medium5SeqOptGen) { (a, f, l) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val ids = l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val tweetId = await(tweetsRepository.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result1 = await(tweetsRepository.find(tweetId, sessionId))
        assert(result1.message == f.message)
        assert(result1.warning == f.contentWarning)
        assert(result1.mediums.map(_.id) == ids.getOrElse(Seq[MediumId]()))
        assert(result1.tags == tags)

        tags.map(_.foreach({ name =>
          assertFutureValue(existsTweetTag(tweetId, name), true)
        }))

        ids.map(_.foreach({ mediumId =>
          assertFutureValue(existsTweetMedium(tweetId, mediumId), true)
        }))

      }
    }

    scenario("should create an user tweet and a notification") {
      forOne(userGen, userGen, followerTweetGen) { (s, a, f) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user = await(createUser(a.userName))
        val userId = user.id
        await(followsRepository.create(sessionId.userId, userId.sessionId))
        val tweetId = await(tweetsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        val result1 = await(tweetsRepository.find(None, 0, 10, None, TweetType.received, userId.sessionId))
        assert(result1.size == 1)
        assert(result1.headOption.exists(_.id == tweetId))

        val result = await(notificationsRepository.find(None, 0, 10, Seq(Locale.getDefault()), userId.sessionId))
        assert(result.size == 1)
        assert(result.headOption.exists(_.contentId.exists(_ == tweetId.value)))
        assert(result.headOption.exists(_.informationType == InformationType.tweet))
      }
    }

    scenario("should return exception if medium not exist") {
      forOne(userGen, everyoneTweetGen) { (a, f) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(tweetsRepository.create(f.message, Option(Seq(MediumId(0L))), None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        }.error == MediumNotFound)

      }
    }
  }


  feature("update") {
    scenario("should update a tweet") {

      forOne(userGen, everyoneTweetGen, everyoneTweetGen, medium5SeqOptGen, medium5SeqOptGen) { (a, f, f2, l, l2) =>
        val sessionId = await(createUser(a.userName)).id.sessionId
        val ids = l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val tweetId = await(tweetsRepository.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))


        val ids2 = l2.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags2 = f2.tags.map(_.split(' ').toSeq)
        await(tweetsRepository.update(tweetId, f2.message, ids2, tags2, f2.privacyType, f2.contentWarning, f2.expiration, sessionId))

        // result
        val result1 = await(tweetsRepository.find(tweetId, sessionId))
        assert(result1.message == f2.message)
        assert(result1.warning == f2.contentWarning)
        assert(result1.mediums.map(_.id) == ids2.getOrElse(Seq[MediumId]()))
        assert(result1.tags == tags2)

      }
    }

    scenario("should return exception if medium not exist") {
      forOne(userGen, everyoneTweetGen, medium5SeqOptGen) { (a, f, l) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val ids = l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val tweetId = await(tweetsRepository.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(tweetsRepository.update(tweetId, f.message, Option(Seq(MediumId(0))), tags, f.privacyType, f.contentWarning, f.expiration, sessionId))
        }.error == MediumNotFound)

      }
    }

    scenario("should return exception if tweet not exist") {
      forOne(userGen, everyoneTweetGen, medium5SeqOptGen) { (a, f, l) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)

        // result
        assert(intercept[CactaceaException] {
          await(tweetsRepository.update(TweetId(0), f.message, None, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))
        }.error == AuthorityNotFound)

      }
    }
  }

  feature("delete") {
    scenario("should delete a tweet") {
      forOne(userGen, everyoneTweetGen) { (a, f) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val tweetId = await(tweetsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), true)
        await(tweetsRepository.delete(tweetId, sessionId))
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), false)

      }
    }

    scenario("should delete comments on a tweet") {
      forOne(userGen, everyoneTweetGen, commentGen) { (a, f, c) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val tweetId = await(tweetsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))

        // result
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), true)
        assertFutureValue(commentsDAO.own(commentId, sessionId), true)
        await(tweetsRepository.delete(tweetId, sessionId))
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), false)
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)

      }
    }

    scenario("should delete likes on a tweet") {
      forOne(userGen, userGen, everyoneTweetGen) { (s, a, f) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user = await(createUser(a.userName))
        val userId = user.id
        val tweetId = await(tweetsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(tweetLikesRepository.create(tweetId, userId.sessionId))

        // result
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), true)
        assertFutureValue(tweetLikesDAO.own(tweetId, userId.sessionId), true)
        await(tweetsRepository.delete(tweetId, sessionId))
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), false)
        assertFutureValue(tweetLikesDAO.own(tweetId, userId.sessionId), false)

      }
    }

    scenario("should delete reports on a tweet") {
      forOne(userGen, everyoneTweetGen, tweetReportGen) { (a, f, r) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val tweetId = await(tweetsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(tweetsRepository.report(tweetId, r.reportType, r.reportContent, sessionId))

        // result
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), true)
        assertFutureValue(existsTweetReport(tweetId, sessionId), true)
        await(tweetsRepository.delete(tweetId, sessionId))
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), false)
        assertFutureValue(existsTweetReport(tweetId, sessionId), false)

      }
    }


    scenario("should delete reports on comments on a tweet") {
      forOne(userGen, everyoneTweetGen, commentGen, commentReportGen) { (a, f, c, r) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val tweetId = await(tweetsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))
        await(commentsRepository.report(commentId, r.reportType, r.reportContent, sessionId))

        // result
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), true)
        assertFutureValue(commentsDAO.own(commentId, sessionId), true)
        assertFutureValue(existsCommentReport(commentId, sessionId), true)
        await(tweetsRepository.delete(tweetId, sessionId))
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), false)
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
        assertFutureValue(existsCommentReport(commentId, sessionId), false)

      }
    }

    scenario("should delete likes on comments on a tweet") {
      forOne(userGen, userGen, everyoneTweetGen, commentGen) { (s, a, f, c) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user = await(createUser(a.userName))
        val userId = user.id
        val tweetId = await(tweetsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))
        await(commentLikesRepository.create(commentId, userId.sessionId))

        // result
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), true)
        assertFutureValue(commentsDAO.own(commentId, sessionId), true)
        assertFutureValue(commentLikesDAO.own(commentId, userId.sessionId), true)
        await(tweetsRepository.delete(tweetId, sessionId))
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), false)
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
        assertFutureValue(commentLikesDAO.own(commentId, userId.sessionId), false)

      }
    }

    scenario("should delete tags and meidums on a tweet") {
      forOne(userGen, everyoneTweetGen, medium5SeqOptGen) { (a, f, l) =>

        // preparing
        val session = await(createUser(a.userName))
        val sessionId = session.id.sessionId
        val ids = l.map(_.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val tweetId = await(tweetsRepository.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        await(tweetsRepository.find(tweetId, sessionId))
        assertFutureValue(tweetsDAO.own(tweetId, sessionId), true)
        tags.map(_.foreach({ name =>
          assertFutureValue(existsTweetTag(tweetId, name), true)
        }))

        ids.map(_.foreach({ mediumId =>
          assertFutureValue(existsTweetMedium(tweetId, mediumId), true)
        }))

        await(tweetsRepository.delete(tweetId, sessionId))

        assertFutureValue(tweetsDAO.own(tweetId, sessionId), false)
        tags.map(_.foreach({ name =>
          assertFutureValue(existsTweetTag(tweetId, name), false)
        }))

        ids.map(_.foreach({ mediumId =>
          assertFutureValue(existsTweetMedium(tweetId, mediumId), false)
        }))

      }
    }

    scenario("should delete an user tweets") {
      forOne(userGen, userGen, followerTweetGen) { (s, a, f) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user = await(createUser(a.userName))
        val userId = user.id
        await(followsRepository.create(sessionId.userId, userId.sessionId))
        val tweetId = await(tweetsRepository.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        // result
        assert(existsUserTweets(tweetId, userId))
        await(tweetsRepository.delete(tweetId, sessionId))
        assert(!existsUserTweets(tweetId, userId))

      }
    }

    scenario("should return exception if tweet not exist") {
      forOne(userGen) { (s) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(tweetsRepository.delete(TweetId(0), sessionId))
        }.error == AuthorityNotFound)

      }

    }
  }


//  feature("find tweets an user posted") {
//
//    scenario("should return tweet list user posted") {
//
//    }
//
//    scenario("should return exception if an user not exist") {
//
//    }
//  }
//
//  feature("find tweets session user posted") {
//    scenario("should return tweet list session user posted") {
//
//    }
//  }
//
//  feature("find a tweet") {
//    scenario("should return a tweet") {
//
//    }
//    scenario("should return exception if a tweet not exist") {}
//
//  }

}
