package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, ReportType}
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class GroupAccountsRepositorySpec extends RepositorySpec {

  val groupAccountsRepository = injector.instance[GroupAccountsRepository]
  val groupsRepository = injector.instance[GroupsRepository]
  val reportsRepository = injector.instance[ReportsRepository]

  test("find accounts") {

    val sessionUser = signUp("GroupAccountsRepositorySpec1", "session user password", "session user udid")
    val user1 = signUp("GroupAccountsRepositorySpec2", "user password", "user udid")
    val user2 = signUp("GroupAccountsRepositorySpec3", "user password", "user udid")
    val user3 = signUp("GroupAccountsRepositorySpec4", "user password", "user udid")
    val user4 = signUp("GroupAccountsRepositorySpec5", "user password", "user udid")
    val user5 = signUp("GroupAccountsRepositorySpec6", "user password", "user udid")
    val user6 = signUp("GroupAccountsRepositorySpec7", "user password", "user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(groupAccountsRepository.create(groupId, user1.id.toSessionId))
    execute(groupAccountsRepository.create(groupId, user2.id.toSessionId))
    execute(groupAccountsRepository.create(groupId, user3.id.toSessionId))
    execute(groupAccountsRepository.create(groupId, user4.id.toSessionId))
    execute(groupAccountsRepository.create(groupId, user5.id.toSessionId))
    execute(groupAccountsRepository.create(groupId, user6.id.toSessionId))
    val result1 = execute(groupAccountsRepository.find(groupId, None, 0, 3, sessionUser.id.toSessionId))
    assert(result1.size == 3)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.find(GroupId(0L), None, 0, 3, sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    val groupId2 = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.friends, GroupAuthorityType.member, user1.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.find(groupId2, None, 0, 3, sessionUser.id.toSessionId))
    }.error == AuthorityNotFound)

  }

  test("join to a group") {

    val sessionUser = signUp("GroupAccountsRepositorySpec8", "session user password", "session user udid")
    val user = signUp("GroupAccountsRepositorySpec9", "user password", "user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))

    execute(groupAccountsRepository.create(groupId, user.id.toSessionId))
    val group = execute(groupsRepository.find(groupId, user.id.toSessionId))
    assert(group.accountCount == 2)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.create(GroupId(0L), user.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.create(groupId, user.id.toSessionId))
    }.error == AccountAlreadyJoined)

    val groupId2 = execute(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.create(groupId2, user.id.toSessionId))
    }.error == InnvitationOnlyGroup)

    val groupId3 = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.friends, GroupAuthorityType.member, user.id.toSessionId))
    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.create(groupId3, sessionUser.id.toSessionId))
    }.error == AuthorityNotFound)

  }

  test("join a account to a group") {

    val sessionUser = signUp("GroupAccountsRepositorySpec10", "session user password", "session user udid")
    val user = signUp("GroupAccountsRepositorySpec11", "user password", "user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(groupAccountsRepository.create(user.id, groupId, sessionUser.id.toSessionId))

    val group = execute(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group.accountCount == 2)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.create(user.id, GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.create(AccountId(0L), groupId, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.create(user.id, groupId, sessionUser.id.toSessionId))
    }.error == AccountAlreadyJoined)

//    val groupId2 = execute(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
//    assert(intercept[CactaceaException] {
//      execute(groupAccountsRepository.create(user.id, groupId2, sessionUser.id.toSessionId))
//    }.error == InnvitationOnlyGroup)
//
//    val groupId3 = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.friends, GroupAuthorityType.member, user.id.toSessionId))
//    assert(intercept[CactaceaException] {
//      execute(groupAccountsRepository.create(sessionUser.id, groupId3, user.id.toSessionId))
//    }.error == OperationNotAllowed)
  }

  test("leave a group") {

    val sessionUser = signUp("GroupAccountsRepositorySpec12", "session user password", "session user udid")
    val user = signUp("GroupAccountsRepositorySpec13", "user password", "user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId2 = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, user.id.toSessionId))
    val reportContent = Some("report content")
    execute(groupAccountsRepository.create(groupId, user.id.toSessionId))
    execute(reportsRepository.createGroupReport(groupId, ReportType.inappropriate, reportContent, user.id.toSessionId))

    val group1 = execute(groupsRepository.find(groupId, user.id.toSessionId))
    assert(group1.accountCount == 2)

    execute(groupAccountsRepository.delete(groupId, user.id.toSessionId))

    val group2 = execute(groupsRepository.find(groupId, user.id.toSessionId))
    assert(group2.accountCount == 1)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.delete(groupId2, sessionUser.id.toSessionId))
    }.error == AccountNotJoined)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.delete(GroupId(0L), user.id.toSessionId))
    }.error == GroupNotFound)

    execute(groupAccountsRepository.delete(groupId, sessionUser.id.toSessionId))
    // TODO : Check

  }

  test("leave a account from a group") {

    val sessionUser = signUp("GroupAccountsRepositorySpec14", "session user password", "session user udid")
    val user = signUp("GroupAccountsRepositorySpec15", "user password", "user udid")
    val user1 = signUp("GroupAccountsRepositorySpec16", "user password", "user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(groupAccountsRepository.create(user.id, groupId, sessionUser.id.toSessionId))

    val group1 = execute(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group1.accountCount == 2)

    execute(groupAccountsRepository.delete(user.id, groupId, sessionUser.id.toSessionId))

    val group2 = execute(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group2.accountCount == 1)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.delete(user.id, GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.delete(AccountId(0L), groupId, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.delete(user1.id, groupId, sessionUser.id.toSessionId))
    }.error == AccountNotJoined)

    execute(groupAccountsRepository.create(user1.id, groupId, sessionUser.id.toSessionId))
    execute(groupAccountsRepository.delete(groupId, sessionUser.id.toSessionId))
    val group3 = execute(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group3.accountCount == 1)

    execute(groupAccountsRepository.delete(user1.id, groupId, sessionUser.id.toSessionId))
    // TODO : Check

    val groupId2 = execute(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.owner, sessionUser.id.toSessionId))
    execute(groupAccountsRepository.create(user.id, groupId2, sessionUser.id.toSessionId))
    execute(groupAccountsRepository.create(user1.id, groupId2, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(groupAccountsRepository.delete(user1.id, groupId2, user.id.toSessionId))
    }.error == AuthorityNotFound)

  }



}
