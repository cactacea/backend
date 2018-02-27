package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, ReportType}
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId}
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException

class GroupAccountsRepositorySpec extends RepositorySpec {

  var groupAccountsRepository = injector.instance[GroupAccountsRepository]
  var groupsRepository = injector.instance[GroupsRepository]
  var reportsRepository = injector.instance[ReportsRepository]

  test("find accounts") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user1 = signUp("user name 1", "user password", "user udid").account
    val user2 = signUp("user name 2", "user password", "user udid").account
    val user3 = signUp("user name 3", "user password", "user udid").account
    val user4 = signUp("user name 4", "user password", "user udid").account
    val user5 = signUp("user name 5", "user password", "user udid").account
    val user6 = signUp("user name 6", "user password", "user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user1.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user2.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user3.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user4.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user5.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user6.id.toSessionId))
    val result1 = Await.result(groupAccountsRepository.findAll(groupId, None, None, Some(3), sessionUser.id.toSessionId))
    assert(result1.size == 3)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.findAll(GroupId(0L), None, None, Some(3), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    val groupId2 = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.friends, GroupAuthorityType.member, user1.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.findAll(groupId2, None, None, Some(3), sessionUser.id.toSessionId))
    }.error == AuthorityNotFound)

  }

  test("join to a group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))

    Await.result(groupAccountsRepository.create(groupId, user.id.toSessionId))
    val group = Await.result(groupsRepository.find(groupId, user.id.toSessionId))
    assert(group.accountCount == 2)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.create(GroupId(0L), user.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.create(groupId, user.id.toSessionId))
    }.error == AccountAlreadyJoined)

    val groupId2 = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.create(groupId2, user.id.toSessionId))
    }.error == GroupIsInvitationOnly)

    val groupId3 = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.friends, GroupAuthorityType.member, user.id.toSessionId))
    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.create(groupId3, sessionUser.id.toSessionId))
    }.error == AuthorityNotFound)

  }

  test("join a account to a group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(user.id, groupId, sessionUser.id.toSessionId))

    val group = Await.result(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group.accountCount == 2)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.create(user.id, GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.create(AccountId(0L), groupId, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.create(user.id, groupId, sessionUser.id.toSessionId))
    }.error == AccountAlreadyJoined)

    val groupId2 = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.create(user.id, groupId2, sessionUser.id.toSessionId))
    }.error == GroupIsInvitationOnly)

    val groupId3 = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.friends, GroupAuthorityType.member, user.id.toSessionId))
    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.create(sessionUser.id, groupId3, user.id.toSessionId))
    }.error == OperationNotAllowed)
  }

  test("leave a group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId2 = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, user.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId, user.id.toSessionId))
    Await.result(reportsRepository.createGroupReport(groupId, ReportType.inappropriate, user.id.toSessionId))

    val group1 = Await.result(groupsRepository.find(groupId, user.id.toSessionId))
    assert(group1.accountCount == 2)

    Await.result(groupAccountsRepository.delete(groupId, user.id.toSessionId))

    val group2 = Await.result(groupsRepository.find(groupId, user.id.toSessionId))
    assert(group2.accountCount == 1)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.delete(groupId2, sessionUser.id.toSessionId))
    }.error == AccountNotJoined)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.delete(GroupId(0L), user.id.toSessionId))
    }.error == GroupNotFound)

    Await.result(groupAccountsRepository.delete(groupId, sessionUser.id.toSessionId))
    // TODO : Check

  }

  test("leave a account from a group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account
    val user1 = signUp("user name 1", "user password", "user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(user.id, groupId, sessionUser.id.toSessionId))

    val group1 = Await.result(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group1.accountCount == 2)

    Await.result(groupAccountsRepository.delete(user.id, groupId, sessionUser.id.toSessionId))

    val group2 = Await.result(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group2.accountCount == 1)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.delete(user.id, GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.delete(AccountId(0L), groupId, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.delete(user1.id, groupId, sessionUser.id.toSessionId))
    }.error == AccountNotJoined)

    Await.result(groupAccountsRepository.create(user1.id, groupId, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.delete(groupId, sessionUser.id.toSessionId))
    val group3 = Await.result(groupsRepository.find(groupId, sessionUser.id.toSessionId))
    assert(group3.accountCount == 1)

    Await.result(groupAccountsRepository.delete(user1.id, groupId, sessionUser.id.toSessionId))
    // TODO : Check

    val groupId2 = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.owner, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(user.id, groupId2, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(user1.id, groupId2, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(groupAccountsRepository.delete(user1.id, groupId2, user.id.toSessionId))
    }.error == AuthorityNotFound)

  }



}
