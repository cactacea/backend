package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class PushNotificationMessagesDAOSpec extends DAOSpec {

  feature("update") (pending)
//  {

//    val sessionUser = createUser("UserMessagesDAOSpec11")
//    val user1 = createUser("UserMessagesDAOSpec12")
//    val user2 = createUser("UserMessagesDAOSpec13")
//    val user3 = createUser("UserMessagesDAOSpec14")
//
//    val channelId = await(channelsDAO.create(Some("new channel name"), false, ChannelPrivacyType.everyone, ChannelAuthorityType.member, sessionUser.id.toSessionId))
//
//    await(userChannelsDAO.create(user1.id, channelId))
//    await(userChannelsDAO.create(user2.id, channelId))
//    await(userChannelsDAO.create(user3.id, channelId))
//    await(userChannelsDAO.create(sessionUser.id, channelId))
//
//    val messageId = await(messagesDAO.create(channelId, Some("new message"), None, sessionUser.id.toSessionId))
//    await(userMessagesDAO.create(channelId, messageId, sessionUser.id.toSessionId))
//
//    val ids = List(
//      sessionUser.id,
//      user1.id,
//      user2.id,
//      user3.id
//    )
//    await(pushNotificationMessagesDAO.update(messageId, ids))
//
//    val result2 = await(db.run(quote(query[UserMessages].filter(_.messageId == lift(messageId)).filter(_.notified == true)).size))
//    assert(result2 == ids.size)
//
//  }

}
