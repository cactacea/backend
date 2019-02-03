package io.github.cactacea.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.AccountMessages

class PushNotificationMessagesDAOSpec extends DAOSpec {

  import db._

  test("update") {

    val sessionAccount = createAccount("AccountMessagesDAOSpec11")
    val account1 = createAccount("AccountMessagesDAOSpec12")
    val account2 = createAccount("AccountMessagesDAOSpec13")
    val account3 = createAccount("AccountMessagesDAOSpec14")

    val groupId = execute(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))

    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(account2.id, groupId))
    execute(accountGroupsDAO.create(account3.id, groupId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId))

    val messageId = execute(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))
    execute(accountMessagesDAO.create(groupId, messageId, sessionAccount.id.toSessionId))

    val ids = List(
      sessionAccount.id,
      account1.id,
      account2.id,
      account3.id
    )
    execute(pushNotificationMessagesDAO.update(messageId, ids))

    val result2 = execute(db.run(quote(query[AccountMessages].filter(_.messageId == lift(messageId)).filter(_.notified == true)).size))
    assert(result2 == ids.size)

  }

}
