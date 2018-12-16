package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.inject.Logging
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

class AccountsDAOSpec extends DAOSpec with Logging {

  test("create") {

    val sessionAccount = createAccount("AccountsDAOSpec1")

    val newUser = createAccount("AccountsDAOSpec2")
    val accountId = execute(
      accountsDAO.create(newUser.accountName, newUser.password)
    )

    val userList = selectAccounts(accountId, sessionAccount.id.toSessionId)
    val createdUser = userList.head

    assert(userList.size == 1)
    assert(createdUser.id == accountId)
    assert(createdUser.accountName == newUser.accountName)
    assert(createdUser.displayName == newUser.displayName)
//    assert(createdUser.accountStatus == newUser.accountStatus)

  }

  test("exist by account name and password") {

    val sessionAccount = createAccount("AccountsDAOSpec3")

    val result = execute(
      accountsDAO.find(
        sessionAccount.accountName,
        "password"
      )
    ).get

    assert(result.password == hashService.hash("password"))

  }

  test("exists by session id") {

    val sessionAccount = createAccount("AccountsDAOSpec4")

    val result = execute(
      accountsDAO.find(sessionAccount.id.toSessionId)
    ).get

    assert(result.id == sessionAccount.id)

  }

  test("exists by account ids") {

    val sessionAccount = createAccount("AccountsDAOSpec5")
    val account1 = createAccount("AccountsDAOSpec6")
    val account2 = createAccount("AccountsDAOSpec7")

    val result = execute(accountsDAO.exist(List(sessionAccount.id, account1.id, account2.id), sessionAccount.id.toSessionId))

    assert(result == true)

  }

  test("update password") {

    val sessionAccount = createAccount("AccountsDAOSpec8")

    execute(
      accountsDAO.updatePassword(
        "password",
        "password2",
        sessionAccount.id.toSessionId
      )
    )

    val result = execute(accountsDAO.find(sessionAccount.id.toSessionId)).get
    assert(result.password == hashService.hash("password2"))

  }

  test("update account name") {

    val sessionAccount = createAccount("AccountsDAOSpec9")

    val newUser = createAccount("AccountsDAOSpec10")
    val accountId = insertAccounts(newUser)

    execute(
      accountsDAO.updateAccountName("accountName2", accountId.toSessionId)
    )

    val editedUserList = selectAccounts(accountId, sessionAccount.id.toSessionId)
    val editedUser = editedUserList.head

    assert(editedUserList.size == 1)
    assert(editedUser.id == accountId)
    assert(editedUser.accountName == "accountName2")

  }

  test("update other user's userName") {

    val sessionAccount = createAccount("AccountsDAOSpec11")
    val account1 = createAccount("AccountsDAOSpec12")
    val account2 = createAccount("AccountsDAOSpec13")

    // update other user name
    execute(accountsDAO.updateDisplayName(account1.id, Some("newUserName1"), sessionAccount.id.toSessionId))
    execute(accountsDAO.updateDisplayName(account2.id, Some("newUserName2"), sessionAccount.id.toSessionId))

    val result1 = execute(accountsDAO.find(account1.id, sessionAccount.id.toSessionId))
    assert(result1.get.displayName == "newUserName1")
    val result2 = execute(accountsDAO.find(account2.id, sessionAccount.id.toSessionId))
    assert(result2.get.displayName == "newUserName2")

    execute(accountsDAO.updateDisplayName(account1.id, None, sessionAccount.id.toSessionId))
    execute(accountsDAO.updateDisplayName(account2.id, None, sessionAccount.id.toSessionId))

    val result3 = execute(accountsDAO.find(account1.id, sessionAccount.id.toSessionId))
    assert(result3.get.displayName == "AccountsDAOSpec12")
    val result4 = execute(accountsDAO.find(account2.id, sessionAccount.id.toSessionId))
    assert(result4.get.displayName == "AccountsDAOSpec13")

  }

  test("exist") {

    val sessionAccount = createAccount("AccountsDAOSpec14")
    val noBlockingUser = createAccount("AccountsDAOSpec15")
    val blockingAccount1 = createAccount("AccountsDAOSpec16")
    val blockingAccount2 = createAccount("AccountsDAOSpec17")

    val result1 = execute(accountsDAO.exist(blockingAccount1.id, sessionAccount.id.toSessionId))
    val result2 = execute(accountsDAO.exist(blockingAccount1.id, sessionAccount.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

    execute(blocksDAO.create(sessionAccount.id, blockingAccount1.id.toSessionId))
    execute(blocksDAO.create(sessionAccount.id, blockingAccount2.id.toSessionId))

    assert(execute(accountsDAO.exist(blockingAccount1.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(accountsDAO.exist(blockingAccount2.id, sessionAccount.id.toSessionId)) == false)
    assert(execute(accountsDAO.exist(noBlockingUser.id, sessionAccount.id.toSessionId)) == true)

  }

  test("find") {

    val account1 = createAccount("AccountsDAOSpec18")
    val sessionAccount = createAccount("AccountsDAOSpec19")
    val user1 = createAccount("AccountsDAOSpec20")
    val user2 = createAccount("AccountsDAOSpec21")
    val user3 = createAccount("AccountsDAOSpec22")
    val user4 = createAccount("AccountsDAOSpec23")
    val friend1 = createAccount("AccountsDAOSpec24")
    val friend2 = createAccount("AccountsDAOSpec25")
    val blockingUser = createAccount("AccountsDAOSpec26")

    // account1 following user1
    execute(followingsDAO.create(user1.id, account1.id.toSessionId))
    execute(followersDAO.create(user1.id, account1.id.toSessionId))

    // user2, user3, user4 following account1
    execute(followingsDAO.create(account1.id, user2.id.toSessionId))
    execute(followingsDAO.create(account1.id, user3.id.toSessionId))
    execute(followingsDAO.create(account1.id, user4.id.toSessionId))

    execute(followersDAO.create(account1.id, user2.id.toSessionId))
    execute(followersDAO.create(account1.id, user3.id.toSessionId))
    execute(followersDAO.create(account1.id, user4.id.toSessionId))

    // user1, user2, account1 are friends
    execute(friendsDAO.create(account1.id, friend1.id.toSessionId))
    execute(friendsDAO.create(account1.id, friend2.id.toSessionId))
    execute(friendsDAO.create(friend1.id, account1.id.toSessionId))
    execute(friendsDAO.create(friend2.id, account1.id.toSessionId))

    // account1 followCount = 1, followerCount = 3, friendCount = 2

    // find account1 by session user
    val account1Result = execute(accountsDAO.find(account1.id, sessionAccount.id.toSessionId))
    assert(account1Result.isDefined == true)

    // following count, follower count, friend count
    assert(account1Result.get.accountName == account1.accountName)
    assert(account1Result.get.displayName == account1.displayName)
    assert(account1Result.get.id == account1.id)
    assert(account1Result.get.followingCount == Some(1))
    assert(account1Result.get.followerCount == Some(3))
    assert(account1Result.get.friendCount == Some(2))
    assert(account1Result.get.feedsCount == Some(0)) // TODO : Test me

    val user = account1Result.get
    assert(user.id == account1.id)
    assert(user.accountName == account1.accountName)
    assert(user.displayName == account1.displayName)
//    assert(user.accountStatus == account1.accountStatus)

    // find blocked user
    execute(blocksDAO.create(account1.id, blockingUser.id.toSessionId))
    assert(execute(accountsDAO.find(blockingUser.id, account1.id.toSessionId)).isEmpty)

  }

  test("find session user") {

    val sessionAccount = createAccount("AccountsDAOSpec27")

    val userList = execute(accountsDAO.find(sessionAccount.id.toSessionId))
    val user = userList.head
    assert(userList.size == 1)
    assert(user.id == sessionAccount.id)
    assert(user.accountName == sessionAccount.accountName)
    assert(user.displayName == sessionAccount.displayName)
    assert(user.accountStatus == sessionAccount.accountStatus)

    assert(execute(accountsDAO.find(SessionId(-1L))).isDefined == false)

  }

  test("findAll") {

    val sessionAccount = createAccount("AccountsDAOSpec28")
    val blockingUser = createAccount("AccountsDAOSpec29")
    val account1 = createAccount("AccountsDAOSpec30")
    val account2 = createAccount("AccountsDAOSpec31")
    val account3 = createAccount("AccountsDAOSpec32")
    val account4 = createAccount("AccountsDAOSpec33")
    val account5 = createAccount("AccountsDAOSpec34")

    execute(accountsDAO.updateAccountName("userName1", account1.id.toSessionId))
    execute(accountsDAO.updateAccountName("userName2", account2.id.toSessionId))
    execute(accountsDAO.updateAccountName("userName3", account3.id.toSessionId))
    execute(accountsDAO.updateAccountName("userName4", account4.id.toSessionId))
    execute(accountsDAO.updateAccountName("userName5", account5.id.toSessionId))

    // blocked user
    execute(blocksDAO.create(sessionAccount.id, blockingUser.id.toSessionId))

    // find all users topPage
    val result15 = execute(accountsDAO.findAll(Some("userName"), None, 0, 3, sessionAccount.id.toSessionId))
    assert(result15.size == 3)
    assert(result15(0).id == account1.id)
    assert(result15(1).id == account2.id)
    assert(result15(2).id == account3.id)

    val result16 = execute(accountsDAO.findAll(Some("userName"), result15(2).next, 0, 2, sessionAccount.id.toSessionId))
    assert(result16.size == 2)
    assert(result16(0).id == account4.id)
    assert(result16(1).id == account5.id)

  }


  test("signOut") {

    val sessionAccount = createAccount("AccountsDAOSpec35")

    execute(accountsDAO.signOut(sessionAccount.id.toSessionId))
    val result1 = execute(accountsDAO.find(sessionAccount.id.toSessionId))
    assert(result1.head.signedOutAt.isDefined == true)

  }

  test("findStatus") {

    val sessionAccount = createAccount("AccountsDAOSpec36")
    val result = execute(accountsDAO.findStatus(sessionAccount.id.toSessionId))
    assert(result.isDefined == true)
    val (accountStatus, signedOutAt) = result.get
    assert(accountStatus == AccountStatusType.normally)
    assert(signedOutAt.isEmpty == true)
  }

}

