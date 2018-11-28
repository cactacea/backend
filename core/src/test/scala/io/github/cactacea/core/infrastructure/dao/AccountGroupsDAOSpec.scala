package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.AccountGroups

class AccountGroupsDAOSpec extends DAOSpec {

  import db._

  test("create one to one group") {

    val sessionAccount = createAccount("AccountGroupsDAOSpec1")
    val account1 = createAccount("AccountGroupsDAOSpec2")

    val groupId = execute(groupsDAO.create(Some("new group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account1.id, groupId, sessionAccount.id.toSessionId))

    val accountGroups = execute(db.run(query[AccountGroups].filter(_.groupId == lift(groupId)).sortBy(_.joinedAt)(Ord.asc)))
    assert(accountGroups.size == 1)
    val userGroup1 = accountGroups(0)

    assert(userGroup1.accountId == account1.id)
    assert(userGroup1.groupId == groupId)
    assert(userGroup1.hidden == false)
    assert(userGroup1.toAccountId == sessionAccount.id)
    assert(userGroup1.unreadCount == 0L)

  }

  test("create") {

    val sessionAccount = createAccount("AccountGroupsDAOSpec3")
    val account1 = createAccount("AccountGroupsDAOSpec4")
    val account2 = createAccount("AccountGroupsDAOSpec5")
    val account3 = createAccount("AccountGroupsDAOSpec6")
    val account4 = createAccount("AccountGroupsDAOSpec7")

    val groupId = execute(groupsDAO.create(Some("new one to one group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(account2.id, groupId))
    execute(accountGroupsDAO.create(account3.id, groupId))
    execute(accountGroupsDAO.create(account4.id, groupId))

    val accountGroups = execute(db.run(query[AccountGroups].filter(_.groupId == lift(groupId)).sortBy(_.joinedAt)(Ord.asc)))
    assert(accountGroups.size == 4)
    val userGroup1 = accountGroups(0)
    val userGroup2 = accountGroups(1)
    val userGroup3 = accountGroups(2)
    val userGroup4 = accountGroups(3)

    assert(userGroup1.accountId == account1.id)
    assert(userGroup2.accountId == account2.id)
    assert(userGroup3.accountId == account3.id)
    assert(userGroup4.accountId == account4.id)

    assert(userGroup1.groupId == groupId)
    assert(userGroup2.groupId == groupId)
    assert(userGroup3.groupId == groupId)
    assert(userGroup4.groupId == groupId)

    assert(userGroup1.hidden == false)
    assert(userGroup2.hidden == false)
    assert(userGroup3.hidden == false)
    assert(userGroup4.hidden == false)

    assert(userGroup1.toAccountId == userGroup1.accountId)
    assert(userGroup2.toAccountId == userGroup2.accountId)
    assert(userGroup3.toAccountId == userGroup3.accountId)
    assert(userGroup4.toAccountId == userGroup4.accountId)

    assert(userGroup1.unreadCount ==  0)
    assert(userGroup2.unreadCount ==  0)
    assert(userGroup3.unreadCount ==  0)
    assert(userGroup4.unreadCount ==  0)

  }

  test("delete") {

    val sessionAccount = createAccount("AccountGroupsDAOSpec8")
    val account1 = createAccount("AccountGroupsDAOSpec9")
    val account2 = createAccount("AccountGroupsDAOSpec10")
    val account3 = createAccount("AccountGroupsDAOSpec11")
    val account4 = createAccount("AccountGroupsDAOSpec12")

    val groupId = execute(groupsDAO.create(Some("new one to one group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(account2.id, groupId))
    execute(accountGroupsDAO.create(account3.id, groupId))
    execute(accountGroupsDAO.create(account4.id, groupId))

    execute(accountGroupsDAO.delete(account1.id, groupId))
    val result1 = execute(db.run(query[AccountGroups].filter(_.groupId == lift(groupId)).sortBy(_.joinedAt)(Ord.asc)))
    assert(result1.size == 3)

    execute(accountGroupsDAO.delete(account2.id, groupId))
    val result2 = execute(db.run(query[AccountGroups].filter(_.groupId == lift(groupId)).sortBy(_.joinedAt)(Ord.asc)))
    assert(result2.size == 2)

    execute(accountGroupsDAO.delete(account3.id, groupId))
    val result3 = execute(db.run(query[AccountGroups].filter(_.groupId == lift(groupId)).sortBy(_.joinedAt)(Ord.asc)))
    assert(result3.size == 1)

    execute(accountGroupsDAO.delete(account4.id, groupId))
    val result4 = execute(db.run(query[AccountGroups].filter(_.groupId == lift(groupId)).sortBy(_.joinedAt)(Ord.asc)))
    assert(result4.size == 0)

  }

  test("updateUnreadCount") {

    val sessionAccount = createAccount("AccountGroupsDAOSpec13")
    val account1 = createAccount("AccountGroupsDAOSpec14")
    val account2 = createAccount("AccountGroupsDAOSpec15")
    val account3 = createAccount("AccountGroupsDAOSpec16")
    val account4 = createAccount("AccountGroupsDAOSpec17")

    val groupId = execute(groupsDAO.create(Some("new one to one group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(account2.id, groupId))
    execute(accountGroupsDAO.create(account3.id, groupId))
    execute(accountGroupsDAO.create(account4.id, groupId))
    execute(accountGroupsDAO.updateUnreadCount(groupId))

    val accountGroups = execute(db.run(query[AccountGroups].filter(_.groupId == lift(groupId)).sortBy(_.joinedAt)(Ord.asc)))
    assert(accountGroups.size == 4)
    val group1 = accountGroups(0)
    val group2 = accountGroups(1)
    val group3 = accountGroups(2)
    val group4 = accountGroups(3)

    assert(group1.accountId == account1.id)
    assert(group2.accountId == account2.id)
    assert(group3.accountId == account3.id)
    assert(group4.accountId == account4.id)

    assert(group1.groupId == groupId)
    assert(group2.groupId == groupId)
    assert(group3.groupId == groupId)
    assert(group4.groupId == groupId)

    assert(group1.hidden == false)
    assert(group2.hidden == false)
    assert(group3.hidden == false)
    assert(group4.hidden == false)

    assert(group1.toAccountId == group1.accountId)
    assert(group2.toAccountId == group2.accountId)
    assert(group3.toAccountId == group3.accountId)
    assert(group4.toAccountId == group4.accountId)

    assert(group1.unreadCount == 1)
    assert(group2.unreadCount == 1)
    assert(group3.unreadCount == 1)
    assert(group4.unreadCount == 1)

  }

  test("updateHidden") {

    val sessionAccount = createAccount("AccountGroupsDAOSpec18")
    val account1 = createAccount("AccountGroupsDAOSpec19")
    val account2 = createAccount("AccountGroupsDAOSpec20")
    val account3 = createAccount("AccountGroupsDAOSpec21")
    val account4 = createAccount("AccountGroupsDAOSpec22")

    val groupId = execute(groupsDAO.create(Some("new one to one group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account1.id, groupId))
    execute(accountGroupsDAO.create(account2.id, groupId))
    execute(accountGroupsDAO.create(account3.id, groupId))
    execute(accountGroupsDAO.create(account4.id, groupId))

    execute(accountGroupsDAO.updateHidden(groupId, true, account1.id.toSessionId))
    execute(accountGroupsDAO.updateHidden(groupId, true, account3.id.toSessionId))

    val accountGroups = execute(db.run(query[AccountGroups].filter(_.groupId == lift(groupId)).sortBy(_.joinedAt)(Ord.asc)))
    assert(accountGroups.size == 4)
    val userGroup1 = accountGroups(0)
    val userGroup2 = accountGroups(1)
    val userGroup3 = accountGroups(2)
    val userGroup4 = accountGroups(3)

    assert(userGroup1.accountId == account1.id)
    assert(userGroup2.accountId == account2.id)
    assert(userGroup3.accountId == account3.id)
    assert(userGroup4.accountId == account4.id)

    assert(userGroup1.groupId == groupId)
    assert(userGroup2.groupId == groupId)
    assert(userGroup3.groupId == groupId)
    assert(userGroup4.groupId == groupId)

    assert(userGroup1.hidden == true)
    assert(userGroup2.hidden == false)
    assert(userGroup3.hidden == true)
    assert(userGroup4.hidden == false)

    assert(userGroup1.toAccountId == account1.id, 0)
    assert(userGroup2.toAccountId == account2.id, 0)
    assert(userGroup3.toAccountId == account3.id, 0)
    assert(userGroup4.toAccountId == account4.id, 0)

    assert(userGroup1.unreadCount == 0)
    assert(userGroup2.unreadCount == 0)
    assert(userGroup3.unreadCount == 0)
    assert(userGroup4.unreadCount == 0)

  }

  test("findAll") {

    val sessionAccount = createAccount("AccountGroupsDAOSpec23")
    createAccount("AccountGroupsDAOSpec24")
    createAccount("AccountGroupsDAOSpec25")
    createAccount("AccountGroupsDAOSpec26")
    createAccount("AccountGroupsDAOSpec27")

    val groupId1 = execute(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId1))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId1))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId1))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId2))

    val result1 = execute(accountGroupsDAO.findAll(sessionAccount.id, None, None, Some(2), false))
    assert(result1.size == 2)
    assert(result1(0)._2.id == groupId2)
    assert(result1(0)._4.isDefined == false)

    val (messageId1, postedAt1) = execute(messagesDAO.create(groupId1, Some("New Message1"), 2, None, sessionAccount.id.toSessionId))
    execute(messagesDAO.create(groupId1, Some("New Message2"), 2, None, sessionAccount.id.toSessionId))
    val (messageId3, postedAt3) = execute(messagesDAO.create(groupId2, Some("New Message3"), 2, None, sessionAccount.id.toSessionId))
    execute(groupsDAO.update(groupId1, messageId1, postedAt1, sessionAccount.id.toSessionId))
    execute(groupsDAO.update(groupId2, messageId3, postedAt3, sessionAccount.id.toSessionId))

    val result2 = execute(accountGroupsDAO.findAll(sessionAccount.id, None, None, Some(2), false))
    assert(result2.size == 2)
    val (ag, _, m, _) = result2(0)
    assert(ag.groupId == groupId2)
    assert(m.isDefined)
    assert(m.map(_.id) == Some(messageId3))
    assert(m.flatMap(_.message) == Some("New Message3"))

  }


  test("find one to one group") {

    val sessionAccount = createAccount("AccountGroupsDAOSpec28")
    val account1 = createAccount("AccountGroupsDAOSpec29")
    val account2 = createAccount("AccountGroupsDAOSpec30")

    val groupId1 = execute(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account1.id, groupId1, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account2.id, groupId2, sessionAccount.id.toSessionId))

    val result = execute(accountGroupsDAO.findByAccountId(account1.id, sessionAccount.id.toSessionId))
    assert(result.isDefined == true)
    val group = result.get._2

    assert(group.id == groupId1)
    assert(group.name == Some("new group name1"))
    assert(group.by == sessionAccount.id)
    assert(group.invitationOnly == true)
    assert(group.privacyType == GroupPrivacyType.everyone)
    assert(group.accountCount == 0)

  }

  test("find id by message") {

    val sessionAccount = createAccount("AccountGroupsDAOSpec31")

    val groupId1 = execute(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId1))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId1))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId1))
    execute(accountGroupsDAO.create(sessionAccount.id, groupId2))

    val (messageId1, postedAt1) = execute(messagesDAO.create(groupId1, Some("New Message1"), 2, None, sessionAccount.id.toSessionId))
    val (messageId3, postedAt3) = execute(messagesDAO.create(groupId2, Some("New Message3"), 2, None, sessionAccount.id.toSessionId))
    execute(groupsDAO.update(groupId1, messageId1, postedAt1, sessionAccount.id.toSessionId))
    execute(groupsDAO.update(groupId2, messageId3, postedAt3, sessionAccount.id.toSessionId))

    val result1 = execute(accountGroupsDAO.findGroupId(messageId1, sessionAccount.id.toSessionId))
    val result2 = execute(accountGroupsDAO.findGroupId(messageId3, sessionAccount.id.toSessionId))
    assert(result1.isDefined == true)
    assert(result2.isDefined == true)

    val resultGroupId1 = result1.get
    val resultGroupId3 = result2.get
    assert(resultGroupId1 == groupId1)
    assert(resultGroupId3 == groupId2)


  }

  test("exist") {

    val sessionAccount = createAccount("AccountGroupsDAOSpec32")
    val account1 = createAccount("AccountGroupsDAOSpec33")
    val account2 = createAccount("AccountGroupsDAOSpec34")

    val groupId1 = execute(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = execute(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account1.id, groupId1, sessionAccount.id.toSessionId))
    execute(accountGroupsDAO.create(account2.id, groupId2, sessionAccount.id.toSessionId))

    val result1 = execute(accountGroupsDAO.exist(groupId1, account1.id.toSessionId))
    val result2 = execute(accountGroupsDAO.exist(groupId2, account2.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

  }

}
