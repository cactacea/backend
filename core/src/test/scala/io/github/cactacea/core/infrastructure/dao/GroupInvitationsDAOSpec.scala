package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupInvitationStatusType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.GroupInvitations

class GroupInvitationsDAOSpec extends DAOSpec {

  import db._

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val groupInvitationsDAO: GroupInvitationsDAO = injector.instance[GroupInvitationsDAO]

  test("create") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec1")
    val owner1 = createAccount("GroupInvitationsDAOSpec2")
    val owner2 = createAccount("GroupInvitationsDAOSpec3")
    val owner3 = createAccount("GroupInvitationsDAOSpec4")
    val owner4 = createAccount("GroupInvitationsDAOSpec5")
    val owner5 = createAccount("GroupInvitationsDAOSpec6")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId2, owner2.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId4, owner4.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitationsDAO.findAll(None, None, Some(3), sessionAccount.id.toSessionId))
    assert(result1.size == 3)
    val invitation1 = result1(0)
    val invitation2 = result1(1)
    val invitation3 = result1(2)
    assert((invitation1._1.accountId, invitation1._1.by) == (sessionAccount.id, owner5.id))
    assert((invitation2._1.accountId, invitation2._1.by) == (sessionAccount.id, owner4.id))
    assert((invitation3._1.accountId, invitation3._1.by) == (sessionAccount.id, owner3.id))

    val result2 = Await.result(groupInvitationsDAO.findAll(Some(invitation3._1.id.value), None, Some(3), sessionAccount.id.toSessionId))
    assert(result2.size == 2)
    val invitation4 = result2(0)
    val invitation5 = result2(1)
    assert((invitation4._1.accountId, invitation4._1.by) == (sessionAccount.id, owner2.id))
    assert((invitation5._1.accountId, invitation5._1.by) == (sessionAccount.id, owner1.id))


  }

  test("delete") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec7")
    val owner = createAccount("GroupInvitationsDAOSpec8")
    val groupId = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner.id.toSessionId))
    val groupInvitationId = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId, owner.id.toSessionId))
    val result = Await.result(groupInvitationsDAO.delete(groupInvitationId))
    assert(result == true)

  }

  test("exist") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec9")
    val owner1 = createAccount("GroupInvitationsDAOSpec10")
    val owner2 = createAccount("GroupInvitationsDAOSpec11")
    val owner3 = createAccount("GroupInvitationsDAOSpec12")
    val owner4 = createAccount("GroupInvitationsDAOSpec13")
    val owner5 = createAccount("GroupInvitationsDAOSpec14")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitationsDAO.exist(sessionAccount.id, groupId1))
    val result2 = Await.result(groupInvitationsDAO.exist(sessionAccount.id, groupId2))
    val result3 = Await.result(groupInvitationsDAO.exist(sessionAccount.id, groupId3))
    val result4 = Await.result(groupInvitationsDAO.exist(sessionAccount.id, groupId4))
    val result5 = Await.result(groupInvitationsDAO.exist(sessionAccount.id, groupId5))

    assert(result1 == true)
    assert(result2 == false)
    assert(result3 == true)
    assert(result4 == false)
    assert(result5 == true)

  }

  test("find invitation") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec15")
    val account1 = createAccount("GroupInvitationsDAOSpec16")
    val owner1 = createAccount("GroupInvitationsDAOSpec17")
    val owner2 = createAccount("GroupInvitationsDAOSpec18")
    val owner3 = createAccount("GroupInvitationsDAOSpec19")
    val owner4 = createAccount("GroupInvitationsDAOSpec20")
    val owner5 = createAccount("GroupInvitationsDAOSpec21")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val invitationId1 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val invitationId2 = Await.result(groupInvitationsDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    val invitationId3 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val invitationId4 = Await.result(groupInvitationsDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    val invitationId5 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitationsDAO.find(invitationId1, sessionAccount.id.toSessionId))
    val result2 = Await.result(groupInvitationsDAO.find(invitationId2, sessionAccount.id.toSessionId))
    val result3 = Await.result(groupInvitationsDAO.find(invitationId3, sessionAccount.id.toSessionId))
    val result4 = Await.result(groupInvitationsDAO.find(invitationId4, sessionAccount.id.toSessionId))
    val result5 = Await.result(groupInvitationsDAO.find(invitationId5, sessionAccount.id.toSessionId))

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

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val invitationId1 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitationsDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    val invitationId3 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitationsDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    val invitationId5 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitationsDAO.findAll(None, None, Some(2), sessionAccount.id.toSessionId))
    assert(result1.size == 2)
    val invitation1 = result1(0)._1
    val invitation2 = result1(1)._1
    assert((invitation1.groupId, invitation1.id) == (groupId5, invitationId5))
    assert((invitation2.groupId, invitation2.id) == (groupId3, invitationId3))

    val result2 = Await.result(groupInvitationsDAO.findAll(Some(invitation2.id.value), None, Some(2), sessionAccount.id.toSessionId))
    assert(result2.size == 1)
    val invitation3 = result2(0)._1
    assert((invitation3.groupId, invitation3.id) == (groupId1, invitationId1))

  }

  test("delete by groupId") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec29")
    val account1 = createAccount("GroupInvitationsDAOSpec30")
    val owner1 = createAccount("GroupInvitationsDAOSpec31")
    val owner2 = createAccount("GroupInvitationsDAOSpec32")
    val owner3 = createAccount("GroupInvitationsDAOSpec33")
    val owner4 = createAccount("GroupInvitationsDAOSpec34")
    val owner5 = createAccount("GroupInvitationsDAOSpec35")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitationsDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitationsDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitationsDAO.deleteByGroupId(groupId1))
    val result2 = Await.result(groupInvitationsDAO.deleteByGroupId(groupId2))
    val result3 = Await.result(groupInvitationsDAO.deleteByGroupId(groupId3))
    val result4 = Await.result(groupInvitationsDAO.deleteByGroupId(groupId4))
    val result5 = Await.result(groupInvitationsDAO.deleteByGroupId(groupId5))

    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == true)
    assert(result4 == true)
    assert(result5 == true)

    val count1 = Await.result(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId1)).size))
    val count2 = Await.result(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId2)).size))
    val count3 = Await.result(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId3)).size))
    val count4 = Await.result(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId4)).size))
    val count5 = Await.result(db.run(query[GroupInvitations].filter(_.groupId == lift(groupId5)).size))

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

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitationsDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitationsDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitationsDAO.delete(sessionAccount.id, GroupPrivacyType.everyone, owner1.id.toSessionId))
    val result5 = Await.result(groupInvitationsDAO.delete(sessionAccount.id, GroupPrivacyType.everyone, owner5.id.toSessionId))
    assert(result1 == true)
    assert(result5 == true)

    val count = Await.result(db.run(query[GroupInvitations].filter(_.accountId == lift(sessionAccount.id)).size))
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

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val groupInvitationId1 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val groupInvitationId2 = Await.result(groupInvitationsDAO.create(account1.id,       groupId2, owner2.id.toSessionId))
    val groupInvitationId3 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val groupInvitationId4 = Await.result(groupInvitationsDAO.create(account1.id,       groupId4, owner4.id.toSessionId))
    val groupInvitationId5 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    Await.result(groupInvitationsDAO.update(groupInvitationId1, GroupInvitationStatusType.rejected,     sessionAccount.id.toSessionId))
    Await.result(groupInvitationsDAO.update(groupInvitationId2, GroupInvitationStatusType.accepted,     account1.id.toSessionId))
    Await.result(groupInvitationsDAO.update(groupInvitationId3, GroupInvitationStatusType.noResponded,  sessionAccount.id.toSessionId))
    Await.result(groupInvitationsDAO.update(groupInvitationId4, GroupInvitationStatusType.rejected,     account1.id.toSessionId))
    Await.result(groupInvitationsDAO.update(groupInvitationId5, GroupInvitationStatusType.accepted,     sessionAccount.id.toSessionId))

    val invitation1 = Await.result(groupInvitationsDAO.find(groupInvitationId1, sessionAccount.id.toSessionId)).get
    val invitation2 = Await.result(groupInvitationsDAO.find(groupInvitationId2, account1.id.toSessionId)).get
    val invitation3 = Await.result(groupInvitationsDAO.find(groupInvitationId3, sessionAccount.id.toSessionId)).get
    val invitation4 = Await.result(groupInvitationsDAO.find(groupInvitationId4, account1.id.toSessionId)).get
    val invitation5 = Await.result(groupInvitationsDAO.find(groupInvitationId5, sessionAccount.id.toSessionId)).get

    assert(invitation1.invitationStatus == GroupInvitationStatusType.rejected)
    assert(invitation2.invitationStatus == GroupInvitationStatusType.accepted)
    assert(invitation3.invitationStatus == GroupInvitationStatusType.noResponded)
    assert(invitation4.invitationStatus == GroupInvitationStatusType.rejected)
    assert(invitation5.invitationStatus == GroupInvitationStatusType.accepted)

  }

  test("update by accountId") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec49")
    val account1 = createAccount("GroupInvitationsDAOSpec50")
    val owner1 = createAccount("GroupInvitationsDAOSpec51")
    val owner2 = createAccount("GroupInvitationsDAOSpec52")
    val owner3 = createAccount("GroupInvitationsDAOSpec53")
    val owner4 = createAccount("GroupInvitationsDAOSpec54")
    val owner5 = createAccount("GroupInvitationsDAOSpec55")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val groupInvitationId1 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val groupInvitationId2 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId2, owner2.id.toSessionId))
    val groupInvitationId3 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val groupInvitationId4 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId4, owner4.id.toSessionId))
    val groupInvitationId5 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    Await.result(groupInvitationsDAO.update(sessionAccount.id, groupId1, GroupInvitationStatusType.rejected))
    Await.result(groupInvitationsDAO.update(sessionAccount.id, groupId2, GroupInvitationStatusType.accepted))
    Await.result(groupInvitationsDAO.update(sessionAccount.id, groupId3, GroupInvitationStatusType.noResponded))
    Await.result(groupInvitationsDAO.update(sessionAccount.id, groupId4, GroupInvitationStatusType.rejected))
    Await.result(groupInvitationsDAO.update(sessionAccount.id, groupId5, GroupInvitationStatusType.accepted))

    val invitation1 = Await.result(groupInvitationsDAO.find(groupInvitationId1, sessionAccount.id.toSessionId)).get
    val invitation2 = Await.result(groupInvitationsDAO.find(groupInvitationId2, sessionAccount.id.toSessionId)).get
    val invitation3 = Await.result(groupInvitationsDAO.find(groupInvitationId3, sessionAccount.id.toSessionId)).get
    val invitation4 = Await.result(groupInvitationsDAO.find(groupInvitationId4, sessionAccount.id.toSessionId)).get
    val invitation5 = Await.result(groupInvitationsDAO.find(groupInvitationId5, sessionAccount.id.toSessionId)).get

    assert(invitation1.invitationStatus == GroupInvitationStatusType.rejected)
    assert(invitation2.invitationStatus == GroupInvitationStatusType.accepted)
    assert(invitation3.invitationStatus == GroupInvitationStatusType.noResponded)
    assert(invitation4.invitationStatus == GroupInvitationStatusType.rejected)
    assert(invitation5.invitationStatus == GroupInvitationStatusType.accepted)

  }

  test("updateNotified") {

    val sessionAccount = createAccount("GroupInvitationsDAOSpec56")
    val account1 = createAccount("GroupInvitationsDAOSpec57")
    val owner1 = createAccount("GroupInvitationsDAOSpec58")
    val owner2 = createAccount("GroupInvitationsDAOSpec59")
    val owner3 = createAccount("GroupInvitationsDAOSpec60")
    val owner4 = createAccount("GroupInvitationsDAOSpec61")
    val owner5 = createAccount("GroupInvitationsDAOSpec62")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val groupInvitationId1 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val groupInvitationId2 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId2, owner2.id.toSessionId))
    val groupInvitationId3 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val groupInvitationId4 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId4, owner4.id.toSessionId))
    val groupInvitationId5 = Await.result(groupInvitationsDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitationsDAO.updateNotified(groupInvitationId1, true))
    val result2 = Await.result(groupInvitationsDAO.updateNotified(groupInvitationId2, false))
    val result3 = Await.result(groupInvitationsDAO.updateNotified(groupInvitationId3, true))
    val result4 = Await.result(groupInvitationsDAO.updateNotified(groupInvitationId4, false))
    val result5 = Await.result(groupInvitationsDAO.updateNotified(groupInvitationId5, true))
    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == true)
    assert(result4 == true)
    assert(result5 == true)

    val invitation1 = Await.result(groupInvitationsDAO.find(groupInvitationId1)).get
    val invitation2 = Await.result(groupInvitationsDAO.find(groupInvitationId2)).get
    val invitation3 = Await.result(groupInvitationsDAO.find(groupInvitationId3)).get
    val invitation4 = Await.result(groupInvitationsDAO.find(groupInvitationId4)).get
    val invitation5 = Await.result(groupInvitationsDAO.find(groupInvitationId5)).get

    assert(invitation1.notified == true)
    assert(invitation2.notified == false)
    assert(invitation3.notified == true)
    assert(invitation4.notified == false)
    assert(invitation5.notified == true)

    val result6 = Await.result(groupInvitationsDAO.find(invitation2.id))
    assert(result6.get.notified == false)

  }




}
