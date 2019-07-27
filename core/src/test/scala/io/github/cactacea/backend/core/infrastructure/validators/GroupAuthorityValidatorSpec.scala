package io.github.cactacea.backend.core.infrastructure.validators

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class GroupAuthorityValidatorSpec extends DAOSpec {


  feature("hasInviteAuthority") (pending)

//  {
//
//    val owner = createAccount("GroupAuthorityDAOSpec1")
//    val member = createAccount("GroupAuthorityDAOSpec2")
//
//    val groupId1 = await(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.friends, GroupAuthorityType.member, owner.id.toSessionId))
//    val groupId2 = await(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.friends, GroupAuthorityType.member, owner.id.toSessionId))
//    val groupId3 = await(groupsDAO.create(Some("new group name3"), true, GroupPrivacyType.friends, GroupAuthorityType.owner, owner.id.toSessionId))
////    val group1 = await(helperDAO.selectGroup(groupId1)).get
////    val group2 = await(helperDAO.selectGroup(groupId2)).get
////    val group3 = await(helperDAO.selectGroup(groupId3)).get
//    await(helperDAO.selectGroup(groupId1)).get
//    await(helperDAO.selectGroup(groupId2)).get
//    await(helperDAO.selectGroup(groupId3)).get
//    await(accountGroupsDAO.create(owner.id, groupId1))
//    await(accountGroupsDAO.create(owner.id, groupId2))
//    await(accountGroupsDAO.create(member.id, groupId1))
//    await(accountGroupsDAO.create(member.id, groupId3))

//    // check by owner
//    assert(await(groupAuthorityDAO._hasManagingAuthority(group1, owner.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasManagingAuthority(group2, owner.id.toSessionId)) == Right(true))
//
//    // check by member
//    assert(await(groupAuthorityDAO._hasManagingAuthority(group1, member.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasManagingAuthority(group3, member.id.toSessionId)) == Left(AuthorityNotFound))
//
//  }

  feature("hasJoinAuthority") (pending)
//  {
//
//    val owner = createAccount("GroupAuthorityDAOSpec3")
//    val user = createAccount("GroupAuthorityDAOSpec4")
//    val follower = createAccount("GroupAuthorityDAOSpec5")
//    val follow = createAccount("GroupAuthorityDAOSpec6")
//    val friend = createAccount("GroupAuthorityDAOSpec7")
//
//    // create follows
//    await(followersDAO.create(owner.id, follow.id.toSessionId))
//    await(followsDAO.create(follow.id, owner.id.toSessionId))
//
//    // create follower
//    await(followersDAO.create(follower.id, owner.id.toSessionId))
//    await(followsDAO.create(owner.id, follower.id.toSessionId))
//
//    // create friend
//    await(followsDAO.create(owner.id, friend.id.toSessionId))
//    await(followsDAO.create(friend.id, owner.id.toSessionId))
//    await(followersDAO.create(friend.id, owner.id.toSessionId))
//    await(followersDAO.create(owner.id, friend.id.toSessionId))
//    await(friendsDAO.create(friend.id, owner.id.toSessionId))
//    await(friendsDAO.create(owner.id, friend.id.toSessionId))
//
//    val groupId1 = await(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.followers, GroupAuthorityType.owner, owner.id.toSessionId))
//    val groupId2 = await(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.follows, GroupAuthorityType.owner, owner.id.toSessionId))
//    val groupId3 = await(groupsDAO.create(Some("new group name3"), true, GroupPrivacyType.friends, GroupAuthorityType.owner, owner.id.toSessionId))
//    val groupId4 = await(groupsDAO.create(Some("new group name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, owner.id.toSessionId))
////    val group1 = await(helperDAO.selectGroup(groupId1)).get
////    val group2 = await(helperDAO.selectGroup(groupId2)).get
////    val group3 = await(helperDAO.selectGroup(groupId3)).get
////    val group4 = await(helperDAO.selectGroup(groupId4)).get
//    await(accountGroupsDAO.create(owner.id, groupId1))
//    await(accountGroupsDAO.create(owner.id, groupId2))
//    await(accountGroupsDAO.create(owner.id, groupId3))
//    await(accountGroupsDAO.create(owner.id, groupId4))

//    // check by user
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group1, user.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group2, user.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group3, user.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group4, user.id.toSessionId)) == Right(true))
//
//    // check by owner
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group1, owner.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group2, owner.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group3, owner.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group4, owner.id.toSessionId)) == Right(true))
//
//    // check by friend
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group1, friend.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group2, friend.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group3, friend.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group4, friend.id.toSessionId)) == Right(true))
//
//    // check by follows
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group1, follow.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group2, follow.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group3, follow.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group4, follow.id.toSessionId)) == Right(true))
//
//    // check by follower
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group1, follower.id.toSessionId)) == Right(true))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group2, follower.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group3, follower.id.toSessionId)) == Left(AuthorityNotFound))
//    assert(await(groupAuthorityDAO._hasJoinAuthority(group4, follower.id.toSessionId)) == Right(true))
//
//  }


}
