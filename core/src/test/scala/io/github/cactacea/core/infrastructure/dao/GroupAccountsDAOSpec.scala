package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.helpers.DAOSpec

class GroupAccountsDAOSpec extends DAOSpec {

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val groupUsersDAO: GroupAccountsDAO = injector.instance[GroupAccountsDAO]
  val accountGroupsDAO: AccountGroupsDAO = injector.instance[AccountGroupsDAO]

  test("create user") {

    val sessionAccount = this.createAccount(0L)
    val member1 = this.createAccount(1L)
    val member2 = this.createAccount(2L)
    val member3 = this.createAccount(3L)
    val member4 = this.createAccount(4L)
    val member5 = this.createAccount(5L)

    val groupId = Await.result(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val result1 = Await.result(accountGroupsDAO.create(member1.id, groupId))
    val result2 = Await.result(accountGroupsDAO.create(member2.id, groupId))
    val result3 = Await.result(accountGroupsDAO.create(member3.id, groupId))
    val result4 = Await.result(accountGroupsDAO.create(member4.id, groupId))
    val result5 = Await.result(accountGroupsDAO.create(member5.id, groupId))

    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == true)
    assert(result4 == true)
    assert(result5 == true)

  }


  test("create users") {

    val sessionAccount = this.createAccount(0L)
    val member1 = this.createAccount(1L)
    val member2 = this.createAccount(2L)
    val member3 = this.createAccount(3L)
    val member4 = this.createAccount(4L)
    val member5 = this.createAccount(5L)

    val accountIds = List(member1.id, member2.id, member3.id, member4.id, member5.id)
    val groupId = Await.result(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val result = Await.result(accountGroupsDAO.create(accountIds, groupId))
    assert(result == true)

  }

  test("findAll") {

    val sessionAccount = this.createAccount(0L)
    val member1 = this.createAccount(1L)
    val member2 = this.createAccount(2L)
    val member3 = this.createAccount(3L)
    val member4 = this.createAccount(4L)
    val member5 = this.createAccount(5L)

    val groupId = Await.result(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(member1.id, groupId))
    Await.result(accountGroupsDAO.create(member2.id, groupId))
    Await.result(accountGroupsDAO.create(member3.id, groupId))
    Await.result(accountGroupsDAO.create(member4.id, groupId))
    Await.result(accountGroupsDAO.create(member5.id, groupId))

    val result1 = Await.result(groupUsersDAO.findAll(groupId, None, None, Some(3), sessionAccount.id.toSessionId))
    assert(result1.size == 3)
    val joinedAccount1 = result1(0)._3
    val joinedAccount2 = result1(1)._3
    val joinedAccount3 = result1(2)._3

    val result2 = Await.result(groupUsersDAO.findAll(groupId, Some(joinedAccount3.joinedAt), None, Some(3), sessionAccount.id.toSessionId))
    assert(result2.size == 2)

  }

  test("delete") {

    val sessionAccount = this.createAccount(0L)
    val member1 = this.createAccount(1L)
    val member2 = this.createAccount(2L)
    val member3 = this.createAccount(3L)
    val member4 = this.createAccount(4L)
    val member5 = this.createAccount(5L)

    val accountIds = List(member1.id, member2.id, member3.id, member4.id, member5.id)
    val groupId = Await.result(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(accountIds, groupId))

    Await.result(accountGroupsDAO.delete(member1.id, groupId))
    Await.result(accountGroupsDAO.delete(member3.id, groupId))
    Await.result(accountGroupsDAO.delete(member5.id, groupId))

    val result1 = Await.result(groupUsersDAO.findAll(groupId, None, None, Some(3), sessionAccount.id.toSessionId))
    assert(result1.size == 2)


  }

  test("exist") {

    val sessionAccount = this.createAccount(0L)
    val member1 = this.createAccount(1L)
    val member2 = this.createAccount(2L)
    val member3 = this.createAccount(3L)
    val member4 = this.createAccount(4L)
    val member5 = this.createAccount(5L)

    val accountIds = List(member1.id, member2.id, member3.id, member4.id, member5.id)
    val groupId = Await.result(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(accountIds, groupId))

    Await.result(accountGroupsDAO.delete(member1.id, groupId))
    Await.result(accountGroupsDAO.delete(member3.id, groupId))
    Await.result(accountGroupsDAO.delete(member5.id, groupId))

    val result1 = Await.result(groupUsersDAO.exist(member1.id, groupId))
    val result2 = Await.result(groupUsersDAO.exist(member2.id, groupId))
    val result3 = Await.result(groupUsersDAO.exist(member3.id, groupId))
    val result4 = Await.result(groupUsersDAO.exist(member4.id, groupId))
    val result5 = Await.result(groupUsersDAO.exist(member5.id, groupId))

    assert(result1 == false)
    assert(result2 == true)
    assert(result3 == false)
    assert(result4 == true)
    assert(result5 == false)

  }


  test("findCount") {

    val sessionAccount = this.createAccount(0L)
    val member1 = this.createAccount(1L)
    val member2 = this.createAccount(2L)
    val member3 = this.createAccount(3L)
    val member4 = this.createAccount(4L)
    val member5 = this.createAccount(5L)

    val accountIds = List(member1.id, member2.id, member3.id, member4.id, member5.id)
    val groupId = Await.result(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(accountIds, groupId))

    val result = Await.result(groupUsersDAO.findCount(groupId))
    assert(result == 5)

  }

}
