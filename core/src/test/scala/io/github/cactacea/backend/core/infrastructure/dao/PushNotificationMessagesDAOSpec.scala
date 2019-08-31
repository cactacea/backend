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
//    val channelId = await(channelsDAO.create(Some("new channel name"), false, ChannelPrivacyType.everyone, ChannelAuthorityType.member, sessionAccount.id.toSessionId))
//
//    await(accountChannelsDAO.create(account1.id, channelId))
//    await(accountChannelsDAO.create(account2.id, channelId))
//    await(accountChannelsDAO.create(account3.id, channelId))
//    await(accountChannelsDAO.create(sessionAccount.id, channelId))
//
//    val messageId = await(messagesDAO.create(channelId, Some("new message"), None, sessionAccount.id.toSessionId))
//    await(accountMessagesDAO.create(channelId, messageId, sessionAccount.id.toSessionId))
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
