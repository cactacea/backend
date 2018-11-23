package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, FeedAlreadyLiked, FeedNotLiked, FeedNotFound}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class FeedLikesRepositorySpec extends RepositorySpec {

  val feedsRepository = injector.instance[FeedsRepository]
  val feedLikesRepository = injector.instance[FeedLikesRepository]

  test("create a feed like") {

    val session = signUp("FeedLikesRepositorySpec1", "session password", "udid")
    val user = signUp("FeedLikesRepositorySpec2", "user password", "user udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId, user.id.toSessionId))
    // TODO : Check

  }

  test("create a feed like on no exist feed") {

    val session = signUp("FeedLikesRepositorySpec3", "session password", "udid")
    assert(intercept[CactaceaException] {
      execute(feedLikesRepository.create(FeedId(0L), session.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("create duplication feed likes") {

    val session = signUp("FeedLikesRepositorySpec4", "session password", "udid")
    val user = signUp("FeedLikesRepositorySpec5", "user password", "user udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId, user.id.toSessionId))
    assert(intercept[CactaceaException] {
      execute(feedLikesRepository.create(feedId, user.id.toSessionId))
    }.error == FeedAlreadyLiked)

  }

  test("delete a feed like") {

    val session = signUp("FeedLikesRepositorySpec6", "session password", "udid")
    val user = signUp("FeedLikesRepositorySpec7", "user password", "user udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId, user.id.toSessionId))
    execute(feedLikesRepository.delete(feedId, user.id.toSessionId))
    // TODO : Check


  }

  test("delete a feed like on no exist feed") {

    val session = signUp("FeedLikesRepositorySpec8", "session password", "udid")
    assert(intercept[CactaceaException] {
      execute(feedLikesRepository.delete(FeedId(0L), session.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("delete duplication feed likes") {

    val session = signUp("FeedLikesRepositorySpec9", "session password", "udid")
    val user = signUp("FeedLikesRepositorySpec10", "user password", "user udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    assert(intercept[CactaceaException] {
      execute(feedLikesRepository.delete(feedId, user.id.toSessionId))
    }.error == FeedNotLiked)

  }

  test("find feed likes by a user") {

    val session = signUp("FeedLikesRepositorySpec11", "session password", "udid")
    val user1 = signUp("FeedLikesRepositorySpec11-2", "user password", "user udid")
    val feedId1 = execute(feedsRepository.create("feed message 1", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId2 = execute(feedsRepository.create("feed message 2", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId3 = execute(feedsRepository.create("feed message 3", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId4 = execute(feedsRepository.create("feed message 4", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId5 = execute(feedsRepository.create("feed message 5", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId1, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId2, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId3, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId4, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId5, user1.id.toSessionId))
    val feeds1 = execute(feedLikesRepository.findAll(None, None, Some(3), user1.id.toSessionId))
    assert(feeds1.size == 3)
    val feed3 = feeds1(2)
    val feeds2 = execute(feedLikesRepository.findAll(Some(feed3.next), None, Some(3), user1.id.toSessionId))
    assert(feeds2.size == 2)

  }

  test("find feed likes by session") {

    val session = signUp("FeedLikesRepositorySpec12", "session password", "udid")
    val user1 = signUp("FeedLikesRepositorySpec13", "user password", "user udid")
    val feedId1 = execute(feedsRepository.create("feed message 1", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    val feedId2 = execute(feedsRepository.create("feed message 2", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    val feedId3 = execute(feedsRepository.create("feed message 3", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    val feedId4 = execute(feedsRepository.create("feed message 4", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    val feedId5 = execute(feedsRepository.create("feed message 5", None, None, FeedPrivacyType.everyone, false, None, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId1, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId2, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId3, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId4, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId5, session.id.toSessionId))
    val feeds1 = execute(feedLikesRepository.findAll(None, None, Some(3), session.id.toSessionId))
    assert(feeds1.size == 3)
    val feed3 = feeds1(2)
    val feeds2 = execute(feedLikesRepository.findAll(Some(feed3.next), None, Some(3), session.id.toSessionId))
    assert(feeds2.size == 2)

  }

  test("find feed likes by a account") {

    val session = signUp("FeedLikesRepositorySpec14", "session password", "udid")
    val user1 = signUp("FeedLikesRepositorySpec15", "user password", "user udid")
    val feedId1 = execute(feedsRepository.create("feed message 1", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId2 = execute(feedsRepository.create("feed message 2", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId3 = execute(feedsRepository.create("feed message 3", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId4 = execute(feedsRepository.create("feed message 4", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feedId5 = execute(feedsRepository.create("feed message 5", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId1, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId2, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId3, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId4, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId5, user1.id.toSessionId))
    val feeds1 = execute(feedLikesRepository.findAll(user1.id, None, None, Some(3), session.id.toSessionId))
    assert(feeds1.size == 3)
    assert(feeds1(0).id == feedId5)
    assert(feeds1(1).id == feedId4)
    assert(feeds1(2).id == feedId3)

    val feed3 = feeds1(2)
    val feeds2 = execute(feedLikesRepository.findAll(user1.id, Some(feed3.next), None, Some(3), session.id.toSessionId))
    assert(feeds2.size == 2)
    assert(feeds2(0).id == feedId2)
    assert(feeds2(1).id == feedId1)

    assert(intercept[CactaceaException] {
      execute(feedLikesRepository.findAll(AccountId(0L), None, None, Some(3), session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("find accounts") {

    val session = signUp("FeedLikesRepositorySpec16", "session password", "udid")
    val user1 = signUp("FeedLikesRepositorySpec17", "user password 1", "user udid 1")
    val user2 = signUp("FeedLikesRepositorySpec18", "user password 2", "user udid 2")
    val user3 = signUp("FeedLikesRepositorySpec19", "user password 3", "user udid 3")
    val user4 = signUp("FeedLikesRepositorySpec20", "user password 4", "user udid 4")
    val user5 = signUp("FeedLikesRepositorySpec21", "user password 5", "user udid 5")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    execute(feedLikesRepository.create(feedId, user1.id.toSessionId))
    execute(feedLikesRepository.create(feedId, user2.id.toSessionId))
    execute(feedLikesRepository.create(feedId, user3.id.toSessionId))
    execute(feedLikesRepository.create(feedId, user4.id.toSessionId))
    execute(feedLikesRepository.create(feedId, user5.id.toSessionId))
    val accounts1 = execute(feedLikesRepository.findAccounts(feedId, None, None, Some(3), session.id.toSessionId))
    assert(accounts1.size == 3)
    val account3 = accounts1(2)
    val accounts2 = execute(feedLikesRepository.findAccounts(feedId, Some(account3.next), None, Some(3), session.id.toSessionId))
    assert(accounts2.size == 2)

  }

  test("find no exist feed") {

    val session = signUp("FeedLikesRepositorySpec22", "session password", "udid")
    assert(intercept[CactaceaException] {
      execute(feedLikesRepository.findAccounts(FeedId(0L), None, None, Some(3), session.id.toSessionId))
    }.error == FeedNotFound)

  }

}

