package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, MediumType, ReportType}
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, MediumId}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, FeedNotFound, MediumNotFound}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class FeedsRepositorySpec extends RepositorySpec {

  val feedsRepository = injector.instance[FeedsRepository]
  val mediumRepository = injector.instance[MediumsRepository]
  var reportsRepository = injector.instance[ReportsRepository]

  test("create") {

    val session = signUp("FeedsRepositorySpec1", "session password", "udid")

    val tags = Some(List("tag1", "tag2", "tag3"))
    val key = "key"
    val uri = "http://cactacea.io/test.jpeg"
    val (id, url) = Await.result(mediumRepository.create(key, uri, Some(uri), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums = Some(List(id))
    val feedId = Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feed = Await.result(feedsRepository.find(feedId, session.id.toSessionId))
    assert(feed.message == "feed message")
    assert(feed.tags == tags)
    assert(feed.mediums.map(_.map(_.id)) == Some(List(id)))
    assert(feed.mediums.get.head.id == id)
    assert(feed.mediums.get.head.uri == uri)
    assert(feed.mediums.get.head.width == 120)
    assert(feed.mediums.get.head.height == 120)
    assert(feed.mediums.get.head.size == 58L)


  }

  test("create with no exist medium") {

    val session = signUp("FeedsRepositorySpec2", "session password", "udid")

    val tags = Some(List("tag1", "tag2", "tag3"))
    val mediums = Some(List(MediumId(0L)))

    assert(intercept[CactaceaException] {
      Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    }.error == MediumNotFound)

  }

  test("update") {

    val session = signUp("FeedsRepositorySpec3", "session password", "udid")

    val tags = Some(List("tag1", "tag2", "tag3"))
    val (id1, _) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums = Some(List(id1))
    val feedId = Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))

    val tags2 = Some(List("tag4", "tag5", "tag6"))
    val (id2, _) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums2 = Some(List(id2))
    Await.result(feedsRepository.update(feedId, "feed message 2", mediums2, tags2, FeedPrivacyType.followers, true, None, session.id.toSessionId))
    // TODO : Check

  }

  test("update no exist feed") {

    val session = signUp("FeedsRepositorySpec4", "session password", "udid")

    val tags = Some(List("tag1", "tag2", "tag3"))
    val (id, _) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums = Some(List(id))
    Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(feedsRepository.update(FeedId(0L), "feed message", mediums, tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("update with no exist medium") {

    val session = signUp("FeedsRepositorySpec5", "session password", "udid")

    val tags = Some(List("tag1", "tag2", "tag3"))
    val (id, _) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums = Some(List(id))
    val feedId = Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(feedsRepository.update(feedId, "feed message", Some(List(MediumId(0L))), tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    }.error == MediumNotFound)

  }

  test("delete") {

    val session = signUp("FeedsRepositorySpec6", "session password", "udid")
    val user = signUp("FeedsRepositorySpec6-2", "user password", "user udid")

    val tags = Some(List("tag1", "tag2", "tag3"))
    val (id, _) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums = Some(List(id))
    val feedId = Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    Await.result(reportsRepository.createFeedReport(feedId, ReportType.inappropriate, user.id.toSessionId))
    Await.result(feedsRepository.delete(feedId, session.id.toSessionId))
    // TODO : Check

  }

  test("delete no exist feed") {

    val session = signUp("FeedsRepositorySpec7", "session password", "udid")

    assert(intercept[CactaceaException] {
      Await.result(feedsRepository.delete(FeedId(0L), session.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("find feeds by a user") {

    val session = signUp("FeedsRepositorySpec8", "session password", "udid")

    val tags = Some(List("tag1", "tag2", "tag3"))
    val (id, _) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums = Some(List(id))
    val feedId = Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val result = Await.result(feedsRepository.find(feedId, session.id.toSessionId))
    assert(result.id == feedId)

  }

  test("findAll a account's feeds") {

    val session = signUp("FeedsRepositorySpec9", "session password", "udid")
    val user = signUp("FeedsRepositorySpec9-2", "user password", "udid")
    val feedId1 = Await.result(feedsRepository.create("feed message 1", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId2 = Await.result(feedsRepository.create("feed message 2", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId3 = Await.result(feedsRepository.create("feed message 3", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId4 = Await.result(feedsRepository.create("feed message 4", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId5 = Await.result(feedsRepository.create("feed message 5", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))

    val result1 = Await.result(feedsRepository.findAll(session.id, None, None, Some(3), user.id.toSessionId))
    assert(result1.size == 3)
    val feed1 = result1(0)
    val feed2 = result1(1)
    val feed3 = result1(2)
    assert(feed1.id == feedId5)
    assert(feed2.id == feedId4)
    assert(feed3.id == feedId3)

    val result2 = Await.result(feedsRepository.findAll(session.id, Some(feed3.next), None, Some(3), user.id.toSessionId))
    assert(result2.size == 2)
    val feed4 = result2(0)
    val feed5 = result2(1)
    assert(feed4.id == feedId2)
    assert(feed5.id == feedId1)

  }

  test("findAll no exist account's feeds") {

    val session = signUp("FeedsRepositorySpec10", "session password", "udid")

    assert(intercept[CactaceaException] {
      Await.result(feedsRepository.findAll(AccountId(0L), None, None, Some(3), session.id.toSessionId))
    }.error == AccountNotFound)

  }


  test("find session's feeds") {

    val session = signUp("FeedsRepositorySpec11", "session password", "udid")
    val feedId1 = Await.result(feedsRepository.create("feed message 1", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId2 = Await.result(feedsRepository.create("feed message 2", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId3 = Await.result(feedsRepository.create("feed message 3", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId4 = Await.result(feedsRepository.create("feed message 4", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId5 = Await.result(feedsRepository.create("feed message 5", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))

    val result1 = Await.result(feedsRepository.findAll(None, None, Some(3), session.id.toSessionId))
    assert(result1.size == 3)
    val feed1 = result1(0)
    val feed2 = result1(1)
    val feed3 = result1(2)
    assert(feed1.id == feedId5)
    assert(feed2.id == feedId4)
    assert(feed3.id == feedId3)

    val result2 = Await.result(feedsRepository.findAll(Some(feed3.next), None, Some(3), session.id.toSessionId))
    assert(result2.size == 2)
    val feed4 = result2(0)
    val feed5 = result2(1)
    assert(feed4.id == feedId2)
    assert(feed5.id == feedId1)

  }

  test("find a feed") {

    val session = signUp("FeedsRepositorySpec12", "session password", "udid")
    val feedId = Await.result(feedsRepository.create("feed message 1", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val result = Await.result(feedsRepository.find(feedId, session.id.toSessionId))
    assert(result.id == feedId)

  }

  test("find no exist feed") {

    val session = signUp("FeedsRepositorySpec13", "session password", "udid")
    assert(intercept[CactaceaException] {
      Await.result(feedsRepository.find(FeedId(0L), session.id.toSessionId))
    }.error == FeedNotFound)

  }

}
