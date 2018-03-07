package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.AccountGroups

class AccountGroupsDAOSpec extends DAOSpec {

  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val accountGroupsDAO: AccountGroupsDAO = injector.instance[AccountGroupsDAO]
  val messagesDAO: MessagesDAO = injector.instance[MessagesDAO]

  import db._

  test("create one to one group") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")

    val groupId = Await.result(groupsDAO.create(Some("new group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val result1 = Await.result(accountGroupsDAO.create(account1.id, groupId, sessionAccount.id.toSessionId))

    assert(result1 == true)

    val accountGroups = Await.result(db.run(query[AccountGroups].sortBy(_.accountId)(Ord.asc)))
    assert(accountGroups.size == 1)
    val userGroup1 = accountGroups(0)

    assert((userGroup1.accountId, userGroup1.groupId, userGroup1.hidden, userGroup1.toAccountId, userGroup1.unreadCount) == (account1.id, groupId, false, sessionAccount.id, 0))

  }

  test("create") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")
    val account4 = createAccount("account4")

    val groupId = Await.result(groupsDAO.create(Some("new one to one group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val result1 = Await.result(accountGroupsDAO.create(account1.id, groupId))
    val result2 = Await.result(accountGroupsDAO.create(account2.id, groupId))
    val result3 = Await.result(accountGroupsDAO.create(account3.id, groupId))
    val result4 = Await.result(accountGroupsDAO.create(account4.id, groupId))

    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == true)
    assert(result4 == true)

    val accountGroups = Await.result(db.run(query[AccountGroups].sortBy(_.accountId)(Ord.asc)))
    assert(accountGroups.size == 4)
    val userGroup1 = accountGroups(0)
    val userGroup2 = accountGroups(1)
    val userGroup3 = accountGroups(2)
    val userGroup4 = accountGroups(3)

    assert((userGroup1.accountId, userGroup1.groupId, userGroup1.hidden, userGroup1.toAccountId, userGroup1.unreadCount) == (account1.id, groupId, false, userGroup1.accountId, 0))
    assert((userGroup2.accountId, userGroup2.groupId, userGroup2.hidden, userGroup2.toAccountId, userGroup2.unreadCount) == (account2.id, groupId, false, userGroup2.accountId, 0))
    assert((userGroup3.accountId, userGroup3.groupId, userGroup3.hidden, userGroup3.toAccountId, userGroup3.unreadCount) == (account3.id, groupId, false, userGroup3.accountId, 0))
    assert((userGroup4.accountId, userGroup4.groupId, userGroup4.hidden, userGroup4.toAccountId, userGroup4.unreadCount) == (account4.id, groupId, false, userGroup4.accountId, 0))


  }

  test("delete") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")
    val account4 = createAccount("account4")

    val groupId = Await.result(groupsDAO.create(Some("new one to one group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(account1.id, groupId))
    Await.result(accountGroupsDAO.create(account2.id, groupId))
    Await.result(accountGroupsDAO.create(account3.id, groupId))
    Await.result(accountGroupsDAO.create(account4.id, groupId))

    Await.result(accountGroupsDAO.delete(account1.id, groupId))
    val result1 = Await.result(db.run(query[AccountGroups].sortBy(_.accountId)(Ord.asc)))
    assert(result1.size == 3)

    Await.result(accountGroupsDAO.delete(account2.id, groupId))
    val result2 = Await.result(db.run(query[AccountGroups].sortBy(_.accountId)(Ord.asc)))
    assert(result2.size == 2)

    Await.result(accountGroupsDAO.delete(account3.id, groupId))
    val result3 = Await.result(db.run(query[AccountGroups].sortBy(_.accountId)(Ord.asc)))
    assert(result3.size == 1)

    Await.result(accountGroupsDAO.delete(account4.id, groupId))
    val result4 = Await.result(db.run(query[AccountGroups].sortBy(_.accountId)(Ord.asc)))
    assert(result4.size == 0)

  }

  test("updateUnreadCount") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")
    val account4 = createAccount("account4")

    val groupId = Await.result(groupsDAO.create(Some("new one to one group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(account1.id, groupId))
    Await.result(accountGroupsDAO.create(account2.id, groupId))
    Await.result(accountGroupsDAO.create(account3.id, groupId))
    Await.result(accountGroupsDAO.create(account4.id, groupId))

    val result1 = Await.result(accountGroupsDAO.updateUnreadCount(groupId))
    assert(result1 == true)

    val accountGroups = Await.result(db.run(query[AccountGroups].sortBy(_.accountId)(Ord.asc)))
    assert(accountGroups.size == 4)
    val group1 = accountGroups(0)
    val group2 = accountGroups(1)
    val group3 = accountGroups(2)
    val group4 = accountGroups(3)

    assert((group1.accountId, group1.groupId, group1.hidden, group1.toAccountId, group1.unreadCount) == (account1.id, groupId, false, group1.accountId, 1))
    assert((group2.accountId, group2.groupId, group2.hidden, group2.toAccountId, group2.unreadCount) == (account2.id, groupId, false, group2.accountId, 1))
    assert((group3.accountId, group3.groupId, group3.hidden, group3.toAccountId, group3.unreadCount) == (account3.id, groupId, false, group3.accountId, 1))
    assert((group4.accountId, group4.groupId, group4.hidden, group4.toAccountId, group4.unreadCount) == (account4.id, groupId, false, group4.accountId, 1))

  }

  test("updateHidden") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")
    val account4 = createAccount("account4")

    val groupId = Await.result(groupsDAO.create(Some("new one to one group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(account1.id, groupId))
    Await.result(accountGroupsDAO.create(account2.id, groupId))
    Await.result(accountGroupsDAO.create(account3.id, groupId))
    Await.result(accountGroupsDAO.create(account4.id, groupId))

    val result1 = Await.result(accountGroupsDAO.updateHidden(groupId, true, account1.id.toSessionId))
    val result2 = Await.result(accountGroupsDAO.updateHidden(groupId, true, account3.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

    val accountGroups = Await.result(db.run(query[AccountGroups].sortBy(_.accountId)(Ord.asc)))
    assert(accountGroups.size == 4)
    val userGroup1 = accountGroups(0)
    val userGroup2 = accountGroups(1)
    val userGroup3 = accountGroups(2)
    val userGroup4 = accountGroups(3)

    assert((userGroup1.accountId, userGroup1.groupId, userGroup1.hidden, userGroup1.toAccountId, userGroup1.unreadCount) == (account1.id, groupId, true,  account1.id, 0))
    assert((userGroup2.accountId, userGroup2.groupId, userGroup2.hidden, userGroup2.toAccountId, userGroup2.unreadCount) == (account2.id, groupId, false, account2.id, 0))
    assert((userGroup3.accountId, userGroup3.groupId, userGroup3.hidden, userGroup3.toAccountId, userGroup3.unreadCount) == (account3.id, groupId, true,  account3.id, 0))
    assert((userGroup4.accountId, userGroup4.groupId, userGroup4.hidden, userGroup4.toAccountId, userGroup4.unreadCount) == (account4.id, groupId, false, account4.id, 0))

  }

  test("findAll") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")
    val account2 = createAccount("account2")
    val account3 = createAccount("account3")
    val account4 = createAccount("account4")

    val groupId1 = Await.result(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId1))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId1))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId1))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId2))

    val result1 = Await.result(accountGroupsDAO.findAll(sessionAccount.id, None, None, Some(2), false, sessionAccount.id.toSessionId))
    assert(result1.size == 2)
    assert(result1(0)._1.id == groupId2)
    assert(result1(0)._2.isDefined == false)

    val messageId1 = Await.result(messagesDAO.create(groupId1, Some("New Message1"), 2, None, sessionAccount.id.toSessionId))
    Await.result(messagesDAO.create(groupId1, Some("New Message2"), 2, None, sessionAccount.id.toSessionId))
    val messageId3 = Await.result(messagesDAO.create(groupId2, Some("New Message3"), 2, None, sessionAccount.id.toSessionId))
    Await.result(groupsDAO.update(groupId1, Some(messageId1), sessionAccount.id.toSessionId))
    Await.result(groupsDAO.update(groupId2, Some(messageId3), sessionAccount.id.toSessionId))

    val result2 = Await.result(accountGroupsDAO.findAll(sessionAccount.id, None, None, Some(2), false, sessionAccount.id.toSessionId))
    assert(result2.size == 2)
    assert(result2(0)._1.id == groupId2)
    assert(result2(0)._2.isDefined == true)
    assert(result2(0)._2.forall(_.id == messageId3) == true)

    val message = result2(0)._2.get
    assert(message.message == Some("New Message3"))

  }


  test("find one to one group") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")
    val account2 = createAccount("account2")

    val groupId1 = Await.result(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val result1 = Await.result(accountGroupsDAO.create(account1.id, groupId1, sessionAccount.id.toSessionId))
    val result2 = Await.result(accountGroupsDAO.create(account2.id, groupId2, sessionAccount.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

    val result = Await.result(accountGroupsDAO.find(account1.id, sessionAccount.id.toSessionId))
    assert(result.isDefined == true)
    val group = result.get

    assert((group.id, group.name, group.by, group.invitationOnly, group.privacyType, group.authorityType, group.accountCount) == (groupId1, Some("new group name1"), sessionAccount.id, true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0))

  }

  test("find id by message") {

    val sessionAccount = createAccount("account0")

    val groupId1 = Await.result(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId1))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId1))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId1))
    Await.result(accountGroupsDAO.create(sessionAccount.id, groupId2))

    val messageId1 = Await.result(messagesDAO.create(groupId1, Some("New Message1"), 2, None, sessionAccount.id.toSessionId))
    val messageId3 = Await.result(messagesDAO.create(groupId2, Some("New Message3"), 2, None, sessionAccount.id.toSessionId))
    Await.result(groupsDAO.update(groupId1, Some(messageId1), sessionAccount.id.toSessionId))
    Await.result(groupsDAO.update(groupId2, Some(messageId3), sessionAccount.id.toSessionId))

    val result1 = Await.result(accountGroupsDAO.findGroupId(messageId1, sessionAccount.id.toSessionId))
    val result2 = Await.result(accountGroupsDAO.findGroupId(messageId3, sessionAccount.id.toSessionId))
    assert(result1.isDefined == true)
    assert(result2.isDefined == true)

    val resultGroupId1 = result1.get
    val resultGroupId3 = result2.get
    assert(resultGroupId1 == groupId1)
    assert(resultGroupId3 == groupId2)

  }

  test("exist") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")
    val account2 = createAccount("account2")

    val groupId1 = Await.result(groupsDAO.create(Some("new group name1"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val groupId2 = Await.result(groupsDAO.create(Some("new group name2"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(account1.id, groupId1, sessionAccount.id.toSessionId))
    Await.result(accountGroupsDAO.create(account2.id, groupId2, sessionAccount.id.toSessionId))

    val result1 = Await.result(accountGroupsDAO.exist(groupId1, account1.id.toSessionId))
    val result2 = Await.result(accountGroupsDAO.exist(groupId2, account2.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

  }

}
