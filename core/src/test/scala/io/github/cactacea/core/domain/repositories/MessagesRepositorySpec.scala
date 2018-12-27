package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, MediumType}
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao.MessagesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, MediumId}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotJoined, GroupNotFound, MediumNotFound}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class MessagesRepositorySpec extends RepositorySpec {

  val groupsRepository = injector.instance[GroupsRepository]
  val groupAccountsRepository = injector.instance[GroupAccountsRepository]
  val messagesRepository = injector.instance[MessagesRepository]
  val messagesDAO = injector.instance[MessagesDAO]
  val mediumRepository = injector.instance[MediumsRepository]

  test("create and delete") {

    val sessionUser = signUp("MessagesRepositorySpec1", "session user password", "session user udid")
    val user = signUp("MessagesRepositorySpec2", "user password", "user udid")

    val groupId = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(groupAccountsRepository.create(groupId, user.id.toSessionId))

    execute(messagesRepository.createText(groupId, "test", user.id.toSessionId))

    execute(messagesRepository.delete(groupId, user.id.toSessionId))
    // TODO : Check

    val id = execute(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, user.id.toSessionId))
    execute(messagesRepository.createMedium(groupId, id, user.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(messagesRepository.createMedium(groupId, MediumId(0L), sessionUser.id.toSessionId))
    }.error == MediumNotFound)

    assert(intercept[CactaceaException] {
      execute(messagesRepository.createText(GroupId(0L), "test", user.id.toSessionId))
    }.error == AccountNotJoined)

  }

  test("find all and update read status") {

    val sessionUser = signUp("MessagesRepositorySpec3", "session user password", "session user udid")
    val user = signUp("MessagesRepositorySpec4", "user password", "user udid")

    val groupId = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(groupAccountsRepository.create(groupId, user.id.toSessionId))
    execute(messagesRepository.createText(groupId, "test", user.id.toSessionId))
//    execute(deliveryMessagesRepository.create(messageId))

    val id = execute(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, user.id.toSessionId))
    execute(messagesRepository.createMedium(groupId, id, user.id.toSessionId))
//    execute(deliveryMessagesRepository.create(messageId2))

    val messages = execute(messagesRepository.find(groupId, None, 0, 29, false, user.id.toSessionId))
    assert(messages.size == 2)

    execute(messagesRepository.updateReadStatus(messages, user.id.toSessionId))
    // TODO : Check

    assert(intercept[CactaceaException] {
      execute(messagesRepository.find(GroupId(0L), None, 0, 20, false, user.id.toSessionId))
    }.error == GroupNotFound)

    val messages3 = execute(messagesRepository.find(groupId, None, 0, 20, false, user.id.toSessionId))
    assert(messages3.filter(_.unread == false).size == 2)

    execute(messagesRepository.updateReadStatus(messages3, user.id.toSessionId))
    // TODO : Check

    execute(messagesRepository.updateReadStatus(List[Message](), user.id.toSessionId))
    // TODO : Check

  }

}
