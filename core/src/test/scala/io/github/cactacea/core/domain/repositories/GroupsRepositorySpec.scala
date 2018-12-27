package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao.{AccountGroupsDAO, GroupAccountsDAO, GroupsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{DirectMessageGroupCanNotUpdated, GroupNotFound}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class GroupsRepositorySpec extends RepositorySpec {

  val groupsRepository = injector.instance[GroupsRepository]
  val groupsDAO = injector.instance[GroupsDAO]
  val groupAccountsDAO = injector.instance[GroupAccountsDAO]
  val accountGroupsDAO = injector.instance[AccountGroupsDAO]
  val accountGroupsRepository = injector.instance[AccountGroupsRepository]

  test("create a group") {

    val sessionUser = signUp("GroupsRepositorySpec1", "session user password", "session user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    assert(execute(groupsDAO.exist(groupId)) == true)

  }

  test("update a group") {

    val sessionUser = signUp("GroupsRepositorySpec2", "session user password", "session user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(groupsRepository.update(groupId, Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    // TODO : Check

  }

  test("update privacy type") {

    val sessionUser = signUp("GroupsRepositorySpec3", "session user password", "session user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(groupsRepository.update(groupId, Some("new group name"), false, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    // TODO : Check

  }

  test("update a direct message group") {

    val sessionUser = signUp("GroupsRepositorySpec4", "session user password", "session user udid")
    val user = signUp("GroupsRepositorySpec5", "session user password", "session user udid")
    val group = execute(accountGroupsRepository.findOrCreate(user.id, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(groupsRepository.update(group.id, Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    }.error == DirectMessageGroupCanNotUpdated)

  }

  test("find a exist group") {

    val sessionUser = signUp("GroupsRepositorySpec6", "session user password", "session user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val group = execute(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group.id == groupId)

  }

  test("find no exist group") {

    val sessionUser = signUp("GroupsRepositorySpec7", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      execute(groupsRepository.find(GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

  }

  test("update no exist group") {

    val sessionUser = signUp("GroupsRepositorySpec8", "session user password", "session user udid")

    assert(intercept[CactaceaException] {
      execute(groupsRepository.update(GroupId(0L), Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    }.error == GroupNotFound)

  }

  test("find all groups") {

    val sessionUser = signUp("GroupsRepositorySpec9", "session user password", "session user udid")
    val user1 = signUp("GroupsRepositorySpec10", "user password 1", "user udid 1")
    val user2 = signUp("GroupsRepositorySpec11", "user password 2", "user udid 2")
    val user3 = signUp("GroupsRepositorySpec12", "user password 3", "user udid 3")
    val user4 = signUp("GroupsRepositorySpec13", "user password 4", "user udid 4")
    val user5 = signUp("GroupsRepositorySpec14", "user password 5", "user udid 5")

    val groupId10 = execute(groupsRepository.create(Some("group name 10"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId11 = execute(groupsRepository.create(Some("group name 11"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId12 = execute(groupsRepository.create(Some("group name 12"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId13 = execute(groupsRepository.create(Some("group name 13"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId14 = execute(groupsRepository.create(Some("group name 14"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))

    execute(accountGroupsDAO.create(List(user1.id, user2.id, user3.id, user4.id, user5.id),groupId10))
    execute(accountGroupsDAO.create(List(user1.id, user2.id, user3.id, user4.id),groupId11))
    execute(accountGroupsDAO.create(List(user1.id, user2.id, user3.id),groupId12))
    execute(accountGroupsDAO.create(List(user1.id, user2.id),groupId13))
    execute(accountGroupsDAO.create(List(user1.id),groupId14))

    val groupId1 = execute(groupsRepository.create(Some("group name 1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId2 = execute(groupsRepository.create(Some("group name 2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId3 = execute(groupsRepository.create(Some("group name 3"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId4 = execute(groupsRepository.create(Some("group name 4"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId5 = execute(groupsRepository.create(Some("group name 5"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))

    execute(accountGroupsDAO.create(List(user1.id, user2.id, user3.id, user4.id, user5.id), groupId1))
    execute(accountGroupsDAO.create(List(user1.id, user2.id, user3.id, user4.id), groupId2))
    execute(accountGroupsDAO.create(List(user1.id, user2.id, user3.id), groupId3))
    execute(accountGroupsDAO.create(List(user1.id, user2.id), groupId4))
    execute(accountGroupsDAO.create(List(user1.id), groupId5))

    assert(execute(groupsRepository.find(None , None, None, None, 0, 10, sessionUser.id.toSessionId)).size == 10)

    val result1 = execute(groupsRepository.find(None , None, None, None, 0, 10, user1.id.toSessionId))
    assert(result1.size == 10)
    assert(result1(0).id == groupId5)
    assert(result1(1).id == groupId4)
    assert(result1(2).id == groupId3)

    val result2 = execute(groupsRepository.find(None , None, None, result1(2).next, 0, 2, user1.id.toSessionId))
    assert(result2.size == 2)
    assert(result2(0).id == groupId2)
    assert(result2(1).id == groupId1)

    val result3 = execute(groupsRepository.find(Some("group name 1") , None, None, None, 0, 3, user1.id.toSessionId))
    assert(result3.size == 3)
    assert(result3(0).id == groupId1)
    assert(result3(1).id == groupId14)
    assert(result3(2).id == groupId13)

    val result4 = execute(groupsRepository.find(Some("group name 1") , None, None, result3(2).next, 0, 3, user1.id.toSessionId))
    assert(result4.size == 3)
    assert(result4(0).id == groupId12)
    assert(result4(1).id == groupId11)
  }

  test("find a group") {

  }

}
