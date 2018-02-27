package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupInviteStatusType, GroupPrivacyType}
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.dao.{GroupAccountsDAO, GroupInvitesDAO, GroupsDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, GroupInviteId}
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException

class GroupInvitesRepositorySpec extends RepositorySpec {

  val groupInvitesRepository = injector.instance[GroupInvitesRepository]
  val groupsRepository = injector.instance[GroupsRepository]
  val groupAccountsRepository = injector.instance[GroupAccountsRepository]
  val groupInviteDAO = injector.instance[GroupInvitesDAO]
  val groupAccountsDAO = injector.instance[GroupAccountsDAO]
  val groupsDAO = injector.instance[GroupsDAO]

  test("invite accounts to a groups") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user1 = signUp("user name 1", "user password 1", "user udid 1").account
    val user2 = signUp("user name 2", "user password 2", "user udid 2").account
    val user3 = signUp("user name 3", "user password 3", "user udid 3").account
    val user4 = signUp("user name 4", "user password 4", "user udid 4").account
    val user5 = signUp("user name 5", "user password 5", "user udid 5").account

    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(groupInvitesRepository.create(List(user1.id, user2.id, user3.id, user4.id, user5.id), groupId, sessionUser.id.toSessionId))
    val result1 = Await.result(groupInvitesRepository.findAll(None, None, None, user1.id.toSessionId))
    val result2 = Await.result(groupInvitesRepository.findAll(None, None, None, user2.id.toSessionId))
    val result3 = Await.result(groupInvitesRepository.findAll(None, None, None, user3.id.toSessionId))
    val result4 = Await.result(groupInvitesRepository.findAll(None, None, None, user4.id.toSessionId))
    val result5 = Await.result(groupInvitesRepository.findAll(None, None, None, user5.id.toSessionId))
    assert(result1.size == 1)
    assert(result2.size == 1)
    assert(result3.size == 1)
    assert(result4.size == 1)
    assert(result5.size == 1)

  }

  test("invite a account to a group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user1 = signUp("user name 1", "user password 2", "user udid 2").account
    val user2 = signUp("user name 2", "user password 2", "user udid 2").account
    val user3 = signUp("user name 3", "user password 2", "user udid 2").account

    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupInviteId = Await.result(groupInvitesRepository.create(user2.id, groupId, sessionUser.id.toSessionId))
    val groupInvite = Await.result(groupInviteDAO.find(groupInviteId, user2.id.toSessionId))
    assert(groupInvite.isDefined)
    assert(groupInvite.get.groupId == groupId)
    assert(groupInvite.get.accountId == user2.id)
    assert(groupInvite.get.by == sessionUser.id)
    assert(groupInvite.get.inviteStatus == GroupInviteStatusType.noresponsed.toValue)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitesRepository.create(AccountId(0L), groupId, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitesRepository.create(user2.id, groupId, sessionUser.id.toSessionId))
    }.error == AccountAlreadyInvited)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitesRepository.create(user2.id, GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitesRepository.create(user2.id, GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitesRepository.create(user1.id, groupId, user3.id.toSessionId))
    }.error == AuthorityNotFound)

  }

  test("accept a group invite") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user1 = signUp("user name 1", "user password 1", "user udid 1").account
    val user2 = signUp("user name 2", "user password 2", "user udid 2").account

    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupInviteId = Await.result(groupInvitesRepository.create(user2.id, groupId, sessionUser.id.toSessionId))
    Await.result(groupInvitesRepository.accept(groupInviteId, user2.id.toSessionId))
    assert(Await.result(groupAccountsDAO.exist(user2.id, groupId)) == true)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitesRepository.accept(GroupInviteId(0L), user2.id.toSessionId))
    }.error == GroupInviteNotFound)

    val groupId2 = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupInviteId2 = Await.result(groupInvitesRepository.create(user1.id, groupId2, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId2, user1.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(groupInvitesRepository.create(user1.id, groupId2, sessionUser.id.toSessionId))
    }.error == AccountAlreadyJoined)

    Await.result(groupInvitesRepository.accept(groupInviteId2, user1.id.toSessionId))
    // TODO : Check

  }

  test("reject a group invite") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user2 = signUp("user name 2", "user password 2", "user udid 2").account

    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupInviteId = Await.result(groupInvitesRepository.create(user2.id, groupId, sessionUser.id.toSessionId))
    groupInvitesRepository.reject(groupInviteId, user2.id.toSessionId)
    assert(Await.result(groupAccountsDAO.exist(user2.id, groupId)) == false)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitesRepository.reject(GroupInviteId(0L), user2.id.toSessionId))
    }.error == GroupInviteNotFound)

  }

  test("find group invites to session") (pending)

}
