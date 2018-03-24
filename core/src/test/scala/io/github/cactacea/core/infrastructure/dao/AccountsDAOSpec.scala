package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.inject.Logging
import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

class AccountsDAOSpec extends DAOSpec with Logging {

  val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]
  val blocksDAO: BlocksDAO = injector.instance[BlocksDAO]
  val blockersDAO: BlockersDAO = injector.instance[BlockersDAO]
  val followingDAO: FollowingDAO = injector.instance[FollowingDAO]
  val followersDAO: FollowersDAO = injector.instance[FollowersDAO]
  val friendsDAO: FriendsDAO = injector.instance[FriendsDAO]

  test("create") {

    val sessionAccount = createAccount("account0")

    val newUser = createAccount("account1")
    val accountId = Await.result(
      accountsDAO.create(newUser.accountName, newUser.displayName, newUser.password, newUser.web, newUser.birthday, newUser.location, newUser.bio)
    )

    val userList = this.selectAccounts(accountId, sessionAccount.id.toSessionId)
    val createdUser = userList.head._1

    assert(userList.size == 1)
    assert(createdUser.id == accountId)
    assert(createdUser.accountName == newUser.accountName)
    assert(createdUser.displayName == newUser.displayName)
    assert(createdUser.accountStatus == newUser.accountStatus)

  }

  test("exist by account name and password") {

    val sessionAccount = createAccount("account0")

    val result = Await.result(
      accountsDAO.find(
        sessionAccount.accountName,
        "password"
      )
    ).get

    assert(result.password == accountsDAO.createHashedPassword("password"))

  }

  test("exists by session id") {

    val sessionAccount = createAccount("account0")

    val result = Await.result(
      accountsDAO.find(sessionAccount.id.toSessionId)
    ).get

    assert(result.id == sessionAccount.id)

  }

  test("exists by account ids") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")
    val account2 = createAccount("account2")

    val result = Await.result(accountsDAO.exist(List(sessionAccount.id, account1.id, account2.id), sessionAccount.id.toSessionId))

    assert(result == true)

  }

  test("update password") {

    val sessionAccount = createAccount("account1")

    Await.result(
      accountsDAO.updatePassword(
        "password",
        "password2",
        sessionAccount.id.toSessionId
      )
    )

    val result = Await.result(accountsDAO.find(sessionAccount.id.toSessionId)).get
    assert(result.password == accountsDAO.createHashedPassword("password2"))

  }

  test("update account name") {

    val sessionAccount = createAccount("account1")

    val newUser = createAccount("account0")
    val accountId = this.insertAccounts(newUser)

    Await.result(
      accountsDAO.updateAccountName("accountName2", accountId.toSessionId)
    )

    val editedUserList = this.selectAccounts(accountId, sessionAccount.id.toSessionId)
    val editedUser = editedUserList.head._1

    assert(editedUserList.size == 1)
    assert(editedUser.id == accountId)
    assert(editedUser.accountName == "accountName2")

  }

  test("update other user's userName") {

    val sessionAccount = createAccount("account0")
    val account1 = createAccount("account1")
    val account2 = createAccount("account2")

    // update other user name
    Await.result(accountsDAO.updateDisplayName(account1.id, Some("newUserName1"), sessionAccount.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(account2.id, Some("newUserName2"), sessionAccount.id.toSessionId))

    val result1 = Await.result(accountsDAO.find(account1.id, sessionAccount.id.toSessionId))
    assert(result1.get._1.displayName == "newUserName1")
    val result2 = Await.result(accountsDAO.find(account2.id, sessionAccount.id.toSessionId))
    assert(result2.get._1.displayName == "newUserName2")

    Await.result(accountsDAO.updateDisplayName(account1.id, None, sessionAccount.id.toSessionId))
    Await.result(accountsDAO.updateDisplayName(account2.id, None, sessionAccount.id.toSessionId))

    val result3 = Await.result(accountsDAO.find(account1.id, sessionAccount.id.toSessionId))
    assert(result3.get._1.displayName == account1.displayName)
    val result4 = Await.result(accountsDAO.find(account2.id, sessionAccount.id.toSessionId))
    assert(result4.get._1.displayName == account2.displayName)

  }

  test("exist") {

    val sessionAccount = createAccount("account0")
    val noBlockedUser = createAccount("account1")
    val blockedAccount1 = createAccount("account2")
    val blockedAccount2 = createAccount("account3")

    val result1 = Await.result(accountsDAO.exist(blockedAccount1.id, sessionAccount.id.toSessionId))
    val result2 = Await.result(accountsDAO.exist(blockedAccount1.id, sessionAccount.id.toSessionId))
    assert(result1 == true)
    assert(result2 == true)

    Await.result(blocksDAO.create(blockedAccount1.id, sessionAccount.id.toSessionId))
    Await.result(blocksDAO.create(blockedAccount2.id, sessionAccount.id.toSessionId))

    assert(Await.result(accountsDAO.exist(blockedAccount1.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(accountsDAO.exist(blockedAccount2.id, sessionAccount.id.toSessionId)) == false)
    assert(Await.result(accountsDAO.exist(noBlockedUser.id, sessionAccount.id.toSessionId)) == true)

  }

  test("find") {

    val account1 = createAccount("account1")
    val sessionAccount = createAccount("account2")
    val user1 = createAccount("account3")
    val user2 = createAccount("account5")
    val user3 = createAccount("account6")
    val user4 = createAccount("account7")
    val friend1 = createAccount("account8")
    val friend2 = createAccount("account9")
    val blockedUser = createAccount("account10")

    // account1 follow user1
    Await.result(followingDAO.create(user1.id, account1.id.toSessionId))
    Await.result(followersDAO.create(user1.id, account1.id.toSessionId))

    // user2, user3, user4 follow account1
    Await.result(followingDAO.create(account1.id, user2.id.toSessionId))
    Await.result(followingDAO.create(account1.id, user3.id.toSessionId))
    Await.result(followingDAO.create(account1.id, user4.id.toSessionId))

    Await.result(followersDAO.create(account1.id, user2.id.toSessionId))
    Await.result(followersDAO.create(account1.id, user3.id.toSessionId))
    Await.result(followersDAO.create(account1.id, user4.id.toSessionId))

    // user1, user2, account1 are friends
    Await.result(friendsDAO.create(account1.id, friend1.id.toSessionId))
    Await.result(friendsDAO.create(account1.id, friend2.id.toSessionId))
    Await.result(friendsDAO.create(friend1.id, account1.id.toSessionId))
    Await.result(friendsDAO.create(friend2.id, account1.id.toSessionId))

    // account1 followCount = 1, followerCount = 3, friendCount = 2

    // find account1 by session user
    val account1Result = Await.result(accountsDAO.find(account1.id, sessionAccount.id.toSessionId))
    assert(account1Result.isDefined == true)

    // follow count, follower count, friend count
    assert(account1Result.get._1.accountName == account1.accountName)
    assert(account1Result.get._1.displayName == account1.displayName)
    assert(account1Result.get._1.id == account1.id)
    assert(account1Result.get._1.followCount == 1)
    assert(account1Result.get._1.followerCount == 3)
    assert(account1Result.get._1.friendCount == 2)

    val user = account1Result.get._1
    assert(user.id == account1.id)
    assert(user.accountName == account1.accountName)
    assert(user.displayName == account1.displayName)
    assert(user.accountStatus == account1.accountStatus)

    // find blocked user
    Await.result(blocksDAO.create(account1.id, blockedUser.id.toSessionId))
    Await.result(blockersDAO.create(blockedUser.id, account1.id.toSessionId))
    assert(Await.result(accountsDAO.find(blockedUser.id, account1.id.toSessionId)).isDefined == false)

  }

  test("find session user") {

    val sessionAccount = createAccount("account0")

    val userList = Await.result(accountsDAO.find(sessionAccount.id.toSessionId))
    val user = userList.head
    assert(userList.size == 1)
    assert(user.id == sessionAccount.id)
    assert(user.accountName == sessionAccount.accountName)
    assert(user.displayName == sessionAccount.displayName)
    assert(user.accountStatus == sessionAccount.accountStatus)

    assert(Await.result(accountsDAO.find(SessionId(-1L))).isDefined == false)

  }

  test("findAll") {

    val sessionAccount = createAccount("account0")
    val blockedUser = createAccount("account1")
    val account1 = createAccount("account2")
    val account2 = createAccount("account3")
    val account3 = createAccount("account4")
    val account4 = createAccount("account5")
    val account5 = createAccount("account6")

    Await.result(accountsDAO.updateAccountName("userName1", account1.id.toSessionId))
    Await.result(accountsDAO.updateAccountName("userName2", account2.id.toSessionId))
    Await.result(accountsDAO.updateAccountName("userName3", account3.id.toSessionId))
    Await.result(accountsDAO.updateAccountName("userName4", account4.id.toSessionId))
    Await.result(accountsDAO.updateAccountName("userName5", account5.id.toSessionId))

    // blocked user
    Await.result(blocksDAO.create(sessionAccount.id, blockedUser.id.toSessionId))
    Await.result(blockersDAO.create(blockedUser.id, sessionAccount.id.toSessionId))

    // find by userName
    val result1 = Await.result(accountsDAO.findAll(Some("userName1"), None, None, Some(5), sessionAccount.id.toSessionId))
    assert(result1.size == 1)

    // find all users topPage
    val result15 = Await.result(accountsDAO.findAll(None, None, None, Some(3), sessionAccount.id.toSessionId))
    assert(result15.size == 3)
    val resultAccount1 = result15(0)._1
    val resultAccount2 = result15(1)._1
    val resultAccount3 = result15(2)._1
    assert(resultAccount1.id == account5.id)
    assert(resultAccount2.id == account4.id)
    assert(resultAccount3.id == account3.id)

    val result16 = Await.result(accountsDAO.findAll(None, Some(resultAccount3.id.value), None, Some(3), sessionAccount.id.toSessionId))
    assert(result16.size == 2)
    val resultAccount4 = result16(0)._1
    val resultAccount5 = result16(1)._1
    assert(resultAccount4.id == account2.id)
    assert(resultAccount5.id == account1.id)

  }


  test("signOut") {

    val sessionAccount = createAccount("account0")

    Await.result(accountsDAO.signOut(sessionAccount.id.toSessionId))
    val result1 = Await.result(accountsDAO.find(sessionAccount.id.toSessionId))
    assert(result1.head.signedOutAt.isDefined == true)

  }

  test("findStatus") {

    val sessionAccount = createAccount("account0")
    val result = Await.result(accountsDAO.findStatus(sessionAccount.id.toSessionId))
    assert(result.isDefined == true)
    val (accountStatus, signedOutAt) = result.get
    assert(accountStatus == AccountStatusType.normally)
    assert(signedOutAt.isEmpty == true)
  }

}

