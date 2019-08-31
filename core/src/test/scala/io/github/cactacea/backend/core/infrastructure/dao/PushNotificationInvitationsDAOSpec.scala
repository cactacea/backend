package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class PushNotificationInvitationsDAOSpec extends DAOSpec {


  feature("update") (pending)
//  {
//
//    val sessionAccount = createAccount("ChannelInvitationsDAOSpec56")
//    createAccount("ChannelInvitationsDAOSpec57")
//    val owner1 = createAccount("ChannelInvitationsDAOSpec58")
//    val owner2 = createAccount("ChannelInvitationsDAOSpec59")
//    val owner3 = createAccount("ChannelInvitationsDAOSpec60")
//    val owner4 = createAccount("ChannelInvitationsDAOSpec61")
//    val owner5 = createAccount("ChannelInvitationsDAOSpec62")
//
//    val channelId1 = await(channelsDAO.create(Some("New Channel Name1"), true, ChannelPrivacyType.everyone, ChannelAuthorityType.owner, owner1.id.toSessionId))
//    val channelId2 = await(channelsDAO.create(Some("New Channel Name2"), true, ChannelPrivacyType.everyone, ChannelAuthorityType.owner, owner2.id.toSessionId))
//    val channelId3 = await(channelsDAO.create(Some("New Channel Name3"), true, ChannelPrivacyType.everyone, ChannelAuthorityType.owner, owner3.id.toSessionId))
//    val channelId4 = await(channelsDAO.create(Some("New Channel Name4"), true, ChannelPrivacyType.everyone, ChannelAuthorityType.owner, owner4.id.toSessionId))
//    val channelId5 = await(channelsDAO.create(Some("New Channel Name5"), true, ChannelPrivacyType.everyone, ChannelAuthorityType.owner, owner5.id.toSessionId))
//
//    val channelInvitationId1 = await(channelInvitationsDAO.create(sessionAccount.id, channelId1, owner1.id.toSessionId))
//    val channelInvitationId2 = await(channelInvitationsDAO.create(sessionAccount.id, channelId2, owner2.id.toSessionId))
//    val channelInvitationId3 = await(channelInvitationsDAO.create(sessionAccount.id, channelId3, owner3.id.toSessionId))
//    val channelInvitationId4 = await(channelInvitationsDAO.create(sessionAccount.id, channelId4, owner4.id.toSessionId))
//    val channelInvitationId5 = await(channelInvitationsDAO.create(sessionAccount.id, channelId5, owner5.id.toSessionId))
//
//    await(pushNotificationChannelInvitationsDAO.update(channelInvitationId1))
//    await(pushNotificationChannelInvitationsDAO.update(channelInvitationId3))
//    await(pushNotificationChannelInvitationsDAO.update(channelInvitationId5))
//
//    val invitation1 = await(db.run(query[ChannelInvitations].filter(_.id == lift(channelInvitationId1)))).head
//    val invitation2 = await(db.run(query[ChannelInvitations].filter(_.id == lift(channelInvitationId2)))).head
//    val invitation3 = await(db.run(query[ChannelInvitations].filter(_.id == lift(channelInvitationId3)))).head
//    val invitation4 = await(db.run(query[ChannelInvitations].filter(_.id == lift(channelInvitationId4)))).head
//    val invitation5 = await(db.run(query[ChannelInvitations].filter(_.id == lift(channelInvitationId5)))).head
//
//    assert(invitation1.notified == true)
//    assert(invitation2.notified == false)
//    assert(invitation3.notified == true)
//    assert(invitation4.notified == false)
//    assert(invitation5.notified == true)
//
//  }


}
