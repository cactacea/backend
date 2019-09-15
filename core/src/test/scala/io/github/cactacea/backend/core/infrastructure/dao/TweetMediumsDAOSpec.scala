package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.TweetMediums

class TweetMediumsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create tweet mediums") {
      forAll(userGen, tweetGen, medium5SeqOptGen) { (a, f, l) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tweetId = await(tweetsDAO.create(f.message, ids, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(tweetMediumsDAO.create(tweetId, ids))
        val result = await(db.run(quote(query[TweetMediums].filter(_.tweetId == lift(tweetId)).map(_.mediumId))))
        assert(ids.exists(_ == result) || (ids.isEmpty && result.size == 0))
      }
    }
  }

  feature("delete") {
    scenario("should delete tweet mediums") {
      forAll(userGen, tweetGen, medium5SeqOptGen) { (a, f, l) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tweetId = await(tweetsDAO.create(f.message, ids, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(tweetMediumsDAO.create(tweetId, ids))
        await(tweetMediumsDAO.delete(tweetId))
        val result = await(db.run(quote(query[TweetMediums].filter(_.tweetId == lift(tweetId)).map(_.mediumId))))
        assert(result.size == 0)
      }
    }
  }

}

