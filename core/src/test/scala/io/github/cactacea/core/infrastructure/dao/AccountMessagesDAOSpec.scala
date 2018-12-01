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
      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))
    val (messageId, _) = execute(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, sessionAccount.id.toSessionId))

  }

  test("delete") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec3")
    val account1 = createAccount("AccountMessagesDAOSpec4")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone,
      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val (messageId1, _) = execute(messagesDAO.create(groupId, Some("new message1"), 1, None, sessionAccount.id.toSessionId))
    val (messageId2, _) = execute(messagesDAO.create(groupId, Some("new message2"), 1, None, sessionAccount.id.toSessionId))
    val (messageId3, _) = execute(messagesDAO.create(groupId, Some("new message3"), 1, None, sessionAccount.id.toSessionId))
    val (messageId4, _) = execute(messagesDAO.create(groupId, Some("new message4"), 1, None, sessionAccount.id.toSessionId))
    val (messageId5, _) = execute(messagesDAO.create(groupId, Some("new message5"), 1, None, sessionAccount.id.toSessionId))

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
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    execute(messagesDAO.create(groupId, Some("new message1"), 1, None, sessionAccount.id.toSessionId))
    val (messageId2, _) = execute(messagesDAO.create(groupId, Some("new message2"), 1, None, sessionAccount.id.toSessionId))
    val (messageId3, _) = execute(messagesDAO.create(groupId, Some("new message3"), 1, None, sessionAccount.id.toSessionId))
    val (messageId4, _) = execute(messagesDAO.create(groupId, Some("new message4"), 1, None, sessionAccount.id.toSessionId))
    val (messageId5, _) = execute(messagesDAO.create(groupId, Some("new message5"), 1, None, sessionAccount.id.toSessionId))
    val (messageId6, _) = execute(messagesDAO.create(groupId, Some("new message6"), 1, None, sessionAccount.id.toSessionId))
    val (messageId7, _) = execute(messagesDAO.create(groupId, Some("new message7"), 1, None, sessionAccount.id.toSessionId))
    val (messageId8, _) = execute(messagesDAO.create(groupId, Some("new message8"), 1, None, sessionAccount.id.toSessionId))
    val (messageId9, _) = execute(messagesDAO.create(groupId, Some("new message9"), 1, None, sessionAccount.id.toSessionId))

    execute(accountMessagesDAO.create(groupId, messageId2, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId3, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId4, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId5, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId6, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId7, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId8, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId9, sessionAccount.id.toSessionId))

    val result1 = execute(accountMessagesDAO.findOlder(groupId, None, 0, 3, sessionAccount.id.toSessionId))
    assert(result1.size == 3)
    val message1 = result1(0)._2
    val message2 = result1(1)._2
    val message3 = result1(2)._2
    assert(message1.messageId == messageId9)
    assert(message2.messageId == messageId8)
    assert(message3.messageId == messageId7)

    val result2 = execute(accountMessagesDAO.findOlder(groupId, Some(message3.postedAt), 0, 3, sessionAccount.id.toSessionId))
    assert(result2.size == 3)
    val message4 = result2(0)._2
    val message5 = result2(1)._2
    val message6 = result2(2)._2
    assert(message4.messageId == messageId6)
    assert(message5.messageId == messageId5)
    assert(message6.messageId == messageId4)

    val result3 = execute(accountMessagesDAO.findOlder(groupId, Some(message6.postedAt), 0, 3, sessionAccount.id.toSessionId))
    assert(result3.size == 2)
    val message7 = result3(0)._2
    val message8 = result3(1)._2
    assert(message7.messageId == messageId3)
    assert(message8.messageId == messageId2)

  }

  test("find earlier") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec7")
    val account1 = createAccount("AccountMessagesDAOSpec8")
    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val (messageId1, _) = execute(messagesDAO.create(groupId, Some("new message1"), 1, None, sessionAccount.id.toSessionId))
    val (messageId2, _) = execute(messagesDAO.create(groupId, Some("new message2"), 1, None, sessionAccount.id.toSessionId))
    val (messageId3, _) = execute(messagesDAO.create(groupId, Some("new message3"), 1, None, sessionAccount.id.toSessionId))
    val (messageId4, _) = execute(messagesDAO.create(groupId, Some("new message4"), 1, None, sessionAccount.id.toSessionId))
    val (messageId5, _) = execute(messagesDAO.create(groupId, Some("new message5"), 1, None, sessionAccount.id.toSessionId))
    val (messageId6, _) = execute(messagesDAO.create(groupId, Some("new message6"), 1, None, sessionAccount.id.toSessionId))
    val (messageId7, _) = execute(messagesDAO.create(groupId, Some("new message7"), 1, None, sessionAccount.id.toSessionId))
    val (messageId8, _) = execute(messagesDAO.create(groupId, Some("new message8"), 1, None, sessionAccount.id.toSessionId))
    execute(messagesDAO.create(groupId, Some("new message9"), 1, None, sessionAccount.id.toSessionId))

    execute(accountMessagesDAO.create(groupId, messageId1, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId2, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId3, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId4, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId5, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId6, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId7, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId8, sessionAccount.id.toSessionId))

    val result1 = execute(accountMessagesDAO.findEarlier(groupId, None, 0, 3, sessionAccount.id.toSessionId))
    assert(result1.size == 3)
    val message1 = result1(0)._2
    val message2 = result1(1)._2
    val message3 = result1(2)._2
    assert(message1.messageId == messageId1)
    assert(message2.messageId == messageId2)
    assert(message3.messageId == messageId3)

    val result2 = execute(accountMessagesDAO.findEarlier(groupId, Some(message3.postedAt), 0, 3, sessionAccount.id.toSessionId))
    assert(result2.size == 3)
    val message4 = result2(0)._2
    val message5 = result2(1)._2
    val message6 = result2(2)._2
    assert(message4.messageId == messageId4)
    assert(message5.messageId == messageId5)
    assert(message6.messageId == messageId6)

    val result3 = execute(accountMessagesDAO.findEarlier(groupId, Some(message6.postedAt), 0, 3, sessionAccount.id.toSessionId))
    assert(result3.size == 2)
    val message7 = result3(0)._2
    val message8 = result3(1)._2
    assert(message7.messageId == messageId7)
    assert(message8.messageId == messageId8)

  }


  test("updateUnreadStatus") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec9")
    val account1 = createAccount("AccountMessagesDAOSpec10")

    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val (messageId, _) = execute(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.updateUnread(List(messageId), sessionAccount.id.toSessionId))

    val result2 = execute(db.run(quote(query[AccountMessages].filter(_.accountId == lift(sessionAccount.id)).filter(_.messageId == lift(messageId)))))
    assert(result2.size == 1)
    val userMessage = result2(0)
    assert(userMessage.unread == false)

  }

  test("updateNotified") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec11")
    val account1 = createAccount("AccountMessagesDAOSpec12")
    val account2 = createAccount("AccountMessagesDAOSpec13")
    val account3 = createAccount("AccountMessagesDAOSpec14")

    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(account2.id, groupId))
    execute(accountGroupsDAO.create(account3.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val (messageId, _) = execute(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, sessionAccount.id.toSessionId))

    val ids = List(
      sessionAccount.id,
      account1.id,
      account2.id,
      account3.id
    )
    execute(accountMessagesDAO.updateNotified(messageId, ids))

    val result2 = execute(db.run(quote(query[AccountMessages].filter(_.messageId == lift(messageId)).filter(_.notified == true)).size))
    assert(result2 == ids.size)

  }

}
