package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.FeedTags

class FeedTagsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create feed tabs") {
      forAll(userGen, feedGen) { (a, f) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val tags = f.tags.map(_.split(' ').toList)
        val feedId = await(feedsDAO.create(f.message, None, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(feedTagsDAO.create(feedId, tags))
        val result = await(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId)).map(_.name))))
        assert(tags.exists(_ == result) || (tags.isEmpty && result.size == 0))
      }
    }
  }

  feature("delete") {
    scenario("should delete feed tags") {
      forAll(userGen, feedGen) { (a, f) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val tags = f.tags.map(_.split(' ').toList)
        val feedId = await(feedsDAO.create(f.message, None, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(feedTagsDAO.create(feedId, tags))
        await(feedTagsDAO.delete(feedId))

        val result = await(db.run(quote(query[FeedTags].filter(_.feedId == lift(feedId)))))
        assert(result.size == 0)

      }
    }
  }



}

