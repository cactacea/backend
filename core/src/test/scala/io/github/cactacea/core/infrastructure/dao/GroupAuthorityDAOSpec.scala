package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.helpers.CactaceaDAOTest
import io.github.cactacea.core.util.responses.CactaceaError.AuthorityNotFound

class GroupAuthorityDAOSpec extends CactaceaDAOTest {

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val accountGroupsDAO: AccountGroupsDAO = injector.instance[AccountGroupsDAO]
  val groupAuthorityDAO: GroupAuthorityDAO = injector.instance[GroupAuthorityDAO]
  val followsDAO: FollowsDAO = injector.instance[FollowsDAO]
  val followersDAO: FollowersDAO = injector.instance[FollowersDAO]
  val friendsDAO: FriendsDAO = injector.instance[FriendsDAO]

  test("hasInviteAuthority") {

    val owner = this.createAccount(1L)
    val member = this.createAccount(2L)

    val groupId1 = Await.result(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.friends, GroupAuthorityType.member, 0L, owner.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.friends, GroupAuthorityType.member, 0L, owner.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("new group name3"), true, GroupPrivacyType.friends, GroupAuthorityType.owner, 0L, owner.id.toSessionId))
    val group1 = Await.result(groupsDAO.find(groupId1)).get
    val group2 = Await.result(groupsDAO.find(groupId2)).get
    val group3 = Await.result(groupsDAO.find(groupId3)).get
    Await.result(accountGroupsDAO.create(owner.id, groupId1))
    Await.result(accountGroupsDAO.create(owner.id, groupId2))
    Await.result(accountGroupsDAO.create(member.id, groupId1))
    Await.result(accountGroupsDAO.create(member.id, groupId3))

    // check by owner
    assert(Await.result(groupAuthorityDAO.hasManagingAuthority(group1, owner.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasManagingAuthority(group2, owner.id.toSessionId)) == Right(true))

    // check by member
    assert(Await.result(groupAuthorityDAO.hasManagingAuthority(group1, member.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasManagingAuthority(group3, member.id.toSessionId)) == Left(AuthorityNotFound))

  }

  test("hasJoinAuthority") {

    val owner = this.createAccount(1L)
    val user = this.createAccount(2L)
    val follower = this.createAccount(3L)
    val follow = this.createAccount(4L)
    val friend = this.createAccount(5L)

    // create follow
    Await.result(followersDAO.create(owner.id, follow.id.toSessionId))
    Await.result(followsDAO.create(follow.id, owner.id.toSessionId))

    // create follower
    Await.result(followersDAO.create(follower.id, owner.id.toSessionId))
    Await.result(followsDAO.create(owner.id, follower.id.toSessionId))

    // create friend
    Await.result(followsDAO.create(owner.id, friend.id.toSessionId))
    Await.result(followsDAO.create(friend.id, owner.id.toSessionId))
    Await.result(followersDAO.create(friend.id, owner.id.toSessionId))
    Await.result(followersDAO.create(owner.id, friend.id.toSessionId))
    Await.result(friendsDAO.create(friend.id, owner.id.toSessionId))
    Await.result(friendsDAO.create(owner.id, friend.id.toSessionId))

    val groupId1 = Await.result(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.followers, GroupAuthorityType.owner, 0L, owner.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.follows, GroupAuthorityType.owner, 0L, owner.id.toSessionId))
    val groupId3 = Await.result(groupsDAO.create(Some("new group name3"), true, GroupPrivacyType.friends, GroupAuthorityType.owner, 0L, owner.id.toSessionId))
    val groupId4 = Await.result(groupsDAO.create(Some("new group name4"), true, GroupPrivacyType.everyone, GroupAuthorityType.owner, 0L, owner.id.toSessionId))
    val group1 = Await.result(groupsDAO.find(groupId1)).get
    val group2 = Await.result(groupsDAO.find(groupId2)).get
    val group3 = Await.result(groupsDAO.find(groupId3)).get
    val group4 = Await.result(groupsDAO.find(groupId4)).get
    Await.result(accountGroupsDAO.create(owner.id, groupId1))
    Await.result(accountGroupsDAO.create(owner.id, groupId2))
    Await.result(accountGroupsDAO.create(owner.id, groupId3))
    Await.result(accountGroupsDAO.create(owner.id, groupId4))

    // check by user
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group1, user.id.toSessionId)) == Left(AuthorityNotFound))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group2, user.id.toSessionId)) == Left(AuthorityNotFound))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group3, user.id.toSessionId)) == Left(AuthorityNotFound))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group4, user.id.toSessionId)) == Right(true))

    // check by owner
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group1, owner.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group2, owner.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group3, owner.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group4, owner.id.toSessionId)) == Right(true))

    // check by friend
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group1, friend.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group2, friend.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group3, friend.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group4, friend.id.toSessionId)) == Right(true))

    // check by follow
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group1, follow.id.toSessionId)) == Left(AuthorityNotFound))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group2, follow.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group3, follow.id.toSessionId)) == Left(AuthorityNotFound))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group4, follow.id.toSessionId)) == Right(true))

    // check by follower
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group1, follower.id.toSessionId)) == Right(true))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group2, follower.id.toSessionId)) == Left(AuthorityNotFound))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group3, follower.id.toSessionId)) == Left(AuthorityNotFound))
    assert(Await.result(groupAuthorityDAO.hasJoinAuthority(group4, follower.id.toSessionId)) == Right(true))

  }


}
