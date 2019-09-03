package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.FeedMediums

class FeedMediumsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create feed mediums") {
      forAll(userGen, feedGen, medium5ListOptGen) { (a, f, l) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val feedId = await(feedsDAO.create(f.message, ids, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(feedMediumsDAO.create(feedId, ids))
        val result = await(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId)).map(_.mediumId))))
        assert(ids.exists(_ == result) || (ids.isEmpty && result.size == 0))
      }
    }
  }

  feature("delete") {
    scenario("should delete feed mediums") {
      forAll(userGen, feedGen, medium5ListOptGen) { (a, f, l) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val feedId = await(feedsDAO.create(f.message, ids, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(feedMediumsDAO.create(feedId, ids))
        await(feedMediumsDAO.delete(feedId))
        val result = await(db.run(quote(query[FeedMediums].filter(_.feedId == lift(feedId)).map(_.mediumId))))
        assert(result.size == 0)
      }
    }
  }

}

