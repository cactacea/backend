package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec

class GroupAccountsDAOSpec extends DAOSpec {




  test("find all") {

    val sessionAccount = createAccount("GroupAccountsDAOSpec1")
    val member1 = createAccount("GroupAccountsDAOSpec2")
    val member2 = createAccount("GroupAccountsDAOSpec3")
    val member3 = createAccount("GroupAccountsDAOSpec4")
    val member4 = createAccount("GroupAccountsDAOSpec5")
    val member5 = createAccount("GroupAccountsDAOSpec6")

    val groupId = execute(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(member1.id, groupId))
    execute(accountGroupsDAO.create(member2.id, groupId))
    val accountGroupId3 = execute(accountGroupsDAO.create(member3.id, groupId))
    val accountGroupId4 = execute(accountGroupsDAO.create(member4.id, groupId))
    val accountGroupId5 = execute(accountGroupsDAO.create(member5.id, groupId))

    val result1 = execute(groupAccountsDAO.find(groupId, None, 0, 3))
    assert(result1.size == 3)
    val next1 = result1(0)
    val next2 = result1(1)
    val next3 = result1(2)
    assert(next1.next == Some(accountGroupId5.value))
    assert(next2.next == Some(accountGroupId4.value))
    assert(next3.next == Some(accountGroupId3.value))

    val result2 = execute(groupAccountsDAO.find(groupId, next3.next, 0, 3))
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
    val groupId = execute(groupsDAO.create(Some("New Group"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(accountIds, groupId))

    execute(accountGroupsDAO.delete(member1.id, groupId))
    execute(accountGroupsDAO.delete(member3.id, groupId))
    execute(accountGroupsDAO.delete(member5.id, groupId))

    val result1 = execute(groupAccountsDAO.find(groupId, None, 0, 3)) //, sessionAccount.id.toSessionId))
    assert(result1.size == 2)


  }

}
