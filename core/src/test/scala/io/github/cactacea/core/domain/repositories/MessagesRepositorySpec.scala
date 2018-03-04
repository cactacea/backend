package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, MediumType}
import io.github.cactacea.core.domain.models.Message
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.dao.MessagesDAO
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, MediumId}
import io.github.cactacea.core.util.responses.CactaceaError.{GroupNotFound, MediumNotFound}
import io.github.cactacea.core.util.exceptions.CactaceaException

class MessagesRepositorySpec extends RepositorySpec {

  var groupsRepository = injector.instance[GroupsRepository]
  var groupAccountsRepository = injector.instance[GroupAccountsRepository]
  var messagesRepository = injector.instance[MessagesRepository]
  var messagesDAO = injector.instance[MessagesDAO]
  val mediumRepository = injector.instance[MediumsRepository]

  test("create and delete") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account

    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user.id.toSessionId))

    val messageId = Await.result(messagesRepository.create(groupId, Some("test"), None, user.id.toSessionId))

    Await.result(messagesRepository.delete(groupId, user.id.toSessionId))
    // TODO : Check

    val (id, _) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, user.id.toSessionId))
    Await.result(messagesRepository.create(groupId, None, Some(id), user.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(messagesRepository.create(groupId, None, Some(MediumId(0L)), sessionUser.id.toSessionId))
    }.error == MediumNotFound)

    assert(intercept[CactaceaException] {
      Await.result(messagesRepository.create(GroupId(0L), Some("test"), None, user.id.toSessionId))
    }.error == GroupNotFound)

  }

  test("findAll and update read status") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account

    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user.id.toSessionId))
    val messageId = Await.result(messagesRepository.create(groupId, Some("test"), None, user.id.toSessionId))
//    Await.result(deliveryMessagesRepository.create(messageId))

    val (id, _) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, user.id.toSessionId))
    val messageId2 = Await.result(messagesRepository.create(groupId, None, Some(id), user.id.toSessionId))
//    Await.result(deliveryMessagesRepository.create(messageId2))

    val messages = Await.result(messagesRepository.findAll(groupId, None, None, None, false, user.id.toSessionId))
    assert(messages.size == 2)

    Await.result(messagesRepository.updateReadStatus(messages, user.id.toSessionId))
    // TODO : Check

    assert(intercept[CactaceaException] {
      Await.result(messagesRepository.findAll(GroupId(0L), None, None, None, false, user.id.toSessionId))
    }.error == GroupNotFound)

    val messages3 = Await.result(messagesRepository.findAll(groupId, None, None, None, false, user.id.toSessionId))
    assert(messages3.filter(_.unread == false).size == 2)

    Await.result(messagesRepository.updateReadStatus(messages3, user.id.toSessionId))
    // TODO : Check

    Await.result(messagesRepository.updateReadStatus(List[Message](), user.id.toSessionId))
    // TODO : Check

  }

}
