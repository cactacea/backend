package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupInviteStatusType, GroupPrivacyType}
import io.github.cactacea.core.helpers.CactaceaDAOTest
import io.github.cactacea.core.infrastructure.models.GroupInvites

class GroupInvitesDAOSpec extends CactaceaDAOTest {

  import db._

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val groupInvitesDAO: GroupInvitesDAO = injector.instance[GroupInvitesDAO]

  test("create") {

    val sessionAccount = this.createAccount(0L)
    val owner1 = this.createAccount(1L)
    val owner2 = this.createAccount(2L)
    val owner3 = this.createAccount(3L)
    val owner4 = this.createAccount(4L)
    val owner5 = this.createAccount(5L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId2, owner2.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId4, owner4.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitesDAO.findAll(None, None, Some(3), sessionAccount.id.toSessionId))
    assert(result1.size == 3)
    val invite1 = result1(0)
    val invite2 = result1(1)
    val invite3 = result1(2)
    assert((invite1._1.accountId, invite1._1.by) == (sessionAccount.id, owner5.id))
    assert((invite2._1.accountId, invite2._1.by) == (sessionAccount.id, owner4.id))
    assert((invite3._1.accountId, invite3._1.by) == (sessionAccount.id, owner3.id))

    val result2 = Await.result(groupInvitesDAO.findAll(Some(invite3._1.invitedAt), None, Some(3), sessionAccount.id.toSessionId))
    assert(result2.size == 2)
    val invite4 = result2(0)
    val invite5 = result2(1)
    assert((invite4._1.accountId, invite4._1.by) == (sessionAccount.id, owner2.id))
    assert((invite5._1.accountId, invite5._1.by) == (sessionAccount.id, owner1.id))


  }

  test("delete") {

    val sessionAccount = this.createAccount(0L)
    val owner = this.createAccount(1L)
    val groupId = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner.id.toSessionId))
    val groupInviteId = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId, owner.id.toSessionId))
    val result = Await.result(groupInvitesDAO.delete(groupInviteId))
    assert(result == true)

  }

  test("exist") {

    val sessionAccount = this.createAccount(0L)
    val owner1 = this.createAccount(1L)
    val owner2 = this.createAccount(2L)
    val owner3 = this.createAccount(3L)
    val owner4 = this.createAccount(4L)
    val owner5 = this.createAccount(5L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitesDAO.exist(sessionAccount.id, groupId1))
    val result2 = Await.result(groupInvitesDAO.exist(sessionAccount.id, groupId2))
    val result3 = Await.result(groupInvitesDAO.exist(sessionAccount.id, groupId3))
    val result4 = Await.result(groupInvitesDAO.exist(sessionAccount.id, groupId4))
    val result5 = Await.result(groupInvitesDAO.exist(sessionAccount.id, groupId5))

    assert(result1 == true)
    assert(result2 == false)
    assert(result3 == true)
    assert(result4 == false)
    assert(result5 == true)

  }

  test("find invite") {

    val sessionAccount = this.createAccount(0L)
    val account1 = this.createAccount(1L)
    val owner1 = this.createAccount(2L)
    val owner2 = this.createAccount(3L)
    val owner3 = this.createAccount(4L)
    val owner4 = this.createAccount(5L)
    val owner5 = this.createAccount(6L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val inviteId1 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val inviteId2 = Await.result(groupInvitesDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    val inviteId3 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val inviteId4 = Await.result(groupInvitesDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    val inviteId5 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitesDAO.find(inviteId1, sessionAccount.id.toSessionId))
    val result2 = Await.result(groupInvitesDAO.find(inviteId2, sessionAccount.id.toSessionId))
    val result3 = Await.result(groupInvitesDAO.find(inviteId3, sessionAccount.id.toSessionId))
    val result4 = Await.result(groupInvitesDAO.find(inviteId4, sessionAccount.id.toSessionId))
    val result5 = Await.result(groupInvitesDAO.find(inviteId5, sessionAccount.id.toSessionId))

    assert(result1.isDefined == true)
    assert(result2.isDefined == false)
    assert(result3.isDefined == true)
    assert(result4.isDefined == false)
    assert(result5.isDefined == true)

  }

  test("findInvites") {

    val sessionAccount = this.createAccount(0L)
    val account1 = this.createAccount(1L)
    val owner1 = this.createAccount(2L)
    val owner2 = this.createAccount(3L)
    val owner3 = this.createAccount(4L)
    val owner4 = this.createAccount(5L)
    val owner5 = this.createAccount(6L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val inviteId1 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitesDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    val inviteId3 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitesDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    val inviteId5 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitesDAO.findAll(None, None, Some(2), sessionAccount.id.toSessionId))
    assert(result1.size == 2)
    val invite1 = result1(0)._1
    val invite2 = result1(1)._1
    assert((invite1.groupId, invite1.id) == (groupId5, inviteId5))
    assert((invite2.groupId, invite2.id) == (groupId3, inviteId3))

    val result2 = Await.result(groupInvitesDAO.findAll(Some(invite2.invitedAt), None, Some(2), sessionAccount.id.toSessionId))
    assert(result2.size == 1)
    val invite3 = result2(0)._1
    assert((invite3.groupId, invite3.id) == (groupId1, inviteId1))

  }

  test("delete by groupId") {

    val sessionAccount = this.createAccount(0L)
    val account1 = this.createAccount(1L)
    val owner1 = this.createAccount(2L)
    val owner2 = this.createAccount(3L)
    val owner3 = this.createAccount(4L)
    val owner4 = this.createAccount(5L)
    val owner5 = this.createAccount(6L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitesDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitesDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitesDAO.deleteByGroupId(groupId1))
    val result2 = Await.result(groupInvitesDAO.deleteByGroupId(groupId2))
    val result3 = Await.result(groupInvitesDAO.deleteByGroupId(groupId3))
    val result4 = Await.result(groupInvitesDAO.deleteByGroupId(groupId4))
    val result5 = Await.result(groupInvitesDAO.deleteByGroupId(groupId5))

    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == true)
    assert(result4 == true)
    assert(result5 == true)

    val count1 = Await.result(db.run(query[GroupInvites].filter(_.groupId == lift(groupId1)).size))
    val count2 = Await.result(db.run(query[GroupInvites].filter(_.groupId == lift(groupId2)).size))
    val count3 = Await.result(db.run(query[GroupInvites].filter(_.groupId == lift(groupId3)).size))
    val count4 = Await.result(db.run(query[GroupInvites].filter(_.groupId == lift(groupId4)).size))
    val count5 = Await.result(db.run(query[GroupInvites].filter(_.groupId == lift(groupId5)).size))

    assert(count1 == 0)
    assert(count2 == 0)
    assert(count3 == 0)
    assert(count4 == 0)
    assert(count5 == 0)

  }

  test("delete by accountId") {

    val sessionAccount = this.createAccount(0L)
    val account1 = this.createAccount(1L)
    val owner1 = this.createAccount(2L)
    val owner2 = this.createAccount(3L)
    val owner3 = this.createAccount(4L)
    val owner4 = this.createAccount(5L)
    val owner5 = this.createAccount(6L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    Await.result(groupInvitesDAO.create(account1.id, groupId2, owner2.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    Await.result(groupInvitesDAO.create(account1.id, groupId4, owner4.id.toSessionId))
    Await.result(groupInvitesDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitesDAO.delete(sessionAccount.id, GroupPrivacyType.everyone, owner1.id.toSessionId))
    val result5 = Await.result(groupInvitesDAO.delete(sessionAccount.id, GroupPrivacyType.everyone, owner5.id.toSessionId))
    assert(result1 == true)
    assert(result5 == true)

    val count = Await.result(db.run(query[GroupInvites].filter(_.accountId == lift(sessionAccount.id)).size))
    assert(count == 1)

  }

  test("update by invitedId") {

    val sessionAccount = this.createAccount(0L)
    val account1 = this.createAccount(1L)
    val owner1 = this.createAccount(2L)
    val owner2 = this.createAccount(3L)
    val owner3 = this.createAccount(4L)
    val owner4 = this.createAccount(5L)
    val owner5 = this.createAccount(6L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val groupInviteId1 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val groupInviteId2 = Await.result(groupInvitesDAO.create(account1.id,       groupId2, owner2.id.toSessionId))
    val groupInviteId3 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val groupInviteId4 = Await.result(groupInvitesDAO.create(account1.id,       groupId4, owner4.id.toSessionId))
    val groupInviteId5 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    Await.result(groupInvitesDAO.update(groupInviteId1, GroupInviteStatusType.rejected,     sessionAccount.id.toSessionId))
    Await.result(groupInvitesDAO.update(groupInviteId2, GroupInviteStatusType.accepted,     account1.id.toSessionId))
    Await.result(groupInvitesDAO.update(groupInviteId3, GroupInviteStatusType.noresponsed,  sessionAccount.id.toSessionId))
    Await.result(groupInvitesDAO.update(groupInviteId4, GroupInviteStatusType.rejected,     account1.id.toSessionId))
    Await.result(groupInvitesDAO.update(groupInviteId5, GroupInviteStatusType.accepted,     sessionAccount.id.toSessionId))

    val invite1 = Await.result(groupInvitesDAO.find(groupInviteId1, sessionAccount.id.toSessionId)).get
    val invite2 = Await.result(groupInvitesDAO.find(groupInviteId2, account1.id.toSessionId)).get
    val invite3 = Await.result(groupInvitesDAO.find(groupInviteId3, sessionAccount.id.toSessionId)).get
    val invite4 = Await.result(groupInvitesDAO.find(groupInviteId4, account1.id.toSessionId)).get
    val invite5 = Await.result(groupInvitesDAO.find(groupInviteId5, sessionAccount.id.toSessionId)).get

    assert(invite1.inviteStatus == GroupInviteStatusType.rejected.toValue)
    assert(invite2.inviteStatus == GroupInviteStatusType.accepted.toValue)
    assert(invite3.inviteStatus == GroupInviteStatusType.noresponsed.toValue)
    assert(invite4.inviteStatus == GroupInviteStatusType.rejected.toValue)
    assert(invite5.inviteStatus == GroupInviteStatusType.accepted.toValue)

  }

  test("update by accountId") {

    val sessionAccount = this.createAccount(0L)
    val account1 = this.createAccount(1L)
    val owner1 = this.createAccount(2L)
    val owner2 = this.createAccount(3L)
    val owner3 = this.createAccount(4L)
    val owner4 = this.createAccount(5L)
    val owner5 = this.createAccount(6L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val groupInviteId1 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val groupInviteId2 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId2, owner2.id.toSessionId))
    val groupInviteId3 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val groupInviteId4 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId4, owner4.id.toSessionId))
    val groupInviteId5 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitesDAO.update(sessionAccount.id, groupId1, GroupInviteStatusType.rejected))
    val result2 = Await.result(groupInvitesDAO.update(sessionAccount.id, groupId2, GroupInviteStatusType.accepted))
    val result3 = Await.result(groupInvitesDAO.update(sessionAccount.id, groupId3, GroupInviteStatusType.noresponsed))
    val result4 = Await.result(groupInvitesDAO.update(sessionAccount.id, groupId4, GroupInviteStatusType.rejected))
    val result5 = Await.result(groupInvitesDAO.update(sessionAccount.id, groupId5, GroupInviteStatusType.accepted))
    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == true)
    assert(result4 == true)
    assert(result5 == true)

    val invite1 = Await.result(groupInvitesDAO.find(groupInviteId1, sessionAccount.id.toSessionId)).get
    val invite2 = Await.result(groupInvitesDAO.find(groupInviteId2, sessionAccount.id.toSessionId)).get
    val invite3 = Await.result(groupInvitesDAO.find(groupInviteId3, sessionAccount.id.toSessionId)).get
    val invite4 = Await.result(groupInvitesDAO.find(groupInviteId4, sessionAccount.id.toSessionId)).get
    val invite5 = Await.result(groupInvitesDAO.find(groupInviteId5, sessionAccount.id.toSessionId)).get

    assert(invite1.inviteStatus == GroupInviteStatusType.rejected.toValue)
    assert(invite2.inviteStatus == GroupInviteStatusType.accepted.toValue)
    assert(invite3.inviteStatus == GroupInviteStatusType.noresponsed.toValue)
    assert(invite4.inviteStatus == GroupInviteStatusType.rejected.toValue)
    assert(invite5.inviteStatus == GroupInviteStatusType.accepted.toValue)

  }

  test("updateNotified") {

    val sessionAccount = this.createAccount(0L)
    val account1 = this.createAccount(1L)
    val owner1 = this.createAccount(2L)
    val owner2 = this.createAccount(3L)
    val owner3 = this.createAccount(4L)
    val owner4 = this.createAccount(5L)
    val owner5 = this.createAccount(6L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner1.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner2.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner3.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner4.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner5.id.toSessionId))

    val groupInviteId1 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId1, owner1.id.toSessionId))
    val groupInviteId2 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId2, owner2.id.toSessionId))
    val groupInviteId3 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId3, owner3.id.toSessionId))
    val groupInviteId4 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId4, owner4.id.toSessionId))
    val groupInviteId5 = Await.result(groupInvitesDAO.create(sessionAccount.id, groupId5, owner5.id.toSessionId))

    val result1 = Await.result(groupInvitesDAO.updateNotified(groupInviteId1, true))
    val result2 = Await.result(groupInvitesDAO.updateNotified(groupInviteId2, false))
    val result3 = Await.result(groupInvitesDAO.updateNotified(groupInviteId3, true))
    val result4 = Await.result(groupInvitesDAO.updateNotified(groupInviteId4, false))
    val result5 = Await.result(groupInvitesDAO.updateNotified(groupInviteId5, true))
    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == true)
    assert(result4 == true)
    assert(result5 == true)

    val invite1 = Await.result(groupInvitesDAO.find(groupInviteId1)).get
    val invite2 = Await.result(groupInvitesDAO.find(groupInviteId2)).get
    val invite3 = Await.result(groupInvitesDAO.find(groupInviteId3)).get
    val invite4 = Await.result(groupInvitesDAO.find(groupInviteId4)).get
    val invite5 = Await.result(groupInvitesDAO.find(groupInviteId5)).get

    assert(invite1.notified == true)
    assert(invite2.notified == false)
    assert(invite3.notified == true)
    assert(invite4.notified == false)
    assert(invite5.notified == true)

    val result6 = Await.result(groupInvitesDAO.findUnNotified(invite2.id))
    assert(result6.isDefined == true)

  }




}
