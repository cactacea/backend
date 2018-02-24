package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.helpers.SessionRepositoryTest

class DeliveryMessagesRepositorySpec extends SessionRepositoryTest {

  var groupsRepository = injector.instance[GroupsRepository]
  var groupAccountsRepository = injector.instance[GroupAccountsRepository]
  var messagesRepository = injector.instance[MessagesRepository]
  val deliveryMessagesRepository = injector.instance[DeliveryMessagesRepository]
  var devicesRepository = injector.instance[DevicesRepository]

  test("create and findAll") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user.id.toSessionId))
    val messageId = Await.result(messagesRepository.create(groupId, Some("test"), None, user.id.toSessionId))

    assert(Await.result(deliveryMessagesRepository.create(messageId)) == true)

    val result = Await.result(deliveryMessagesRepository.findAll(messageId))
    assert(result.size == 1)

  }

  test("update messages notified") {

  }

  test("update account messages notified") {

  }

}
