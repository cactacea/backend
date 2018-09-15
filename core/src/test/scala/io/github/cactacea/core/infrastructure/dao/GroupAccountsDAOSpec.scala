package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec

class GroupAccountsDAOSpec extends DAOSpec {

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val groupUsersDAO: GroupAccountsDAO = injector.instance[GroupAccountsDAO]
  val accountGroupsDAO: AccountGroupsDAO = injector.instance[AccountGroupsDAO]

//  test("create user") {
//
//    val sessionAccount = createAccount("account0")
//    val member1 = createAccount("account1")
//    val member2 = createAccount("account2")
//    val member3 = createAccount("account3")
//    val member4 = createAccount("account4")
//    val member5 = createAccount("account5")
//
//    val groupId = Await.result(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
//    accountGroupsDAO.create(member1.id, groupId)
//    accountGroupsDAO.create(member2.id, groupId)
//    accountGroupsDAO.create(member3.id, groupId)
//    accountGroupsDAO.create(member4.id, groupId)
//    accountGroupsDAO.create(member5.id, groupId)
//
//    // TODO
//
//  }


//  test("create users") {
//
//    val sessionAccount = createAccount("account0")
//    val member1 = createAccount("account1")
//    val member2 = createAccount("account2")
//    val member3 = createAccount("account3")
//    val member4 = createAccount("account4")
//    val member5 = createAccount("account5")
//
//    val accountIds = List(member1.id, member2.id, member3.id, member4.id, member5.id)
//    val groupId = Await.result(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
//    Await.result(accountGroupsDAO.create(accountIds, groupId))
//
//    // TODO
//
//  }

  test("findAll") {

    val sessionAccount = createAccount("GroupAccountsDAOSpec1")
    val member1 = createAccount("GroupAccountsDAOSpec2")
    val member2 = createAccount("GroupAccountsDAOSpec3")
    val member3 = createAccount("GroupAccountsDAOSpec4")
    val member4 = createAccount("GroupAccountsDAOSpec5")
    val member5 = createAccount("GroupAccountsDAOSpec6")

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

    val result2 = Await.result(groupUsersDAO.findAll(groupId, Some(result1(2)._3.id.value), None, Some(3), sessionAccount.id.toSessionId))
    assert(result2.size == 2)

  }

  test("delete") {

    val sessionAccount = createAccount("GroupAccountsDAOSpec7")
    val member1 = createAccount("GroupAccountsDAOSpec8")
    val member2 = createAccount("GroupAccountsDAOSpec9")
    val member3 = createAccount("GroupAccountsDAOSpec10")
    val member4 = createAccount("GroupAccountsDAOSpec11")
    val member5 = createAccount("GroupAccountsDAOSpec12")

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

    val sessionAccount = createAccount("GroupAccountsDAOSpec13")
    val member1 = createAccount("GroupAccountsDAOSpec14")
    val member2 = createAccount("GroupAccountsDAOSpec15")
    val member3 = createAccount("GroupAccountsDAOSpec16")
    val member4 = createAccount("GroupAccountsDAOSpec17")
    val member5 = createAccount("GroupAccountsDAOSpec18")

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

    val sessionAccount = createAccount("GroupAccountsDAOSpec19")
    val member1 = createAccount("GroupAccountsDAOSpec20")
    val member2 = createAccount("GroupAccountsDAOSpec21")
    val member3 = createAccount("GroupAccountsDAOSpec22")
    val member4 = createAccount("GroupAccountsDAOSpec23")
    val member5 = createAccount("GroupAccountsDAOSpec24")

    val accountIds = List(member1.id, member2.id, member3.id, member4.id, member5.id)
    val groupId = Await.result(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(accountIds, groupId))

    val result = Await.result(groupUsersDAO.findCount(groupId))
    assert(result == 5)

  }

}
