package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.infrastructure.dao.{AccountGroupsDAO, GroupAccountsDAO, GroupsDAO}
import io.github.cactacea.core.infrastructure.identifiers.GroupId
import io.github.cactacea.core.specs.RepositorySpec
import io.github.cactacea.core.util.responses.CactaceaError.{DirectMessageGroupCanNotUpdated, GroupNotFound}
import io.github.cactacea.core.util.exceptions.CactaceaException

class GroupsRepositorySpec extends RepositorySpec {

  val groupsRepository = injector.instance[GroupsRepository]
  val groupsDAO = injector.instance[GroupsDAO]
  val groupAccountsDAO = injector.instance[GroupAccountsDAO]
  val accountGroupsDAO = injector.instance[AccountGroupsDAO]
  val accountGroupsRepository = injector.instance[AccountGroupsRepository]

  test("create a group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    assert(Await.result(groupsDAO.exist(groupId)) == true)

  }

  test("update a group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val result = Await.result(groupsRepository.update(groupId, Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    // TODO : Check

  }

  test("update privacy type") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val result = Await.result(groupsRepository.update(groupId, Some("new group name"), false, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    // TODO : Check

  }

  test("update a direct message group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "session user password", "session user udid").account
    val group = Await.result(accountGroupsRepository.findOrCreate(user.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(groupsRepository.update(group.id, Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    }.error == DirectMessageGroupCanNotUpdated)

  }

  test("find a exist group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val group = Await.result(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group.id == groupId)

  }

  test("find no exist group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(groupsRepository.find(GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

  }

  test("update no exist group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account

    assert(intercept[CactaceaException] {
      Await.result(groupsRepository.update(GroupId(0L), Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    }.error == GroupNotFound)

  }

  test("find all groups") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user1 = signUp("user name 1", "user password 1", "user udid 1").account
    val user2 = signUp("user name 2", "user password 2", "user udid 2").account
    val user3 = signUp("user name 3", "user password 3", "user udid 3").account
    val user4 = signUp("user name 4", "user password 4", "user udid 4").account
    val user5 = signUp("user name 5", "user password 5", "user udid 5").account

    val groupId10 = Await.result(groupsRepository.create(Some("group name 10"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId11 = Await.result(groupsRepository.create(Some("group name 11"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId12 = Await.result(groupsRepository.create(Some("group name 12"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId13 = Await.result(groupsRepository.create(Some("group name 13"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId14 = Await.result(groupsRepository.create(Some("group name 14"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))

    Await.result(accountGroupsDAO.create(List(user1.id, user2.id, user3.id, user4.id, user5.id),groupId10))
    Await.result(accountGroupsDAO.create(List(user1.id, user2.id, user3.id, user4.id),groupId11))
    Await.result(accountGroupsDAO.create(List(user1.id, user2.id, user3.id),groupId12))
    Await.result(accountGroupsDAO.create(List(user1.id, user2.id),groupId13))
    Await.result(accountGroupsDAO.create(List(user1.id),groupId14))

    val groupId1 = Await.result(groupsRepository.create(Some("group name 1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId2 = Await.result(groupsRepository.create(Some("group name 2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId3 = Await.result(groupsRepository.create(Some("group name 3"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId4 = Await.result(groupsRepository.create(Some("group name 4"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId5 = Await.result(groupsRepository.create(Some("group name 5"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))

    Await.result(accountGroupsDAO.create(List(user1.id, user2.id, user3.id, user4.id, user5.id), groupId1))
    Await.result(accountGroupsDAO.create(List(user1.id, user2.id, user3.id, user4.id), groupId2))
    Await.result(accountGroupsDAO.create(List(user1.id, user2.id, user3.id), groupId3))
    Await.result(accountGroupsDAO.create(List(user1.id, user2.id), groupId4))
    Await.result(accountGroupsDAO.create(List(user1.id), groupId5))

    assert(Await.result(groupsRepository.findAll(None , None, None, None, None, Some(3), sessionUser.id.toSessionId)).size == 0)

    val result1 = Await.result(groupsRepository.findAll(None , None, None, None, None, Some(3), user1.id.toSessionId))
    assert(result1.size == 3)
    assert(result1(0).id == groupId5)
    assert(result1(1).id == groupId4)
    assert(result1(2).id == groupId3)

    val result2 = Await.result(groupsRepository.findAll(None , None, None, Some(result1(2).next), None, Some(3), user1.id.toSessionId))
    assert(result2.size == 3)
    assert(result2(0).id == groupId2)
    assert(result2(1).id == groupId1)

    val result3 = Await.result(groupsRepository.findAll(Some("group name 1") , None, None, None, None, Some(3), user1.id.toSessionId))
    assert(result3.size == 3)
    assert(result3(0).id == groupId1)
    assert(result3(1).id == groupId14)
    assert(result3(2).id == groupId13)

    val result4 = Await.result(groupsRepository.findAll(Some("group name 1") , None, None, Some(result3(2).next), None, Some(3), user1.id.toSessionId))
    assert(result4.size == 3)
    assert(result4(0).id == groupId12)
    assert(result4(1).id == groupId11)
  }

  test("find a group") {

  }

}
