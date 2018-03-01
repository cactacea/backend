package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{FeedPrivacyType, MediumType}
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.identifiers.FeedId

class DeliveryFeedsRepositorySpec extends RepositorySpec {

  val friendsRepository = injector.instance[FriendsRepository]
  val feedsRepository = injector.instance[FeedsRepository]
  val mediumRepository = injector.instance[MediumsRepository]
  val reportsRepository = injector.instance[ReportsRepository]
  val deliveryFeedsRepository = injector.instance[DeliveryFeedsRepository]
  val devicesRepository = injector.instance[DevicesRepository]

  test("create and findAll") {

    val session = signUp("session name", "session password", "udid").account
    val user1 = signUp("user 1 name", "session password", "udid 1").account
    val user2 = signUp("user 2 name", "session password", "udid 2").account
    val user3 = signUp("user 3 name", "session password", "udid 3").account
    val user4 = signUp("user 4 name", "session password", "udid 4").account

    Await.result(friendsRepository.create(session.id, user1.id.toSessionId))
    Await.result(friendsRepository.create(session.id, user2.id.toSessionId))
    Await.result(friendsRepository.create(session.id, user3.id.toSessionId))
    Await.result(friendsRepository.create(session.id, user4.id.toSessionId))

    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")

    Await.result(devicesRepository.update("udid 1", pushToken, user1.id.toSessionId))
    Await.result(devicesRepository.update("udid 2", pushToken, user2.id.toSessionId))
    Await.result(devicesRepository.update("udid 3", pushToken, user3.id.toSessionId))
    Await.result(devicesRepository.update("udid 4", pushToken, user4.id.toSessionId))
    // TODO : Check

    val tags = Some(List("tag1", "tag2", "tag3"))
    val (id, url) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums = Some(List(id))
    val feedId = Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, session.id.toSessionId))

    assert(Await.result(deliveryFeedsRepository.create(feedId)) == true)
    assert(Await.result(deliveryFeedsRepository.create(FeedId(0L))) == false)

    val result1 = Await.result(deliveryFeedsRepository.findAll(feedId))
    assert(result1.isDefined == true)
    assert(result1.get.head.tokens.size == 4)

    val result2 = Await.result(deliveryFeedsRepository.findAll(FeedId(0L)))
    assert(result2.isEmpty == true)

    val result3 = Await.result(deliveryFeedsRepository.updateNotified(feedId))
    assert(result3 == true)

    val result4 = Await.result(deliveryFeedsRepository.updateNotified(feedId, List(user1.id, user2.id, user3.id, user4.id)))
    assert(result4 == true)

  }


}
