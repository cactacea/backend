package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec

class GroupAuthorityValidatorSpec extends DAOSpec {


  test("hasInviteAuthority") {

    val owner = createAccount("GroupAuthorityDAOSpec1")
    val member = createAccount("GroupAuthorityDAOSpec2")

    val groupId1 = execute(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.friends, GroupAuthorityType.member, owner.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.friends, GroupAuthorityType.member, owner.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("new group name3"), true, GroupPrivacyType.friends, GroupAuthorityType.owner, owner.id.toSessionId))
//    val group1 = execute(helperDAO.selectGroup(groupId1)).get
//    val group2 = execute(helperDAO.selectGroup(groupId2)).get
//    val group3 = execute(helperDAO.selectGroup(groupId3)).get
    execute(helperDAO.selectGroup(groupId1)).get
    execute(helperDAO.selectGroup(groupId2)).get
    execute(helperDAO.selectGroup(groupId3)).get
    execute(accountGroupsDAO.create(owner.id, groupId1))
    execute(accountGroupsDAO.create(owner.id, groupId2))
    execute(accountGroupsDAO.create(member.id, groupId1))
    execute(accountGroupsDAO.create(member.id, groupId3))

//    // check by owner
//    assert(execute(groupAuthorityDAO._hasManagingAuthority(group1, owner.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasManagingAuthority(group2, owner.id.toSessionId)) == Right(true))
//
//    // check by member
//    assert(execute(groupAuthorityDAO._hasManagingAuthority(group1, member.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasManagingAuthority(group3, member.id.toSessionId)) == Left(AuthorityNotFound))

  }

  test("hasJoinAuthority") {

    val owner = createAccount("GroupAuthorityDAOSpec3")
    val user = createAccount("GroupAuthorityDAOSpec4")
    val follower = createAccount("GroupAuthorityDAOSpec5")
    val follow = createAccount("GroupAuthorityDAOSpec6")
    val friend = createAccount("GroupAuthorityDAOSpec7")

    // create follows
    execute(followersDAO.create(owner.id, follow.id.toSessionId))
    execute(followsDAO.create(follow.id, owner.id.toSessionId))

    // create follower
    execute(followersDAO.create(follower.id, owner.id.toSessionId))
    execute(followsDAO.create(owner.id, follower.id.toSessionId))

    // create friend
    execute(followsDAO.create(owner.id, friend.id.toSessionId))
    execute(followsDAO.create(friend.id, owner.id.toSessionId))
    execute(followersDAO.create(friend.id, owner.id.toSessionId))
    execute(followersDAO.create(owner.id, friend.id.toSessionId))
    execute(friendsDAO.create(friend.id, owner.id.toSessionId))
    execute(friendsDAO.create(owner.id, friend.id.toSessionId))

    val groupId1 = execute(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.followers, GroupAuthorityType.owner, owner.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.follows, GroupAuthorityType.owner, owner.id.toSessionId))
    val groupId3 = execute(groupsDAO.create(Some("new group name3"), true, GroupPrivacyType.friends, GroupAuthorityType.owner, owner.id.toSessionId))
    val groupId4 = execute(groupsDAO.create(Some("new group name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner.id.toSessionId))
//    val group1 = execute(helperDAO.selectGroup(groupId1)).get
//    val group2 = execute(helperDAO.selectGroup(groupId2)).get
//    val group3 = execute(helperDAO.selectGroup(groupId3)).get
//    val group4 = execute(helperDAO.selectGroup(groupId4)).get
    execute(accountGroupsDAO.create(owner.id, groupId1))
    execute(accountGroupsDAO.create(owner.id, groupId2))
    execute(accountGroupsDAO.create(owner.id, groupId3))
    execute(accountGroupsDAO.create(owner.id, groupId4))

//    // check by user
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group1, user.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group2, user.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group3, user.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group4, user.id.toSessionId)) == Right(true))
//
//    // check by owner
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group1, owner.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group2, owner.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group3, owner.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group4, owner.id.toSessionId)) == Right(true))
//
//    // check by friend
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group1, friend.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group2, friend.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group3, friend.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group4, friend.id.toSessionId)) == Right(true))
//
//    // check by follows
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group1, follow.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group2, follow.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group3, follow.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group4, follow.id.toSessionId)) == Right(true))
//
//    // check by follower
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group1, follower.id.toSessionId)) == Right(true))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group2, follower.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group3, follower.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(execute(groupAuthorityDAO._hasJoinAuthority(group4, follower.id.toSessionId)) == Right(true))

  }


}
