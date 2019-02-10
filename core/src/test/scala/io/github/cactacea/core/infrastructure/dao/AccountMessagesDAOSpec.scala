package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.AccountMessages

class AccountMessagesDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec1")
    val account1 = createAccount("AccountMessagesDAOSpec2")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone,
      GroupAuthorityType.member, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))
    val messageId = execute(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, sessionAccount.id.toSessionId))

  }

  test("delete") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec3")
    val account1 = createAccount("AccountMessagesDAOSpec4")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone,
      GroupAuthorityType.member, sessionAccount.id.toSessionId))
    val messageId1 = execute(messagesDAO.create(groupId, Some("new message1"), None, sessionAccount.id.toSessionId))
    val messageId2 = execute(messagesDAO.create(groupId, Some("new message2"), None, sessionAccount.id.toSessionId))
    val messageId3 = execute(messagesDAO.create(groupId, Some("new message3"), None, sessionAccount.id.toSessionId))
    val messageId4 = execute(messagesDAO.create(groupId, Some("new message4"), None, sessionAccount.id.toSessionId))
    val messageId5 = execute(messagesDAO.create(groupId, Some("new message5"), None, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))
    execute(accountMessagesDAO.create(groupId, messageId1, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId2, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId3, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId4, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId5, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.delete(sessionAccount.id, groupId))

    val result2 = execute(db.run(quote(query[AccountMessages].filter(_.groupId == lift(groupId)).filter(_.accountId == lift(sessionAccount.id)).size)))
    assert(result2 == 0)

  }


  test("find older") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec5")
    val account1 = createAccount("AccountMessagesDAOSpec6")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    execute(messagesDAO.create(groupId, Some("new message1"), None, sessionAccount.id.toSessionId))
    val messageId2 = execute(messagesDAO.create(groupId, Some("new message2"), None, sessionAccount.id.toSessionId))
    val messageId3 = execute(messagesDAO.create(groupId, Some("new message3"), None, sessionAccount.id.toSessionId))
    val messageId4 = execute(messagesDAO.create(groupId, Some("new message4"), None, sessionAccount.id.toSessionId))
    val messageId5 = execute(messagesDAO.create(groupId, Some("new message5"), None, sessionAccount.id.toSessionId))
    val messageId6 = execute(messagesDAO.create(groupId, Some("new message6"), None, sessionAccount.id.toSessionId))
    val messageId7 = execute(messagesDAO.create(groupId, Some("new message7"), None, sessionAccount.id.toSessionId))
    val messageId8 = execute(messagesDAO.create(groupId, Some("new message8"), None, sessionAccount.id.toSessionId))
    val messageId9 = execute(messagesDAO.create(groupId, Some("new message9"), None, sessionAccount.id.toSessionId))

    execute(accountMessagesDAO.create(groupId, messageId2, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId3, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId4, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId5, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId6, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId7, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId8, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId9, sessionAccount.id.toSessionId))

    val result1 = execute(accountMessagesDAO.find(groupId, None, 0, 3, false, sessionAccount.id.toSessionId))
    assert(result1.size == 3)
    assert(result1(0).id == messageId9)
    assert(result1(1).id == messageId8)
    assert(result1(2).id == messageId7)

    val result2 = execute(accountMessagesDAO.find(groupId, result1(2).next, 0, 3, false, sessionAccount.id.toSessionId))
    assert(result2.size == 3)
    assert(result2(0).id == messageId6)
    assert(result2(1).id == messageId5)
    assert(result2(2).id == messageId4)

    val result3 = execute(accountMessagesDAO.find(groupId, result2(2).next, 0, 3, false, sessionAccount.id.toSessionId))
    assert(result3.size == 2)
    assert(result3(0).id == messageId3)
    assert(result3(1).id == messageId2)

  }

  test("find earlier") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec7")
    val account1 = createAccount("AccountMessagesDAOSpec8")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val messageId1 = execute(messagesDAO.create(groupId, Some("new message1"), None, sessionAccount.id.toSessionId))
    val messageId2 = execute(messagesDAO.create(groupId, Some("new message2"), None, sessionAccount.id.toSessionId))
    val messageId3 = execute(messagesDAO.create(groupId, Some("new message3"), None, sessionAccount.id.toSessionId))
    val messageId4 = execute(messagesDAO.create(groupId, Some("new message4"), None, sessionAccount.id.toSessionId))
    val messageId5 = execute(messagesDAO.create(groupId, Some("new message5"), None, sessionAccount.id.toSessionId))
    val messageId6 = execute(messagesDAO.create(groupId, Some("new message6"), None, sessionAccount.id.toSessionId))
    val messageId7 = execute(messagesDAO.create(groupId, Some("new message7"), None, sessionAccount.id.toSessionId))
    val messageId8 = execute(messagesDAO.create(groupId, Some("new message8"), None, sessionAccount.id.toSessionId))
    execute(messagesDAO.create(groupId, Some("new message9"), None, sessionAccount.id.toSessionId))

    execute(accountMessagesDAO.create(groupId, messageId1, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId2, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId3, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId4, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId5, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId6, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId7, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId8, sessionAccount.id.toSessionId))

    val result1 = execute(accountMessagesDAO.find(groupId, None, 0, 3, true, sessionAccount.id.toSessionId))
    assert(result1.size == 3)
    assert(result1(0).id == messageId1)
    assert(result1(1).id == messageId2)
    assert(result1(2).id == messageId3)

    val result2 = execute(accountMessagesDAO.find(groupId, result1(2).next, 0, 3,  true, sessionAccount.id.toSessionId))
    assert(result2.size == 3)
    assert(result2(0).id == messageId4)
    assert(result2(1).id == messageId5)
    assert(result2(2).id == messageId6)

    val result3 = execute(accountMessagesDAO.find(groupId, result2(2).next, 0, 3,  true, sessionAccount.id.toSessionId))
    assert(result3.size == 2)
    assert(result3(0).id == messageId7)
    assert(result3(1).id == messageId8)

  }


  test("updateUnreadStatus") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec9")
    val account1 = createAccount("AccountMessagesDAOSpec10")

    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val messageId = execute(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.updateUnread(List(messageId), sessionAccount.id.toSessionId))

    val result2 = execute(db.run(quote(query[AccountMessages].filter(_.accountId == lift(sessionAccount.id)).filter(_.messageId == lift(messageId)))))
    assert(result2.size == 1)
    val userMessage = result2(0)
    assert(userMessage.unread == false)

  }


}
