package io.github.cactacea.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.GroupInvitations

class PushNotificationGroupInvitationsDAOSpec extends DAOSpec {

  import db._


  test("update") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec56")
    createAccount("GroupInvitationsDAOSpec57")
    val owner1 = createAccount("GroupInvitationsDAOSpec58")
    val owner2 = createAccount("GroupInvitationsDAOSpec59")
    val owner3 = createAccount("GroupInvitationsDAOSpec60")
    val owner4 = createAccount("GroupInvitationsDAOSpec61")
    val owner5 = createAccount("GroupInvitationsDAOSpec62")

    val groupId1 = execute(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner1.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner2.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner3.id.toSessionId))
    val groupId4 = execute(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner4.id.toSessionId))
    val groupId5 = execute(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner5.id.toSessionId))

    val groupInvitationId1 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val groupInvitationId2 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId2, owner2.id.toSessionId))
    val groupInvitationId3 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val groupInvitationId4 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId4, owner4.id.toSessionId))
    val groupInvitationId5 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    execute(pushNotificationGroupInvitationsDAO.update(groupInvitationId1))
    execute(pushNotificationGroupInvitationsDAO.update(groupInvitationId3))
    execute(pushNotificationGroupInvitationsDAO.update(groupInvitationId5))

    val invitation1 = execute(db.run(query[GroupInvitations].filter(_.id == lift(groupInvitationId1)))).head
    val invitation2 = execute(db.run(query[GroupInvitations].filter(_.id == lift(groupInvitationId2)))).head
    val invitation3 = execute(db.run(query[GroupInvitations].filter(_.id == lift(groupInvitationId3)))).head
    val invitation4 = execute(db.run(query[GroupInvitations].filter(_.id == lift(groupInvitationId4)))).head
    val invitation5 = execute(db.run(query[GroupInvitations].filter(_.id == lift(groupInvitationId5)))).head

    assert(invitation1.notified == true)
    assert(invitation2.notified == false)
    assert(invitation3.notified == true)
    assert(invitation4.notified == false)
    assert(invitation5.notified == true)

  }


}
