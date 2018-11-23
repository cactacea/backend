package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, MediumType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Messages

class MessagesDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("MediumsDAOSpec4")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val messageId = execute(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))

    val result = execute(db.run(quote(query[Messages].filter(_.groupId == lift(groupId)))))
    assert(result.size == 1)
    val message = result(0)
    assert(message.id == messageId)
    assert(message.message == Some("new message"))
    assert(message.accountCount == 1L)
    assert(message.contentWarning == false)
    assert(message.notified == false)

    val mediumId2 = execute(mediumsDAO.create("key1", "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg"), MediumType.image, 1, 4, 100L, sessionAccount.id.toSessionId))
    execute(messagesDAO.create(groupId, None, 1, Some(mediumId2), sessionAccount.id.toSessionId))
    val result2 = execute(messagesDAO.find(messageId))
    assert(result2.isDefined)

  }

  test("delete") {

    val sessionAccount = createAccount("MediumsDAOSpec5")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val messageId = execute(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))

    execute(messagesDAO.delete(groupId))
    val result = execute(db.run(quote(query[Messages].filter(_.groupId == lift(groupId)))))
    assert(result.size == 0)
    assert(result.head.id == messageId)
  }

  test("updateReadStatus") {

    val sessionAccount = createAccount("MediumsDAOSpec6")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val messageId = execute(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    // TODO : check
    execute(messagesDAO.updateReadAccountCount(List(messageId)))

    val result2 = execute(db.run(quote(query[Messages].filter(_.groupId == lift(groupId)))))
    assert(result2.size == 1)
    val message = result2(0)
    assert(message.readAccountCount == 1)

  }

  test("updateNotified") {

    val sessionAccount = createAccount("MediumsDAOSpec7")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val messageId = execute(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    // TODO : Check
    execute(messagesDAO.updateNotified(messageId))

    val result2 = execute(db.run(quote(query[Messages].filter(_.id == lift(messageId)))))
    assert(result2.size == 1)
    val message = result2(0)
    assert(message.notified == true)

  }

}
