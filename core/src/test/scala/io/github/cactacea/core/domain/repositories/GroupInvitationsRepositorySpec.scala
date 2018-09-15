package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupInvitationStatusType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao.{GroupAccountsDAO, GroupInvitationsDAO, GroupsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, GroupInvitationId}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class GroupInvitationsRepositorySpec extends RepositorySpec {

  val groupInvitationsRepository = injector.instance[GroupInvitationsRepository]
  val groupsRepository = injector.instance[GroupsRepository]
  val groupAccountsRepository = injector.instance[GroupAccountsRepository]
  val groupInvitationDAO = injector.instance[GroupInvitationsDAO]
  val groupAccountsDAO = injector.instance[GroupAccountsDAO]
  val groupsDAO = injector.instance[GroupsDAO]

  test("invitation accounts to a groups") {

    val sessionUser = signUp("GroupInvitationsRepositorySpec1", "session user password", "session user udid")
    val user1 = signUp("GroupInvitationsRepositorySpec2", "user password 1", "user udid 1")
    val user2 = signUp("GroupInvitationsRepositorySpec3", "user password 2", "user udid 2")
    val user3 = signUp("GroupInvitationsRepositorySpec4", "user password 3", "user udid 3")
    val user4 = signUp("GroupInvitationsRepositorySpec5", "user password 4", "user udid 4")
    val user5 = signUp("GroupInvitationsRepositorySpec6", "user password 5", "user udid 5")

    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(groupInvitationsRepository.create(user1.id, groupId, sessionUser.id.toSessionId))
    Await.result(groupInvitationsRepository.create(user2.id, groupId, sessionUser.id.toSessionId))
    Await.result(groupInvitationsRepository.create(user3.id, groupId, sessionUser.id.toSessionId))
    Await.result(groupInvitationsRepository.create(user4.id, groupId, sessionUser.id.toSessionId))
    Await.result(groupInvitationsRepository.create(user5.id, groupId, sessionUser.id.toSessionId))
    val result1 = Await.result(groupInvitationsRepository.findAll(None, None, None, user1.id.toSessionId))
    val result2 = Await.result(groupInvitationsRepository.findAll(None, None, None, user2.id.toSessionId))
    val result3 = Await.result(groupInvitationsRepository.findAll(None, None, None, user3.id.toSessionId))
    val result4 = Await.result(groupInvitationsRepository.findAll(None, None, None, user4.id.toSessionId))
    val result5 = Await.result(groupInvitationsRepository.findAll(None, None, None, user5.id.toSessionId))
    assert(result1.size == 1)
    assert(result2.size == 1)
    assert(result3.size == 1)
    assert(result4.size == 1)
    assert(result5.size == 1)

  }

  test("invitation a account to a group") {

    val sessionUser = signUp("GroupInvitationsRepositorySpec7", "session user password", "session user udid")
    val user1 = signUp("GroupInvitationsRepositorySpec8", "user password 2", "user udid 2")
    val user2 = signUp("GroupInvitationsRepositorySpec9", "user password 2", "user udid 2")
    val user3 = signUp("GroupInvitationsRepositorySpec10", "user password 2", "user udid 2")

    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupInvitationId = Await.result(groupInvitationsRepository.create(user2.id, groupId, sessionUser.id.toSessionId))
    val groupInvitation = Await.result(groupInvitationDAO.find(groupInvitationId, user2.id.toSessionId))
    assert(groupInvitation.isDefined)
    assert(groupInvitation.get.groupId == groupId)
    assert(groupInvitation.get.accountId == user2.id)
    assert(groupInvitation.get.by == sessionUser.id)
    assert(groupInvitation.get.invitationStatus == GroupInvitationStatusType.noResponded)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitationsRepository.create(AccountId(0L), groupId, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitationsRepository.create(user2.id, groupId, sessionUser.id.toSessionId))
    }.error == AccountAlreadyInvited)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitationsRepository.create(user2.id, GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitationsRepository.create(user2.id, GroupId(0L), sessionUser.id.toSessionId))
    }.error == GroupNotFound)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitationsRepository.create(user1.id, groupId, user3.id.toSessionId))
    }.error == AuthorityNotFound)

  }

  test("accept a group invitation") {

    val sessionUser = signUp("GroupInvitationsRepositorySpec11", "session user password", "session user udid")
    val user1 = signUp("GroupInvitationsRepositorySpec12", "user password 1", "user udid 1")
    val user2 = signUp("GroupInvitationsRepositorySpec13", "user password 2", "user udid 2")

    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupInvitationId = Await.result(groupInvitationsRepository.create(user2.id, groupId, sessionUser.id.toSessionId))
    Await.result(groupInvitationsRepository.accept(groupInvitationId, user2.id.toSessionId))
    assert(Await.result(groupAccountsDAO.exist(user2.id, groupId)) == true)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitationsRepository.accept(GroupInvitationId(0L), user2.id.toSessionId))
    }.error == GroupInvitationNotFound)

    val groupId2 = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupInvitationId2 = Await.result(groupInvitationsRepository.create(user1.id, groupId2, sessionUser.id.toSessionId))
    Await.result(groupAccountsRepository.create(groupId2, user1.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(groupInvitationsRepository.create(user1.id, groupId2, sessionUser.id.toSessionId))
    }.error == AccountAlreadyJoined)

    Await.result(groupInvitationsRepository.accept(groupInvitationId2, user1.id.toSessionId))
    // TODO : Check

  }

  test("reject a group invitation") {

    val sessionUser = signUp("GroupInvitationsRepositorySpec14", "session user password", "session user udid")
    val user2 = signUp("GroupInvitationsRepositorySpec15", "user password 2", "user udid 2")

    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupInvitationId = Await.result(groupInvitationsRepository.create(user2.id, groupId, sessionUser.id.toSessionId))
    groupInvitationsRepository.reject(groupInvitationId, user2.id.toSessionId)
    assert(Await.result(groupAccountsDAO.exist(user2.id, groupId)) == false)

    assert(intercept[CactaceaException] {
      Await.result(groupInvitationsRepository.reject(GroupInvitationId(0L), user2.id.toSessionId))
    }.error == GroupInvitationNotFound)

  }

  test("find group invitations to session") (pending)

}
