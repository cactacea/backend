package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class PushNotificationMessagesDAOSpec extends DAOSpec {

  feature("update") (pending)
//  {

//    val sessionAccount = createAccount("AccountMessagesDAOSpec11")
//    val account1 = createAccount("AccountMessagesDAOSpec12")
//    val account2 = createAccount("AccountMessagesDAOSpec13")
//    val account3 = createAccount("AccountMessagesDAOSpec14")
//
//    val groupId = await(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))
//
//    await(accountGroupsDAO.create(account1.id, groupId))
//    await(accountGroupsDAO.create(account2.id, groupId))
//    await(accountGroupsDAO.create(account3.id, groupId))
//    await(accountGroupsDAO.create(sessionAccount.id, groupId))
//
//    val messageId = await(messagesDAO.create(groupId, Some("new message"), None, sessionAccount.id.toSessionId))
//    await(accountMessagesDAO.create(groupId, messageId, sessionAccount.id.toSessionId))
//
//    val ids = List(
//      sessionAccount.id,
//      account1.id,
//      account2.id,
//      account3.id
//    )
//    await(pushNotificationMessagesDAO.update(messageId, ids))
//
//    val result2 = await(db.run(quote(query[AccountMessages].filter(_.messageId == lift(messageId)).filter(_.notified == true)).size))
//    assert(result2 == ids.size)
//
//  }

}
