package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupInvitationStatusType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.GroupInvitations

class GroupInvitationsDAOSpec extends DAOSpec {

  import db._


  test("create") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec1")
    val owner1 = createAccount("GroupInvitationsDAOSpec2")
    val owner2 = createAccount("GroupInvitationsDAOSpec3")
    val owner3 = createAccount("GroupInvitationsDAOSpec4")
    val owner4 = createAccount("GroupInvitationsDAOSpec5")
    val owner5 = createAccount("GroupInvitationsDAOSpec6")

    val groupId1 = execute(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner1.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner2.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner3.id.toSessionId))
    val groupId4 = execute(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner4.id.toSessionId))
    val groupId5 = execute(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner5.id.toSessionId))

    execute(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId2, owner2.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId4, owner4.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = execute(groupInvitationsDAO.find(None, 0, 3, sessionAccount.id.toSessionId))
    assert(result1.size == 3)
    val invitation1 = result1(0)
    val invitation2 = result1(1)
    val invitation3 = result1(2)
//    assert(invitation1.account.id == sessionAccount.id)
//    assert(invitation2.account.id == sessionAccount.id)
//    assert(invitation3.account.id == sessionAccount.id)
    assert(invitation1.account.id == owner5.id)
    assert(invitation2.account.id == owner4.id)
    assert(invitation3.account.id == owner3.id)


    val result2 = execute(groupInvitationsDAO.find(invitation3.next, 0, 3, sessionAccount.id.toSessionId))
    assert(result2.size == 2)
    val invitation4 = result2(0)
    val invitation5 = result2(1)

//    assert(invitation4.account.id == sessionAccount.id)
//    assert(invitation5.account.id == sessionAccount.id)
    assert(invitation4.account.id == owner2.id)
    assert(invitation5.account.id == owner1.id)

  }

  test("delete") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec7")
    val owner = createAccount("GroupInvitationsDAOSpec8")
    val groupId = execute(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner.id.toSessionId))
    val groupInvitationId = execute(groupInvitationsDAO.create(sessionAccount.id, groupId, owner.id.toSessionId))
    execute(groupInvitationsDAO.delete(groupInvitationId))

  }

  test("exist") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec9")
    val owner1 = createAccount("GroupInvitationsDAOSpec10")
    val owner2 = createAccount("GroupInvitationsDAOSpec11")
    val owner3 = createAccount("GroupInvitationsDAOSpec12")
    val owner4 = createAccount("GroupInvitationsDAOSpec13")
    val owner5 = createAccount("GroupInvitationsDAOSpec14")

    val groupId1 = execute(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner1.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner2.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner3.id.toSessionId))
    val groupId4 = execute(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner4.id.toSessionId))
    val groupId5 = execute(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner5.id.toSessionId))

    execute(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = execute(groupInvitationsDAO.findExist(sessionAccount.id, groupId1))
    val result2 = execute(groupInvitationsDAO.findExist(sessionAccount.id, groupId2))
    val result3 = execute(groupInvitationsDAO.findExist(sessionAccount.id, groupId3))
    val result4 = execute(groupInvitationsDAO.findExist(sessionAccount.id, groupId4))
    val result5 = execute(groupInvitationsDAO.findExist(sessionAccount.id, groupId5))

    assert(result1 == true)
    assert(result2 == false)
    assert(result3 == true)
    assert(result4 == false)
    assert(result5 == true)

  }

  test("find groupInvitation") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec15")
    val account1 = createAccount("GroupInvitationsDAOSpec16")
    val owner1 = createAccount("GroupInvitationsDAOSpec17")
    val owner2 = createAccount("GroupInvitationsDAOSpec18")
    val owner3 = createAccount("GroupInvitationsDAOSpec19")
    val owner4 = createAccount("GroupInvitationsDAOSpec20")
    val owner5 = createAccount("GroupInvitationsDAOSpec21")

    val groupId1 = execute(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner1.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner2.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner3.id.toSessionId))
    val groupId4 = execute(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner4.id.toSessionId))
    val groupId5 = execute(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner5.id.toSessionId))

    val invitationId1 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val invitationId2 = execute(groupInvitationsDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    val invitationId3 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val invitationId4 = execute(groupInvitationsDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    val invitationId5 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = execute(helperDAO.selectGroupInvitation(invitationId1, sessionAccount.id.toSessionId))
    val result2 = execute(helperDAO.selectGroupInvitation(invitationId2, sessionAccount.id.toSessionId))
    val result3 = execute(helperDAO.selectGroupInvitation(invitationId3, sessionAccount.id.toSessionId))
    val result4 = execute(helperDAO.selectGroupInvitation(invitationId4, sessionAccount.id.toSessionId))
    val result5 = execute(helperDAO.selectGroupInvitation(invitationId5, sessionAccount.id.toSessionId))

    assert(result1.isDefined == true)
    assert(result2.isDefined == false)
    assert(result3.isDefined == true)
    assert(result4.isDefined == false)
    assert(result5.isDefined == true)

  }

  test("findInvitations") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec22")
    val account1 = createAccount("GroupInvitationsDAOSpec23")
    val owner1 = createAccount("GroupInvitationsDAOSpec24")
    val owner2 = createAccount("GroupInvitationsDAOSpec25")
    val owner3 = createAccount("GroupInvitationsDAOSpec26")
    val owner4 = createAccount("GroupInvitationsDAOSpec27")
    val owner5 = createAccount("GroupInvitationsDAOSpec28")

    val groupId1 = execute(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner1.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner2.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner3.id.toSessionId))
    val groupId4 = execute(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner4.id.toSessionId))
    val groupId5 = execute(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner5.id.toSessionId))

    val invitationId1 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    execute(groupInvitationsDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    val invitationId3 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    execute(groupInvitationsDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    val invitationId5 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = execute(groupInvitationsDAO.find(None, 0, 2, sessionAccount.id.toSessionId))
    assert(result1.size == 2)
    val invitation1 = result1(0)
    val invitation2 = result1(1)
    assert(invitation1.group.id == groupId5)
    assert(invitation2.group.id == groupId3)

    assert(invitation1.id == invitationId5)
    assert(invitation2.id == invitationId3)

    val result2 = execute(groupInvitationsDAO.find(Some(invitation2.id.value), 0, 2, sessionAccount.id.toSessionId))
    assert(result2.size == 1)
    val invitation3 = result2(0)
    assert(invitation3.group.id == groupId1)
    assert(invitation3.id == invitationId1)

  }

  test("delete by groupId") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec29")
    val account1 = createAccount("GroupInvitationsDAOSpec30")
    val owner1 = createAccount("GroupInvitationsDAOSpec31")
    val owner2 = createAccount("GroupInvitationsDAOSpec32")
    val owner3 = createAccount("GroupInvitationsDAOSpec33")
    val owner4 = createAccount("GroupInvitationsDAOSpec34")
    val owner5 = createAccount("GroupInvitationsDAOSpec35")

    val groupId1 = execute(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner1.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner2.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner3.id.toSessionId))
    val groupId4 = execute(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner4.id.toSessionId))
    val groupId5 = execute(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner5.id.toSessionId))

    execute(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    execute(groupInvitationsDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    execute(groupInvitationsDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    execute(groupInvitationsDAO.deleteByGroupId(groupId1))
    execute(groupInvitationsDAO.deleteByGroupId(groupId2))
    execute(groupInvitationsDAO.deleteByGroupId(groupId3))
    execute(groupInvitationsDAO.deleteByGroupId(groupId4))
    execute(groupInvitationsDAO.deleteByGroupId(groupId5))

    val count1 = execute(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId1)).size))
    val count2 = execute(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId2)).size))
    val count3 = execute(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId3)).size))
    val count4 = execute(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId4)).size))
    val count5 = execute(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId5)).size))

    assert(count1 == 0)
    assert(count2 == 0)
    assert(count3 == 0)
    assert(count4 == 0)
    assert(count5 == 0)

  }

  test("delete by accountId") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec36")
    val account1 = createAccount("GroupInvitationsDAOSpec37")
    val owner1 = createAccount("GroupInvitationsDAOSpec38")
    val owner2 = createAccount("GroupInvitationsDAOSpec39")
    val owner3 = createAccount("GroupInvitationsDAOSpec40")
    val owner4 = createAccount("GroupInvitationsDAOSpec41")
    val owner5 = createAccount("GroupInvitationsDAOSpec42")

    val groupId1 = execute(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner1.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner2.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner3.id.toSessionId))
    val groupId4 = execute(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner4.id.toSessionId))
    val groupId5 = execute(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner5.id.toSessionId))

    execute(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    execute(groupInvitationsDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    execute(groupInvitationsDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    execute(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))
    execute(groupInvitationsDAO.delete(sessionAccount.id, GroupPrivacyType.everyone, owner1.id.toSessionId))
    execute(groupInvitationsDAO.delete(sessionAccount.id, GroupPrivacyType.everyone, owner5.id.toSessionId))

    val count = execute(db.run(query[GroupInvitations].filter(_.accountId == lift(sessionAccount.id)).size))
    assert(count == 1)

  }

  test("update by invitationdId") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec43")
    val account1 = createAccount("GroupInvitationsDAOSpec44")
    val owner1 = createAccount("GroupInvitationsDAOSpec45")
    val owner2 = createAccount("GroupInvitationsDAOSpec46")
    val owner3 = createAccount("GroupInvitationsDAOSpec47")
    val owner4 = createAccount("GroupInvitationsDAOSpec47")
    val owner5 = createAccount("GroupInvitationsDAOSpec48")

    val groupId1 = execute(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner1.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner2.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner3.id.toSessionId))
    val groupId4 = execute(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner4.id.toSessionId))
    val groupId5 = execute(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner5.id.toSessionId))

    val groupInvitationId1 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val groupInvitationId2 = execute(groupInvitationsDAO.create(account1.id,       groupId2, owner2.id.toSessionId))
    val groupInvitationId3 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val groupInvitationId4 = execute(groupInvitationsDAO.create(account1.id,       groupId4, owner4.id.toSessionId))
    val groupInvitationId5 = execute(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    execute(groupInvitationsDAO.update(groupInvitationId1, GroupInvitationStatusType.rejected,     sessionAccount.id.toSessionId))
    execute(groupInvitationsDAO.update(groupInvitationId2, GroupInvitationStatusType.accepted,     account1.id.toSessionId))
    execute(groupInvitationsDAO.update(groupInvitationId3, GroupInvitationStatusType.noResponded,  sessionAccount.id.toSessionId))
    execute(groupInvitationsDAO.update(groupInvitationId4, GroupInvitationStatusType.rejected,     account1.id.toSessionId))
    execute(groupInvitationsDAO.update(groupInvitationId5, GroupInvitationStatusType.accepted,     sessionAccount.id.toSessionId))

    val invitation1 = execute(helperDAO.selectGroupInvitation(groupInvitationId1, sessionAccount.id.toSessionId)).get
    val invitation2 = execute(helperDAO.selectGroupInvitation(groupInvitationId2, account1.id.toSessionId)).get
    val invitation3 = execute(helperDAO.selectGroupInvitation(groupInvitationId3, sessionAccount.id.toSessionId)).get
    val invitation4 = execute(helperDAO.selectGroupInvitation(groupInvitationId4, account1.id.toSessionId)).get
    val invitation5 = execute(helperDAO.selectGroupInvitation(groupInvitationId5, sessionAccount.id.toSessionId)).get

    assert(invitation1.invitationStatus == GroupInvitationStatusType.rejected)
    assert(invitation2.invitationStatus == GroupInvitationStatusType.accepted)
    assert(invitation3.invitationStatus == GroupInvitationStatusType.noResponded)
    assert(invitation4.invitationStatus == GroupInvitationStatusType.rejected)
    assert(invitation5.invitationStatus == GroupInvitationStatusType.accepted)

  }

  test("update by accountId") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec49")
//    val account1 = createAccount("GroupInvitationsDAOSpec50")
    val owner1 = createAccount("GroupInvitationsDAOSpec51")
    val owner2 = createAccount("GroupInvitationsDAOSpec52")
    val owner3 = createAccount("GroupInvitationsDAOSpec53")
    val owner4 = createAccount("GroupInvitationsDAOSpec54")
    val owner5 = createAccount("GroupInvitationsDAOSpec55")

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

    execute(groupInvitationsDAO.update(groupId1, sessionAccount.id, GroupInvitationStatusType.rejected))
    execute(groupInvitationsDAO.update(groupId2, sessionAccount.id, GroupInvitationStatusType.accepted))
    execute(groupInvitationsDAO.update(groupId3, sessionAccount.id, GroupInvitationStatusType.noResponded))
    execute(groupInvitationsDAO.update(groupId4, sessionAccount.id, GroupInvitationStatusType.rejected))
    execute(groupInvitationsDAO.update(groupId5, sessionAccount.id, GroupInvitationStatusType.accepted))

    val invitation1 = execute(helperDAO.selectGroupInvitation(groupInvitationId1, sessionAccount.id.toSessionId)).get
    val invitation2 = execute(helperDAO.selectGroupInvitation(groupInvitationId2, sessionAccount.id.toSessionId)).get
    val invitation3 = execute(helperDAO.selectGroupInvitation(groupInvitationId3, sessionAccount.id.toSessionId)).get
    val invitation4 = execute(helperDAO.selectGroupInvitation(groupInvitationId4, sessionAccount.id.toSessionId)).get
    val invitation5 = execute(helperDAO.selectGroupInvitation(groupInvitationId5, sessionAccount.id.toSessionId)).get

    assert(invitation1.invitationStatus == GroupInvitationStatusType.rejected)
    assert(invitation2.invitationStatus == GroupInvitationStatusType.accepted)
    assert(invitation3.invitationStatus == GroupInvitationStatusType.noResponded)
    assert(invitation4.invitationStatus == GroupInvitationStatusType.rejected)
    assert(invitation5.invitationStatus == GroupInvitationStatusType.accepted)

  }

  test("updateNotified") {

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

    execute(groupInvitationsDAO.updateNotified(groupInvitationId1, true))
    execute(groupInvitationsDAO.updateNotified(groupInvitationId2, false))
    execute(groupInvitationsDAO.updateNotified(groupInvitationId3, true))
    execute(groupInvitationsDAO.updateNotified(groupInvitationId4, false))
    execute(groupInvitationsDAO.updateNotified(groupInvitationId5, true))

    val invitation1 = execute(groupInvitationsDAO.find(groupInvitationId1)).get
    val invitation2 = execute(groupInvitationsDAO.find(groupInvitationId2)).get
    val invitation3 = execute(groupInvitationsDAO.find(groupInvitationId3)).get
    val invitation4 = execute(groupInvitationsDAO.find(groupInvitationId4)).get
    val invitation5 = execute(groupInvitationsDAO.find(groupInvitationId5)).get

    assert(invitation1.notified == true)
    assert(invitation2.notified == false)
    assert(invitation3.notified == true)
    assert(invitation4.notified == false)
    assert(invitation5.notified == true)

    val result6 = execute(groupInvitationsDAO.find(invitation2.id))
    assert(result6.get.notified == false)

  }


}
