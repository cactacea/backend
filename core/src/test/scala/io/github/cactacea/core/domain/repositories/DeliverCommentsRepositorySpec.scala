package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.infrastructure.dao.CommentsDAO
import io.github.cactacea.core.infrastructure.identifiers.CommentId
import io.github.cactacea.core.specs.RepositorySpec

class DeliverCommentsRepositorySpec extends RepositorySpec {

  val commentsRepository = injector.instance[CommentsRepository]
  val commentsDAO = injector.instance[CommentsDAO]
  val feedsRepository = injector.instance[FeedsRepository]
  val deliveryCommentsRepository = injector.instance[DeliveryCommentsRepository]
  val devicesRepository = injector.instance[DevicesRepository]

  test("findAll") {

    val user = signUp("user name", "user password", "udid").account
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")
    val session = signUp("session name", "session password", udid).account

    Await.result(devicesRepository.update(udid, pushToken, session.id.toSessionId))

    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", user.id.toSessionId))

    val result = Await.result(deliveryCommentsRepository.findAll(commentId))
    assert(result.get.size == 1)

    assert(Await.result(deliveryCommentsRepository.findAll(CommentId(0L))).isEmpty == true)

  }

  test("update") {

    val user = signUp("user name", "user password", "udid").account
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")
    val session = signUp("session name", "session password", udid).account

    Await.result(devicesRepository.update(udid, pushToken, session.id.toSessionId))

    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", user.id.toSessionId))

    val result = Await.result(deliveryCommentsRepository.updateNotified(commentId))
    assert(result == true)

  }


}
