package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec

class GroupAccountsDAOSpec extends DAOSpec {


//  test("create user") {
//
//    val sessionAccount = createAccount("account0")
//    val member1 = createAccount("account1")
//    val member2 = createAccount("account2")
//    val member3 = createAccount("account3")
//    val member4 = createAccount("account4")
//    val member5 = createAccount("account5")
//
//    val groupId = execute(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
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
//    val groupId = execute(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
//    execute(accountGroupsDAO.create(accountIds, groupId))
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

    val groupId = execute(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(member1.id, groupId))
    execute(accountGroupsDAO.create(member2.id, groupId))
    execute(accountGroupsDAO.create(member3.id, groupId))
    execute(accountGroupsDAO.create(member4.id, groupId))
    execute(accountGroupsDAO.create(member5.id, groupId))

    val result1 = execute(groupAccountsDAO.findAll(groupId, None, None, Some(3)))
    assert(result1.size == 3)
    val joinedAccount1 = result1(0)._3
    val joinedAccount2 = result1(1)._3
    val joinedAccount3 = result1(2)._3
    assert(joinedAccount1.accountId == member5.id)
    assert(joinedAccount2.accountId == member4.id)
    assert(joinedAccount3.accountId == member3.id)

    val result2 = execute(groupAccountsDAO.findAll(groupId, Some(result1(2)._3.joinedAt), None, Some(3)))
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
    val groupId = execute(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(accountIds, groupId))

    execute(accountGroupsDAO.delete(member1.id, groupId))
    execute(accountGroupsDAO.delete(member3.id, groupId))
    execute(accountGroupsDAO.delete(member5.id, groupId))

    val result1 = execute(groupAccountsDAO.findAll(groupId, None, None, Some(3))) //, sessionAccount.id.toSessionId))
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
    val groupId = execute(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(accountIds, groupId))

    execute(accountGroupsDAO.delete(member1.id, groupId))
    execute(accountGroupsDAO.delete(member3.id, groupId))
    execute(accountGroupsDAO.delete(member5.id, groupId))

    val result1 = execute(groupAccountsDAO.exist(member1.id, groupId))
    val result2 = execute(groupAccountsDAO.exist(member2.id, groupId))
    val result3 = execute(groupAccountsDAO.exist(member3.id, groupId))
    val result4 = execute(groupAccountsDAO.exist(member4.id, groupId))
    val result5 = execute(groupAccountsDAO.exist(member5.id, groupId))

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
    val groupId = execute(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(accountIds, groupId))

    val result = execute(groupAccountsDAO.findCount(groupId))
    assert(result == 5)

  }

}
