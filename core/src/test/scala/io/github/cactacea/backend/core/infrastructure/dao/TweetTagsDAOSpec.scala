package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.TweetTags

class TweetTagsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create tweet tabs") {
      forAll(userGen, tweetGen) { (a, f) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val tags = f.tags.map(_.split(' ').toSeq)
        val tweetId = await(tweetsDAO.create(f.message, None, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(tweetTagsDAO.create(tweetId, tags))
        val result = await(db.run(quote(query[TweetTags].filter(_.tweetId == lift(tweetId)).map(_.name))))
        assert(tags.exists(_ == result) || (tags.isEmpty && result.size == 0))
      }
    }
  }

  feature("delete") {
    scenario("should delete tweet tags") {
      forAll(userGen, tweetGen) { (a, f) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val tags = f.tags.map(_.split(' ').toSeq)
        val tweetId = await(tweetsDAO.create(f.message, None, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(tweetTagsDAO.create(tweetId, tags))
        await(tweetTagsDAO.delete(tweetId))

        val result = await(db.run(quote(query[TweetTags].filter(_.tweetId == lift(tweetId)))))
        assert(result.size == 0)

      }
    }
  }



}

