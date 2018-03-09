package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId}
import io.github.cactacea.core.util.responses.CactaceaError.{AccountNotFound, FeedAlreadyLiked, FeedNotLiked, FeedNotFound}
import io.github.cactacea.core.util.exceptions.CactaceaException

class FeedLikesRepositorySpec extends RepositorySpec {

  var feedsRepository = injector.instance[FeedsRepository]
  var feedLikesRepository = injector.instance[FeedLikesRepository]

  test("create a feed like") {

    val session = signUp("session name", "session password", "udid").account
    val user = signUp("user name", "user password", "user udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val result = Await.result(feedLikesRepository.create(feedId, user.id.toSessionId))
    // TODO : Check

  }

  test("create a feed like on no exist feed") {

    val session = signUp("session name", "session password", "udid").account
    assert(intercept[CactaceaException] {
      Await.result(feedLikesRepository.create(FeedId(0L), session.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("create duplication feed likes") {

    val session = signUp("session name", "session password", "udid").account
    val user = signUp("user name", "user password", "user udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId, user.id.toSessionId))
    assert(intercept[CactaceaException] {
      Await.result(feedLikesRepository.create(feedId, user.id.toSessionId))
    }.error == FeedAlreadyLiked)

  }

  test("delete a feed like") {

    val session = signUp("session name", "session password", "udid").account
    val user = signUp("user name", "user password", "user udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId, user.id.toSessionId))
    val result = Await.result(feedLikesRepository.delete(feedId, user.id.toSessionId))
    // TODO : Check


  }

  test("delete a feed like on no exist feed") {

    val session = signUp("session name", "session password", "udid").account
    assert(intercept[CactaceaException] {
      Await.result(feedLikesRepository.delete(FeedId(0L), session.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("delete duplication feed likes") {

    val session = signUp("session name", "session password", "udid").account
    val user = signUp("user name", "user password", "user udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    assert(intercept[CactaceaException] {
      Await.result(feedLikesRepository.delete(feedId, user.id.toSessionId))
    }.error == FeedNotLiked)

  }

  test("find feed likes by a user") {

    val session = signUp("session name", "session password", "udid").account
    val user1 = signUp("user name 1", "user password", "user udid").account
    val feedId1 = Await.result(feedsRepository.create("feed message 1", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId2 = Await.result(feedsRepository.create("feed message 2", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId3 = Await.result(feedsRepository.create("feed message 3", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId4 = Await.result(feedsRepository.create("feed message 4", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId5 = Await.result(feedsRepository.create("feed message 5", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId1, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId2, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId3, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId4, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId5, user1.id.toSessionId))
    val feeds1 = Await.result(feedLikesRepository.findAll(None, None, Some(3), user1.id.toSessionId))
    assert(feeds1.size == 3)
    val feed3 = feeds1(2)
    val feeds2 = Await.result(feedLikesRepository.findAll(Some(feed3.postedAt), None, Some(3), user1.id.toSessionId))
    assert(feeds2.size == 2)

  }

  test("find feed likes by session") {

    val session = signUp("session name", "session password", "udid").account
    val user1 = signUp("user name 1", "user password", "user udid").account
    val feedId1 = Await.result(feedsRepository.create("feed message 1", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    val feedId2 = Await.result(feedsRepository.create("feed message 2", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    val feedId3 = Await.result(feedsRepository.create("feed message 3", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    val feedId4 = Await.result(feedsRepository.create("feed message 4", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    val feedId5 = Await.result(feedsRepository.create("feed message 5", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId1, session.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId2, session.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId3, session.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId4, session.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId5, session.id.toSessionId))
    val feeds1 = Await.result(feedLikesRepository.findAll(None, None, Some(3), session.id.toSessionId))
    assert(feeds1.size == 3)
    val feed3 = feeds1(2)
    val feeds2 = Await.result(feedLikesRepository.findAll(Some(feed3.postedAt), None, Some(3), session.id.toSessionId))
    assert(feeds2.size == 2)

  }

  test("find feed likes by a account") {

    val session = signUp("session name", "session password", "udid").account
    val user1 = signUp("user name 1", "user password", "user udid").account
    val feedId1 = Await.result(feedsRepository.create("feed message 1", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId2 = Await.result(feedsRepository.create("feed message 2", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId3 = Await.result(feedsRepository.create("feed message 3", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId4 = Await.result(feedsRepository.create("feed message 4", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId5 = Await.result(feedsRepository.create("feed message 5", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId1, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId2, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId3, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId4, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId5, user1.id.toSessionId))
    val feeds1 = Await.result(feedLikesRepository.findAll(user1.id, None, None, Some(3), session.id.toSessionId))
    assert(feeds1.size == 3)
    assert(feeds1(0).id == feedId5)
    assert(feeds1(1).id == feedId4)
    assert(feeds1(2).id == feedId3)

    val feed3 = feeds1(2)
    val feeds2 = Await.result(feedLikesRepository.findAll(user1.id, Some(feed3.postedAt), None, Some(3), session.id.toSessionId))
    assert(feeds2.size == 2)
    assert(feeds2(0).id == feedId2)
    assert(feeds2(1).id == feedId1)

    assert(intercept[CactaceaException] {
      Await.result(feedLikesRepository.findAll(AccountId(0L), None, None, Some(3), session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("find accounts") {

    val session = signUp("session name", "session password", "udid").account
    val user1 = signUp("user name 1", "user password 1", "user udid 1").account
    val user2 = signUp("user name 2", "user password 2", "user udid 2").account
    val user3 = signUp("user name 3", "user password 3", "user udid 3").account
    val user4 = signUp("user name 4", "user password 4", "user udid 4").account
    val user5 = signUp("user name 5", "user password 5", "user udid 5").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId, user1.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId, user2.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId, user3.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId, user4.id.toSessionId))
    Await.result(feedLikesRepository.create(feedId, user5.id.toSessionId))
    val accounts1 = Await.result(feedLikesRepository.findAccounts(feedId, None, None, Some(3), session.id.toSessionId))
    assert(accounts1.size == 3)
    val account3 = accounts1(2)
    val accounts2 = Await.result(feedLikesRepository.findAccounts(feedId, Some(account3.next), None, Some(3), session.id.toSessionId))
    assert(accounts2.size == 2)

  }

  test("find no exist feed") {

    val session = signUp("session name", "session password", "udid").account
    assert(intercept[CactaceaException] {
      Await.result(feedLikesRepository.findAccounts(FeedId(0L), None, None, Some(3), session.id.toSessionId))
    }.error == FeedNotFound)

  }

}

