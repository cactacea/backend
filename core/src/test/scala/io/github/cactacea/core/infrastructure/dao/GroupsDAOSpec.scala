package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec

class GroupsDAOSpec extends DAOSpec {

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val blocksDAO: BlocksDAO = injector.instance[BlocksDAO]

  test("create") {

    val sessionAccount = createAccount("GroupsDAOSpec5")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.following,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.following,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
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

    val sessionAccount = createAccount("GroupsDAOSpec6")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.following,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.following,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
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

    val sessionAccount = createAccount("GroupsDAOSpec7")

    val groupId1 = Await.result(groupsDAO.create(sessionAccount.id.toSessionId))
    assert(Await.result(groupsDAO.exist(groupId1)) == true)


  }

  test("update Account count") {

    val sessionAccount = createAccount("GroupsDAOSpec8")

    val groupId1 = Await.result(groupsDAO.create(sessionAccount.id.toSessionId))
    val result = Await.result(groupsDAO.updateAccountCount(groupId1, 1L))
    assert(result == true)

  }

  test("find") {

    val sessionAccount = createAccount("GroupsDAOSpec9")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.following,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.following,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId8 = Await.result(groupsDAO.create(Some("New Group Name8"), false, GroupPrivacyType.friends,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))

    val group1 = Await.result(groupsDAO.find(groupId1, sessionAccount.id.toSessionId)).head
    val group2 = Await.result(groupsDAO.find(groupId2, sessionAccount.id.toSessionId)).head
    val group3 = Await.result(groupsDAO.find(groupId3, sessionAccount.id.toSessionId)).head
    val group4 = Await.result(groupsDAO.find(groupId4, sessionAccount.id.toSessionId)).head
    val group5 = Await.result(groupsDAO.find(groupId5, sessionAccount.id.toSessionId)).head
    val group6 = Await.result(groupsDAO.find(groupId6, sessionAccount.id.toSessionId)).head
    val group7 = Await.result(groupsDAO.find(groupId7, sessionAccount.id.toSessionId)).head
    val group8 = Await.result(groupsDAO.find(groupId8, sessionAccount.id.toSessionId)).head

    assert((group1.id, group1.name, group1.invitationOnly, group1.privacyType, group1.authorityType) == (groupId1, Some("New Group Name1"),  true, GroupPrivacyType.everyone,       GroupAuthorityType.member))
    assert((group2.id, group2.name, group2.invitationOnly, group2.privacyType, group2.authorityType) == (groupId2, Some("New Group Name2"),  true, GroupPrivacyType.followers,  GroupAuthorityType.member))
    assert((group3.id, group3.name, group3.invitationOnly, group3.privacyType, group3.authorityType) == (groupId3, Some("New Group Name3"),  true, GroupPrivacyType.following,    GroupAuthorityType.member))
    assert((group4.id, group4.name, group4.invitationOnly, group4.privacyType, group4.authorityType) == (groupId4, Some("New Group Name4"),  true, GroupPrivacyType.friends,    GroupAuthorityType.member))
    assert((group5.id, group5.name, group5.invitationOnly, group5.privacyType, group5.authorityType) == (groupId5, Some("New Group Name5"), false, GroupPrivacyType.everyone,       GroupAuthorityType.owner))
    assert((group6.id, group6.name, group6.invitationOnly, group6.privacyType, group6.authorityType) == (groupId6, Some("New Group Name6"), false, GroupPrivacyType.followers,  GroupAuthorityType.owner))
    assert((group7.id, group7.name, group7.invitationOnly, group7.privacyType, group7.authorityType) == (groupId7, Some("New Group Name7"), false, GroupPrivacyType.following,    GroupAuthorityType.owner))
    assert((group8.id, group8.name, group8.invitationOnly, group8.privacyType, group8.authorityType) == (groupId8, Some("New Group Name8"), false, GroupPrivacyType.friends,    GroupAuthorityType.owner))

  }

  test("find2") {

    val sessionAccount = createAccount("GroupsDAOSpec10")
    val groupId = Await.result(groupsDAO.create(Some("New Group Name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    assert(Await.result(groupsDAO.find(groupId)).isDefined == true)

  }

  test("findAll") {

    val sessionAccount = createAccount("GroupsDAOSpec11")
    val groupOwner = createAccount("GroupsDAOSpec12")
    val blockedUser = createAccount("GroupsDAOSpec13")

    Await.result(blocksDAO.create(blockedUser.id, sessionAccount.id.toSessionId))

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, groupOwner.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, groupOwner.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.following,   GroupAuthorityType.member, 0L, groupOwner.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, groupOwner.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, blockedUser.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, groupOwner.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.following,   GroupAuthorityType.owner,  0L, groupOwner.id.toSessionId))
    val groupId8 = Await.result(groupsDAO.create(Some("New Group Name8"), false, GroupPrivacyType.friends,   GroupAuthorityType.owner,  0L, groupOwner.id.toSessionId))

    val result1 = Await.result(groupsDAO.findAll(None, None, None, None, None, Some(4), sessionAccount.id.toSessionId))
    assert(result1.size == 4)
    val group1 = result1(0)
    val group2 = result1(1)
    val group3 = result1(2)
    val group4 = result1(3)
    assert((group1.id, group1.name, group1.invitationOnly, group1.privacyType, group1.authorityType) == (groupId8, Some("New Group Name8"), false, GroupPrivacyType.friends,    GroupAuthorityType.owner))
    assert((group2.id, group2.name, group2.invitationOnly, group2.privacyType, group2.authorityType) == (groupId7, Some("New Group Name7"), false, GroupPrivacyType.following,    GroupAuthorityType.owner))
    assert((group3.id, group3.name, group3.invitationOnly, group3.privacyType, group3.authorityType) == (groupId6, Some("New Group Name6"), false, GroupPrivacyType.followers,  GroupAuthorityType.owner))
    assert((group4.id, group4.name, group4.invitationOnly, group4.privacyType, group4.authorityType) == (groupId4, Some("New Group Name4"),  true, GroupPrivacyType.friends,    GroupAuthorityType.member))

    val result2 = Await.result(groupsDAO.findAll(None, None, None, Some(group4.id.value), None, Some(3), sessionAccount.id.toSessionId))
    assert(result2.size == 3)
    val group5 = result2(0)
    val group6 = result2(1)
    val group7 = result2(2)
    assert((group5.id, group5.name, group5.invitationOnly, group5.privacyType, group5.authorityType) == (groupId3, Some("New Group Name3"),  true, GroupPrivacyType.following,    GroupAuthorityType.member))
    assert((group6.id, group6.name, group6.invitationOnly, group6.privacyType, group6.authorityType) == (groupId2, Some("New Group Name2"),  true, GroupPrivacyType.followers,  GroupAuthorityType.member))
    assert((group7.id, group7.name, group7.invitationOnly, group7.privacyType, group7.authorityType) == (groupId1, Some("New Group Name1"),  true, GroupPrivacyType.everyone,       GroupAuthorityType.member))

  }


  test("update") {

    val sessionAccount = createAccount("GroupsDAOSpec14")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))

    Await.result(groupsDAO.update(groupId1, Some("New Group Name11"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  sessionAccount.id.toSessionId))
    Await.result(groupsDAO.update(groupId2, Some("New Group Name21"),  true, GroupPrivacyType.following,   GroupAuthorityType.member, sessionAccount.id.toSessionId))

    val group1 = Await.result(groupsDAO.find(groupId1, sessionAccount.id.toSessionId)).head
    val group2 = Await.result(groupsDAO.find(groupId2, sessionAccount.id.toSessionId)).head

    assert((group1.id, group1.name, group1.invitationOnly, group1.privacyType, group1.authorityType) == (groupId1, Some("New Group Name11"),  false, GroupPrivacyType.followers, GroupAuthorityType.owner))
    assert((group2.id, group2.name, group2.invitationOnly, group2.privacyType, group2.authorityType) == (groupId2, Some("New Group Name21"),  true,  GroupPrivacyType.following,   GroupAuthorityType.member))

  }

  test("updateLatestMessage") {

    val sessionAccount = createAccount("GroupsDAOSpec15")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(groupsDAO.update(groupId1, None, sessionAccount.id.toSessionId))

  }

  test("exist") {

    val sessionAccount = createAccount("GroupsDAOSpec16")

    val groupId1 = Await.result(groupsDAO.create(Some("New Group Name1"), true,  GroupPrivacyType.everyone,      GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("New Group Name2"), true,  GroupPrivacyType.followers, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("New Group Name3"), true,  GroupPrivacyType.following,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("New Group Name4"), true,  GroupPrivacyType.friends,   GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId5 = Await.result(groupsDAO.create(Some("New Group Name5"), false, GroupPrivacyType.everyone,      GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId6 = Await.result(groupsDAO.create(Some("New Group Name6"), false, GroupPrivacyType.followers, GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
    val groupId7 = Await.result(groupsDAO.create(Some("New Group Name7"), false, GroupPrivacyType.following,   GroupAuthorityType.owner,  0L, sessionAccount.id.toSessionId))
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
