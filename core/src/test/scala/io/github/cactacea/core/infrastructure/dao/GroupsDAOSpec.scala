package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.specs.DAOSpec

class GroupsDAOSpec extends DAOSpec {

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val blocksDAO: BlocksDAO = injector.instance[BlocksDAO]

  test("create") {

    val sessionAccount = this.createAccount(0L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.follows,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.follows,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId8 = Await.result(groupsDAO.create(Some("New Group Name8"), false, GroupPrivacyType.friends,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))

    assert(Await.result(groupsDAO.exist(groupId1)) == true)
    assert(Await.result(groupsDAO.exist(groupId2)) == true)
    assert(Await.result(groupsDAO.exist(groupId3)) == true)
    assert(Await.result(groupsDAO.exist(groupId4)) == true)
    assert(Await.result(groupsDAO.exist(groupId5)) == true)
    assert(Await.result(groupsDAO.exist(groupId6)) == true)
    assert(Await.result(groupsDAO.exist(groupId7)) == true)
    assert(Await.result(groupsDAO.exist(groupId8)) == true)

  }

  test("delete") {

    val sessionAccount = this.createAccount(0L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.follows,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.follows,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId8 = Await.result(groupsDAO.create(Some("New Group Name8"), false, GroupPrivacyType.friends,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))

    Await.result(groupsDAO.delete(groupId1))
    assert(Await.result(groupsDAO.exist(groupId1)) == false)
    assert(Await.result(groupsDAO.exist(groupId2)) == true)
    assert(Await.result(groupsDAO.exist(groupId3)) == true)
    assert(Await.result(groupsDAO.exist(groupId4)) == true)
    assert(Await.result(groupsDAO.exist(groupId5)) == true)
    assert(Await.result(groupsDAO.exist(groupId6)) == true)
    assert(Await.result(groupsDAO.exist(groupId7)) == true)
    assert(Await.result(groupsDAO.exist(groupId8)) == true)

  }

  test("createOneToOne") {

    val sessionAccount = this.createAccount(0L)

    val groupId1 = Await.result(groupsDAO.create(sessionAccount.id.toSessionId))
    assert(Await.result(groupsDAO.exist(groupId1)) == true)


  }

  test("update Account count") {

    val sessionAccount = this.createAccount(0L)

    val groupId1 = Await.result(groupsDAO.create(sessionAccount.id.toSessionId))
    val result = Await.result(groupsDAO.updateAccountCount(groupId1, 1L))
    assert(result == true)

  }

  test("find") {

    val sessionAccount = this.createAccount(0L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.follows,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.follows,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId8 = Await.result(groupsDAO.create(Some("New Group Name8"), false, GroupPrivacyType.friends,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))

    val group1 = Await.result(groupsDAO.find(groupId1, sessionAccount.id.toSessionId)).head
    val group2 = Await.result(groupsDAO.find(groupId2, sessionAccount.id.toSessionId)).head
    val group3 = Await.result(groupsDAO.find(groupId3, sessionAccount.id.toSessionId)).head
    val group4 = Await.result(groupsDAO.find(groupId4, sessionAccount.id.toSessionId)).head
    val group5 = Await.result(groupsDAO.find(groupId5, sessionAccount.id.toSessionId)).head
    val group6 = Await.result(groupsDAO.find(groupId6, sessionAccount.id.toSessionId)).head
    val group7 = Await.result(groupsDAO.find(groupId7, sessionAccount.id.toSessionId)).head
    val group8 = Await.result(groupsDAO.find(groupId8, sessionAccount.id.toSessionId)).head

    assert((group1.id, group1.name, group1.byInvitationOnly, group1.privacyType, group1.authorityType) == (groupId1, Some("New Group Name1"),  true, GroupPrivacyType.everyone.toValue,       GroupAuthorityType.member.toValue))
    assert((group2.id, group2.name, group2.byInvitationOnly, group2.privacyType, group2.authorityType) == (groupId2, Some("New Group Name2"),  true, GroupPrivacyType.followers.toValue,  GroupAuthorityType.member.toValue))
    assert((group3.id, group3.name, group3.byInvitationOnly, group3.privacyType, group3.authorityType) == (groupId3, Some("New Group Name3"),  true, GroupPrivacyType.follows.toValue,    GroupAuthorityType.member.toValue))
    assert((group4.id, group4.name, group4.byInvitationOnly, group4.privacyType, group4.authorityType) == (groupId4, Some("New Group Name4"),  true, GroupPrivacyType.friends.toValue,    GroupAuthorityType.member.toValue))
    assert((group5.id, group5.name, group5.byInvitationOnly, group5.privacyType, group5.authorityType) == (groupId5, Some("New Group Name5"), false, GroupPrivacyType.everyone.toValue,       GroupAuthorityType.owner.toValue))
    assert((group6.id, group6.name, group6.byInvitationOnly, group6.privacyType, group6.authorityType) == (groupId6, Some("New Group Name6"), false, GroupPrivacyType.followers.toValue,  GroupAuthorityType.owner.toValue))
    assert((group7.id, group7.name, group7.byInvitationOnly, group7.privacyType, group7.authorityType) == (groupId7, Some("New Group Name7"), false, GroupPrivacyType.follows.toValue,    GroupAuthorityType.owner.toValue))
    assert((group8.id, group8.name, group8.byInvitationOnly, group8.privacyType, group8.authorityType) == (groupId8, Some("New Group Name8"), false, GroupPrivacyType.friends.toValue,    GroupAuthorityType.owner.toValue))

  }

  test("find2") {

    val sessionAccount = this.createAccount(0L)
    val groupId = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    assert(Await.result(groupsDAO.find(groupId)).isDefined == true)

  }

  test("findAll") {

    val sessionAccount = this.createAccount(0L)
    val groupOwner = this.createAccount(1L)
    val blockedUser = this.createAccount(2L)

    Await.result(blocksDAO.create(blockedUser.id, sessionAccount.id.toSessionId))

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, groupOwner.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, groupOwner.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.follows,   GroupAuthorityType.member, 0L, groupOwner.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, groupOwner.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, blockedUser.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, groupOwner.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.follows,   GroupAuthorityType.owner,  0L, groupOwner.id.toSessionId))
    val groupId8 = Await.result(groupsDAO.create(Some("New Group Name8"), false, GroupPrivacyType.friends,   GroupAuthorityType.owner,  0L, groupOwner.id.toSessionId))

    val result1 = Await.result(groupsDAO.findAll(None, None, None, None, None, Some(4), sessionAccount.id.toSessionId))
    assert(result1.size == 4)
    val group1 = result1(0)
    val group2 = result1(1)
    val group3 = result1(2)
    val group4 = result1(3)
    assert((group1.id, group1.name, group1.byInvitationOnly, group1.privacyType, group1.authorityType) == (groupId8, Some("New Group Name8"), false, GroupPrivacyType.friends.toValue,    GroupAuthorityType.owner.toValue))
    assert((group2.id, group2.name, group2.byInvitationOnly, group2.privacyType, group2.authorityType) == (groupId7, Some("New Group Name7"), false, GroupPrivacyType.follows.toValue,    GroupAuthorityType.owner.toValue))
    assert((group3.id, group3.name, group3.byInvitationOnly, group3.privacyType, group3.authorityType) == (groupId6, Some("New Group Name6"), false, GroupPrivacyType.followers.toValue,  GroupAuthorityType.owner.toValue))
    assert((group4.id, group4.name, group4.byInvitationOnly, group4.privacyType, group4.authorityType) == (groupId4, Some("New Group Name4"),  true, GroupPrivacyType.friends.toValue,    GroupAuthorityType.member.toValue))

    val result2 = Await.result(groupsDAO.findAll(None, None, None, Some(group4.organizedAt), None, Some(4), sessionAccount.id.toSessionId))
    assert(result2.size == 3)
    val group5 = result2(0)
    val group6 = result2(1)
    val group7 = result2(2)
    assert((group5.id, group5.name, group5.byInvitationOnly, group5.privacyType, group5.authorityType) == (groupId3, Some("New Group Name3"),  true, GroupPrivacyType.follows.toValue,    GroupAuthorityType.member.toValue))
    assert((group6.id, group6.name, group6.byInvitationOnly, group6.privacyType, group6.authorityType) == (groupId2, Some("New Group Name2"),  true, GroupPrivacyType.followers.toValue,  GroupAuthorityType.member.toValue))
    assert((group7.id, group7.name, group7.byInvitationOnly, group7.privacyType, group7.authorityType) == (groupId1, Some("New Group Name1"),  true, GroupPrivacyType.everyone.toValue,       GroupAuthorityType.member.toValue))

  }


  test("update") {

    val sessionAccount = this.createAccount(0L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))

    val result1 = Await.result(groupsDAO.update(groupId1, Some("New Group Name11"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  sessionAccount.id.toSessionId))
    val result2 = Await.result(groupsDAO.update(groupId2, Some("New Group Name21"),  true, GroupPrivacyType.follows,   GroupAuthorityType.member, sessionAccount.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

    val group1 = Await.result(groupsDAO.find(groupId1, sessionAccount.id.toSessionId)).head
    val group2 = Await.result(groupsDAO.find(groupId2, sessionAccount.id.toSessionId)).head

    assert((group1.id, group1.name, group1.byInvitationOnly, group1.privacyType, group1.authorityType) == (groupId1, Some("New Group Name11"),  false, GroupPrivacyType.followers.toValue, GroupAuthorityType.owner.toValue))
    assert((group2.id, group2.name, group2.byInvitationOnly, group2.privacyType, group2.authorityType) == (groupId2, Some("New Group Name21"),  true,  GroupPrivacyType.follows.toValue,   GroupAuthorityType.member.toValue))

  }

  test("updateLatestMessage") {

    val sessionAccount = this.createAccount(0L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val result1 = Await.result(groupsDAO.update(groupId1, None, sessionAccount.id.toSessionId))
    assert(result1 == true)

  }

  test("exist") {

    val sessionAccount = this.createAccount(0L)

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.follows,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.follows,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId8 = Await.result(groupsDAO.create(Some("New Group Name8"), false, GroupPrivacyType.friends,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))

    assert(Await.result(groupsDAO.exist(groupId1)) == true)
    assert(Await.result(groupsDAO.exist(groupId2)) == true)
    assert(Await.result(groupsDAO.exist(groupId3)) == true)
    assert(Await.result(groupsDAO.exist(groupId4)) == true)
    assert(Await.result(groupsDAO.exist(groupId5)) == true)
    assert(Await.result(groupsDAO.exist(groupId6)) == true)
    assert(Await.result(groupsDAO.exist(groupId7)) == true)
    assert(Await.result(groupsDAO.exist(groupId8)) == true)

  }

}
