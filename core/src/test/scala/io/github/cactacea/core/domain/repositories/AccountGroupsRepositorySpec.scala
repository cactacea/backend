package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.AccountNotJoined

class AccountGroupsRepositorySpec extends RepositorySpec {

  val accountGroupsRepository = injector.instance[AccountGroupsRepository]
  val messagesRepository = injector.instance[MessagesRepository]
  val groupsRepository = injector.instance[GroupsRepository]

  test("delete") {
    val sessionUser = signUp("AccountGroupsRepositorySpec1", "session user password", "session user udid")
    val groupId = execute(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(messagesRepository.create(groupId, Some("message"), None, sessionUser.id.toSessionId))
    execute(accountGroupsRepository.delete(groupId, sessionUser.id.toSessionId))
    val result1 = execute(accountGroupsRepository.findAll(None, None, None, false, sessionUser.id.toSessionId))
    assert(result1.size == 0)
    val result2 = execute(messagesRepository.findAll(groupId, None, None, None, true, sessionUser.id.toSessionId))
    assert(result2.size == 0)
  }

  test("find all user groups") {
    val sessionUser = signUp("AccountGroupsRepositorySpec2", "session user password", "session user udid")
    val user = signUp("AccountGroupsRepositorySpec2-2", "session user password", "session user udid")
    val groupId1 = execute(groupsRepository.create(Some("group name 1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, user.id.toSessionId))
    val groupId2 = execute(groupsRepository.create(Some("group name 2"), true, GroupPrivacyType.followers, GroupAuthorityType.member, user.id.toSessionId))
    val groupId3 = execute(groupsRepository.create(Some("group name 3"), true, GroupPrivacyType.follows, GroupAuthorityType.member, user.id.toSessionId))
    val groupId4 = execute(groupsRepository.create(Some("group name 4"), true, GroupPrivacyType.friends, GroupAuthorityType.member, user.id.toSessionId))
    val groupId5 = execute(groupsRepository.create(Some("group name 5"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, user.id.toSessionId))
    val result = execute(accountGroupsRepository.findAll(user.id, None, None, None, sessionUser.id.toSessionId))
    assert(result.size == 5)
    assert(result(0).id == groupId5)
    assert(result(1).id == groupId4)
    assert(result(2).id == groupId3)
    assert(result(3).id == groupId2)
    assert(result(4).id == groupId1)
  }

  test("find session's user groups") {
    val sessionUser = signUp("AccountGroupsRepositorySpec3", "session user password", "session user udid")
    val groupId1 = execute(groupsRepository.create(Some("group name 1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId2 = execute(groupsRepository.create(Some("group name 2"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId3 = execute(groupsRepository.create(Some("group name 3"), true, GroupPrivacyType.follows, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId4 = execute(groupsRepository.create(Some("group name 4"), true, GroupPrivacyType.friends, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId5 = execute(groupsRepository.create(Some("group name 5"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val result = execute(accountGroupsRepository.findAll(None, None, None, false, sessionUser.id.toSessionId))
    assert(result.size == 5)
    assert(result(0).id == groupId5)
    assert(result(1).id == groupId4)
    assert(result(2).id == groupId3)
    assert(result(3).id == groupId2)
    assert(result(4).id == groupId1)
  }

  test("create one to one group") {
    val sessionUser = signUp("AccountGroupsRepositorySpec4", "session user password", "session user udid")
    val user = signUp("AccountGroupsRepositorySpec4-2", "session user password", "session user udid")
    execute(accountGroupsRepository.findOrCreate(user.id, sessionUser.id.toSessionId))
    val result1 = execute(accountGroupsRepository.findAll(None, None, None, false, sessionUser.id.toSessionId))
    val result2 = execute(accountGroupsRepository.findAll(None, None, None, false, user.id.toSessionId))
    assert(result1.size == 1)
    assert(result2.size == 1)
  }

  test("find one to one group") {
    val sessionUser = signUp("AccountGroupsRepositorySpec5", "session user password", "session user udid")
    val user = signUp("AccountGroupsRepositorySpec4-3", "session user password", "session user udid")
    val result3 = execute(accountGroupsRepository.findOrCreate(user.id, sessionUser.id.toSessionId))
    val result4 = execute(accountGroupsRepository.findOrCreate(user.id, sessionUser.id.toSessionId))
    assert(result3.id == result4.id)
    val result1 = execute(accountGroupsRepository.findAll(None, None, None, false, sessionUser.id.toSessionId))
    val result2 = execute(accountGroupsRepository.findAll(None, None, None, false, user.id.toSessionId))
    assert(result1.size == 1)
    assert(result2.size == 1)
  }

  test("show") {
    val sessionUser = signUp("AccountGroupsRepositorySpec6", "session user password", "session user udid")
    val groupId1 = execute(groupsRepository.create(Some("group name 1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId2 = execute(groupsRepository.create(Some("group name 2"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId3 = execute(groupsRepository.create(Some("group name 3"), true, GroupPrivacyType.follows, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId4 = execute(groupsRepository.create(Some("group name 4"), true, GroupPrivacyType.friends, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId5 = execute(groupsRepository.create(Some("group name 5"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(accountGroupsRepository.hide(groupId1, sessionUser.id.toSessionId))
    execute(accountGroupsRepository.hide(groupId3, sessionUser.id.toSessionId))
    execute(accountGroupsRepository.hide(groupId5, sessionUser.id.toSessionId))
    val result1 = execute(accountGroupsRepository.findAll(None, None, None, false, sessionUser.id.toSessionId))
    assert(result1.size == 2)
    execute(accountGroupsRepository.show(groupId3, sessionUser.id.toSessionId))
    val result2 = execute(accountGroupsRepository.findAll(None, None, None, false, sessionUser.id.toSessionId))
    assert(result2.size == 3)
    assert(result2(0).id == groupId4)
    assert(result2(1).id == groupId3)
    assert(result2(2).id == groupId2)
  }

  test("show no exist group") {

    val session = signUp("AccountGroupsRepositorySpec7", "password", "udid")
    assert(intercept[CactaceaException] {
      execute(accountGroupsRepository.show(GroupId(-1L), session.id.toSessionId))
    }.error == AccountNotJoined)

  }

  test("hide") {
    val sessionUser = signUp("AccountGroupsRepositorySpec8", "session user password", "session user udid")
    val groupId1 = execute(groupsRepository.create(Some("group name 1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId2 = execute(groupsRepository.create(Some("group name 2"), true, GroupPrivacyType.followers, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId3 = execute(groupsRepository.create(Some("group name 3"), true, GroupPrivacyType.follows, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId4 = execute(groupsRepository.create(Some("group name 4"), true, GroupPrivacyType.friends, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupId5 = execute(groupsRepository.create(Some("group name 5"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    execute(accountGroupsRepository.hide(groupId1, sessionUser.id.toSessionId))
    execute(accountGroupsRepository.hide(groupId3, sessionUser.id.toSessionId))
    execute(accountGroupsRepository.hide(groupId5, sessionUser.id.toSessionId))
    val result = execute(accountGroupsRepository.findAll(None, None, None, false, sessionUser.id.toSessionId))
    assert(result.size == 2)
    assert(result(0).id == groupId4)
    assert(result(1).id == groupId2)
  }

  test("hide no exist group") {

    val session = signUp("AccountGroupsRepositorySpec9", "password", "udid")
    assert(intercept[CactaceaException] {
      execute(accountGroupsRepository.hide(GroupId(-1L), session.id.toSessionId))
    }.error == AccountNotJoined)

  }


}
